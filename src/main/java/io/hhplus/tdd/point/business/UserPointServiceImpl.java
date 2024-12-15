package io.hhplus.tdd.point.business;

import java.util.Objects;

import org.springframework.stereotype.Service;

import io.hhplus.tdd.point.dto.UserPointSelectDTO;
import io.hhplus.tdd.point.exception.InvalidUserIdException;
import io.hhplus.tdd.point.exception.UserNotFoundException;
import io.hhplus.tdd.point.infrastructure.database.UserPoint;
import io.hhplus.tdd.point.infrastructure.database.UserPointRepository;

@Service
public class UserPointServiceImpl implements UserPointService {

	private final UserPointRepository userPointRepository;

	public UserPointServiceImpl(final UserPointRepository userPointRepository) {
		this.userPointRepository = userPointRepository;
	}

	@Override
	public UserPointSelectDTO findById(final long id) {
		if (id < 0 || id == 0) {
			throw new InvalidUserIdException();
		}

		final UserPoint userPoint = userPointRepository.selectById(id);
		if (Objects.equals(userPoint, UserPoint.empty(id))) {
			throw new UserNotFoundException();
		}

		return new UserPointSelectDTO(userPoint.id(), userPoint.point());
	}
}
