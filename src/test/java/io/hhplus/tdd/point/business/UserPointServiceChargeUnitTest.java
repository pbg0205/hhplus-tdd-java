package io.hhplus.tdd.point.business;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.hhplus.tdd.point.business.dto.UserPointSelectDTO;
import io.hhplus.tdd.point.exception.InvalidChargingPointException;
import io.hhplus.tdd.point.exception.InvalidUserIdException;
import io.hhplus.tdd.point.exception.UserNotFoundException;
import io.hhplus.tdd.point.infrastructure.database.UserPoint;
import io.hhplus.tdd.point.infrastructure.database.UserPointRepository;
import io.hhplus.tdd.user.infrastructure.UserRepository;

@DisplayName("포인트 충전 서비스 단위 테스트")
@ExtendWith(MockitoExtension.class)
class UserPointServiceChargeUnitTest {

	private UserPointService userPointService;

	@Mock
	private UserPointRepository userPointRepository;

	@Mock
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		this.userPointService = new UserPointServiceImpl(userPointRepository, userRepository);
	}

	@DisplayName("[실패] 충전 포인트가 음수인 경우, 예외를 반환한다")
	@Test
	void failChargingPointBecauseOfChargingPointNegative() {
		// given
		final long userId = 1L;
		final long chargingPoint = -1_000L;

		// when, then
		assertThatThrownBy(() -> userPointService.charge(userId, chargingPoint))
			.isInstanceOf(InvalidChargingPointException.class);
	}

	@DisplayName("[실패] 충전 포인트가 0인 경우, 예외를 반환한다")
	@Test
	void failChargingPointBecauseOfChargingPointZero() {
		// given
		final long userId = 1L;
		final long chargingPoint = 0L;

		// when, then
		assertThatThrownBy(() -> userPointService.charge(userId, chargingPoint))
			.isInstanceOf(InvalidChargingPointException.class);
	}

	@DisplayName("[실패] 충전 포인트가 10만점을 초과하는 경우, 예외를 반환한다")
	@Test
	void failChargingPointBecauseOfMaxChargingPoint() {
		// given
		final long userId = 1L;
		final long chargingPoint = 100_001L;

		// when, then
		assertThatThrownBy(() -> userPointService.charge(userId, chargingPoint))
			.isInstanceOf(InvalidChargingPointException.class);
	}

	@DisplayName("[실패] 사용자 식별자가 음수인 경우, 예외를 반환한다")
	@Test
	void failChargingPointBecauseOfUserIdNegative() {
		// given
		final long userId = -1L;
		final long chargingPoint = 1L;

		// when, then
		assertThatThrownBy(() -> userPointService.charge(userId, chargingPoint))
			.isInstanceOf(InvalidUserIdException.class);
	}

	@DisplayName("[실패] 사용자 식별자가 음수인 경우, 예외를 반환한다")
	@Test
	void failChargingPointBecauseOfUserIdZero() {
		// given
		final long userId = 0L;
		final long chargingPoint = 1L;

		// when, then
		assertThatThrownBy(() -> userPointService.charge(userId, chargingPoint))
			.isInstanceOf(InvalidUserIdException.class);
	}

	@DisplayName("[실패] 미등록 사용자 식별자인 경우, 예외를 반환한다")
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

	@DisplayName("[성공] 등록 사용자 식별자이고 단일 최대 충전 포인트를 초과하지 않은 양수의 충전 포인트인 경우, 정상 충전된다.")
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
