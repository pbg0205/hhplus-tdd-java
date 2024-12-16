package io.hhplus.tdd.point.business;

import org.springframework.stereotype.Service;

import io.hhplus.tdd.common.api.support.error.ErrorType;
import io.hhplus.tdd.point.business.dto.UserPointSelectDTO;
import io.hhplus.tdd.point.exception.InvalidChargingPointException;
import io.hhplus.tdd.point.exception.InvalidUserIdException;
import io.hhplus.tdd.point.exception.UserNotFoundException;
import io.hhplus.tdd.point.infrastructure.database.UserPoint;
import io.hhplus.tdd.point.infrastructure.database.UserPointRepository;
import io.hhplus.tdd.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPointServiceImpl implements UserPointService {

	private static final long MAX_CHARGING_POINT = 100_000L;

	private final UserPointRepository userPointRepository;
	private final UserRepository userRepository;

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
		if (amount < 0 || amount == 0 || amount > MAX_CHARGING_POINT) {
			throw new InvalidChargingPointException(ErrorType.CHARGING_POINT01);
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
			return new UserPointSelectDTO(updatedUserPoint.id(), totalAmount);
		}
	}
}
