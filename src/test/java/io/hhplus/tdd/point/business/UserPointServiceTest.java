package io.hhplus.tdd.point.business;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
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
import io.hhplus.tdd.point.exception.InvalidChargingPointException;
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

	@DisplayName("[포인트 충전 실패] 충전 포인트가 음수라면 예외를 반환한다")
	@Test
	void failChargingPointBecauseOfChargingPointNegative() {
		// given
		final long userId = 1L;
		final long chargingPoint = -1_000L;

		// when, then
		assertThatThrownBy(() -> userPointService.charge(userId, chargingPoint))
			.isInstanceOf(InvalidChargingPointException.class);
	}

	@DisplayName("[포인트 충전 실패] 충전 포인트가 0이라면 예외를 반환한다")
	@Test
	void failChargingPointBecauseOfChargingPointZero() {
		// given
		final long userId = 1L;
		final long chargingPoint = 0L;

		// when, then
		assertThatThrownBy(() -> userPointService.charge(userId, chargingPoint))
			.isInstanceOf(InvalidChargingPointException.class);
	}

	@DisplayName("[포인트 충전 실패] 충전 포인트가 10만점을 넘으면 예외를 반환한다")
	@Test
	void failChargingPointBecauseOfMaxChargingPoint() {
		// given
		final long userId = 1L;
		final long chargingPoint = 100_001L;

		// when, then
		assertThatThrownBy(() -> userPointService.charge(userId, chargingPoint))
			.isInstanceOf(InvalidChargingPointException.class);
	}

	@DisplayName("[포인트 충전 실패] 사용자 식별자가 음수라면 예외를 반환한다")
	@Test
	void failChargingPointBecauseOfUserIdNegative() {
		// given
		final long userId = -1L;
		final long chargingPoint = 1L;

		// when, then
		assertThatThrownBy(() -> userPointService.charge(userId, chargingPoint))
			.isInstanceOf(InvalidUserIdException.class);
	}

	@DisplayName("[포인트 충전 실패] 사용자 식별자가 음수라면 예외를 반환한다")
	@Test
	void failChargingPointBecauseOfUserIdZero() {
		// given
		final long userId = 0L;
		final long chargingPoint = 1L;

		// when, then
		assertThatThrownBy(() -> userPointService.charge(userId, chargingPoint))
			.isInstanceOf(InvalidUserIdException.class);
	}

	@DisplayName("[포인트 충전 실패] 미등록 사용자 식별자인 경우 예외를 반환한다")
	@Test
	void failChargingPointBecauseOfInvalidUserId() {
		// given
		final long userId = 1L;
		final long chargingPoint = 1L;

		when(userRepository.exists(userId)).thenReturn(false);

		// when, then
		assertThatThrownBy(() -> userPointService.charge(userId, chargingPoint))
			.isInstanceOf(UserNotFoundException.class);
	}

	@DisplayName("[포인트 충전 성공] 특정 사용자의 포인트가 정상 충전된다.")
	@Test
	void successChargingPoint() {
		// given
		final long userId = 1L;
		final long chargingPoint = 1_000L;
		final long currentPoint = 1_000L;

		when(userRepository.exists(userId)).thenReturn(true);
		when(userPointRepository.selectById(userId))
			.thenReturn(new UserPoint(userId, currentPoint, System.currentTimeMillis()));
		when(userPointRepository.insertOrUpdate(anyLong(), anyLong()))
			.thenReturn(new UserPoint(userId, chargingPoint, System.currentTimeMillis()));

		// when, then
		assertThat(userPointService.charge(userId, chargingPoint))
			.isEqualTo(new UserPointSelectDTO(userId, chargingPoint));
	}
}
