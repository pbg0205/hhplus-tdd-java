package io.hhplus.tdd.point.business;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.hhplus.tdd.point.dto.UserPointSelectDTO;
import io.hhplus.tdd.point.exception.InvalidUserIdException;
import io.hhplus.tdd.point.exception.UserNotFoundException;
import io.hhplus.tdd.point.infrastructure.database.UserPoint;
import io.hhplus.tdd.point.infrastructure.database.UserPointRepository;
import io.hhplus.tdd.user.infrastructure.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserPointServiceTest {

	private UserPointService userPointService;

	@Mock
	private UserPointRepository userPointRepository;

	@Mock
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		this.userPointService = new UserPointServiceImpl(userPointRepository, userRepository);
	}

	@DisplayName("[포인트 조회 실패] 사용자 식별자가 음수라면 예외를 반환한다")
	@ParameterizedTest
	@ValueSource(longs = {-1L, -100L, -500_000_000_00L})
	void failSelectingUserPointBecauseOfUserIdNegative(long userId) {
		// given, when, then
		assertThatThrownBy(() -> userPointService.findById(userId)).isInstanceOf(InvalidUserIdException.class);
	}

	@DisplayName("[포인트 조회 실패] 사용자 식별자는 0이라면 예외를 반환한다")
	@Test
	void failSelectingUserPointBecauseOfUserIdZero() {
		// given
		final long userId = 0L;

		// when, then
		assertThatThrownBy(() -> userPointService.findById(userId)).isInstanceOf(InvalidUserIdException.class);
	}

	@DisplayName("[포인트 조회 실패] 미등록 사용자 식별자를 입력하면 예외를 반환한다")
	@Test
	void failSelectingUserPointBecauseOfUnRegisteredUser() {
		// given
		final long unRegisteredUserId = 1L;

		when(userRepository.exists(unRegisteredUserId)).thenReturn(false);

		// when, then
		assertThatThrownBy(() -> userPointService.findById(unRegisteredUserId))
			.isInstanceOf(UserNotFoundException.class);
	}

	@DisplayName("[포인트 조회 성공] 양수이며 등록된 사용자 식별자를 입력하면 현재 회원 포인트를 반환한다")
	@Test
	void successSelectingUserPoint() {
		// given
		final long registeredUserId = 1L;
		final long point = 1_000L;

		when(userRepository.exists(registeredUserId)).thenReturn(true);
		when(userPointRepository.selectById(registeredUserId))
			.thenReturn(new UserPoint(registeredUserId, point, System.currentTimeMillis()));

		// when
		final UserPointSelectDTO userPointSelectDTO = userPointService.findById(registeredUserId);

		// then
		assertThat(userPointSelectDTO).isEqualTo(new UserPointSelectDTO(registeredUserId, point));
	}
}
