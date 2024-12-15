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

@ExtendWith(MockitoExtension.class)
class UserPointServiceTest {

	private UserPointService userPointService;

	@Mock
	private UserPointRepository userPointRepository;

	@BeforeEach
	void setUp() {
		this.userPointService = new UserPointServiceImpl(userPointRepository);
	}

	@DisplayName("[fail] 사용자 식별자가 음수라면 예외를 반환한다")
	@ParameterizedTest
	@ValueSource(longs = {-1L, -100L, -500_000_000_00L})
	void failBecauseOfUserIdNegative(long userId) {
		// given, when, then
		assertThatThrownBy(() -> userPointService.findById(userId)).isInstanceOf(InvalidUserIdException.class);
	}

	@DisplayName("[fail] 사용자 식별자는 0이라면 예외를 반환한다")
	@Test
	void failBecauseOfUserIdZero() {
		// given
		final long userId = 0L;

		// when, then
		assertThatThrownBy(() -> userPointService.findById(userId)).isInstanceOf(InvalidUserIdException.class);
	}

	@DisplayName("[fail] 미등록 사용자 식별자를 입력하면 예외를 반환한다")
	@Test
	void failBecauseOfUnRegisteredUser() {
		// given
		final long unRegisteredUserId = 1L;

		when(userPointRepository.selectById(unRegisteredUserId)).thenReturn(UserPoint.empty(unRegisteredUserId));

		// when, then
		assertThatThrownBy(() -> userPointService.findById(unRegisteredUserId))
			.isInstanceOf(UserNotFoundException.class);
	}

	@DisplayName("[success] 양수이며 등록된 사용자 식별자를 입력하면 현재 회원 포인트를 반환한다")
	@Test
	void successSelectingUserPoint() {
		// given
		final long registeredUserId = 1L;
		final long point = 1_000L;

		when(userPointRepository.selectById(registeredUserId))
			.thenReturn(new UserPoint(registeredUserId, point, System.currentTimeMillis()));

		// when
		final UserPointSelectDTO userPointSelectDTO = userPointService.findById(registeredUserId);

		// then
		assertThat(userPointSelectDTO).isEqualTo(new UserPointSelectDTO(registeredUserId, point));
	}
}
