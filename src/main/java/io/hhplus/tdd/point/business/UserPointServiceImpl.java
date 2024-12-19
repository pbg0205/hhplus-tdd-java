package io.hhplus.tdd.point.business;

import java.util.List;

import org.springframework.stereotype.Service;

import io.hhplus.tdd.common.api.support.error.ErrorType;
import io.hhplus.tdd.point.business.dto.PointHistorySelectDTO;
import io.hhplus.tdd.point.business.dto.UserPointSelectDTO;
import io.hhplus.tdd.point.exception.InvalidChargingPointException;
import io.hhplus.tdd.point.exception.InvalidPointSpendException;
import io.hhplus.tdd.point.exception.InvalidUserIdException;
import io.hhplus.tdd.point.exception.UserNotFoundException;
import io.hhplus.tdd.point.infrastructure.database.PointHistory;
import io.hhplus.tdd.point.infrastructure.database.PointHistoryRepository;
import io.hhplus.tdd.point.infrastructure.database.TransactionType;
import io.hhplus.tdd.point.infrastructure.database.UserPoint;
import io.hhplus.tdd.point.infrastructure.database.UserPointRepository;
import io.hhplus.tdd.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPointServiceImpl implements UserPointService {

	private static final long MAX_CHARGING_POINT = 100_000L;
	private static final long MAX_SPENDING_POINT = 10_000L;

	private final UserPointRepository userPointRepository;
	private final UserRepository userRepository;
	private final PointHistoryRepository pointHistoryRepository;

	@Override
	public UserPointSelectDTO findById(final long id) {
		if (id < 0 || id == 0) {
			throw new InvalidUserIdException();
		}

		if (!userRepository.exists(id)) {
			throw new UserNotFoundException();
		}

		final UserPoint userPoint = userPointRepository.selectById(id);
		return new UserPointSelectDTO(userPoint.id(), userPoint.point());
	}

	@Override
	public UserPointSelectDTO charge(final long id, final long amount) {
		if (amount < 0) {
			throw new InvalidChargingPointException(ErrorType.CHARGING_POINT_NEGATIVE);
		}

		if (amount == 0) {
			throw new InvalidChargingPointException(ErrorType.CHARGING_POINT_ZERO);
		}

		if (amount > MAX_CHARGING_POINT) {
			throw new InvalidChargingPointException(ErrorType.CHARGING_POINT_MAX);
		}

		if (id < 0 || id == 0) {
			throw new InvalidUserIdException();
		}

		if (!userRepository.exists(id)) {
			throw new UserNotFoundException();
		}

		synchronized (this) {
			final UserPoint userPoint = userPointRepository.selectById(id);

			long totalAmount = amount + userPoint.point();

			final UserPoint updatedUserPoint = userPointRepository.insertOrUpdate(id, totalAmount);
			pointHistoryRepository.insert(userPoint.id(), amount, TransactionType.CHARGE, System.currentTimeMillis());
			return new UserPointSelectDTO(updatedUserPoint.id(), totalAmount);
		}
	}

	@Override
	public UserPointSelectDTO spend(final long id, final long amount) {
		if (amount < 0) {
			throw new InvalidPointSpendException(ErrorType.SPENDING_POINT_NEGATIVE);
		}

		if (amount == 0) {
			throw new InvalidPointSpendException(ErrorType.SPENDING_POINT_ZERO);
		}

		if (amount > MAX_SPENDING_POINT) {
			throw new InvalidPointSpendException(ErrorType.SPENDING_POINT_MAX);
		}

		if (id < 0 || id == 0) {
			throw new InvalidUserIdException();
		}

		if (!userRepository.exists(id)) {
			throw new UserNotFoundException();
		}

		synchronized (this) {
			final UserPoint userPoint = userPointRepository.selectById(id);
			final long remainingPoints = userPoint.point() - amount;

			if (remainingPoints < 0) {
				throw new InvalidPointSpendException(ErrorType.SPENDING_POINT_NEGATIVE);
			}

			userPointRepository.insertOrUpdate(id, remainingPoints);
			pointHistoryRepository.insert(userPoint.id(), amount, TransactionType.USE, System.currentTimeMillis());
			return new UserPointSelectDTO(userPoint.id(), remainingPoints);
		}
	}

	@Override
	public List<PointHistorySelectDTO> findPointHistoryByUserId(final long userId) {
		if (userId < 0) {
			throw new InvalidUserIdException();
		}

		if (userId == 0) {
			throw new InvalidUserIdException();
		}

		if (!userRepository.exists(userId)) {
			throw new UserNotFoundException();
		}

		final List<PointHistory> pointHistoryList = pointHistoryRepository.selectAllByUserId(userId);

		return pointHistoryList.stream()
			.map(pointHistory -> new PointHistorySelectDTO(
				pointHistory.id(),
				pointHistory.userId(),
				pointHistory.amount(),
				pointHistory.type(),
				pointHistory.updateMillis())
			).toList();
	}
}
