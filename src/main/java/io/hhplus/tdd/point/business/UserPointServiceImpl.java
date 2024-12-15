package io.hhplus.tdd.point.business;

import org.springframework.stereotype.Service;

import io.hhplus.tdd.point.dto.UserPointSelectDTO;
import io.hhplus.tdd.point.exception.InvalidUserIdException;
import io.hhplus.tdd.point.exception.UserNotFoundException;
import io.hhplus.tdd.point.infrastructure.database.UserPoint;
import io.hhplus.tdd.point.infrastructure.database.UserPointRepository;
import io.hhplus.tdd.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPointServiceImpl implements UserPointService {

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
}
