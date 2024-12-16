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
import io.hhplus.tdd.point.exception.InvalidPointSpendException;
import io.hhplus.tdd.point.exception.InvalidUserIdException;
import io.hhplus.tdd.point.exception.UserNotFoundException;
import io.hhplus.tdd.point.infrastructure.database.UserPoint;
import io.hhplus.tdd.point.infrastructure.database.UserPointRepository;
import io.hhplus.tdd.user.infrastructure.UserRepository;

@DisplayName("포인트 사용 서비스 단위 테스트")
@ExtendWith(MockitoExtension.class)
class UserPointServiceSpendingUnitTest {

	private UserPointService userPointService;

	@Mock
	private UserPointRepository userPointRepository;

	@Mock
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		this.userPointService = new UserPointServiceImpl(userPointRepository, userRepository);
	}

	@DisplayName("[실패] 사용 포인트가 음수인 경우, 예외를 반환한다")
	@Test
	void failPointSpendBecauseOfUsingPointNegative() {
		// given
		final long userId = 1L;
		final long usingPoint = -1_000L;

		// when, then
		assertThatThrownBy(() -> userPointService.spend(userId, usingPoint))
			.isInstanceOf(InvalidPointSpendException.class);
	}

	@DisplayName("[실패] 사용 포인트가 0인 경우, 예외를 반환한다")
	@Test
	void failPointSpendingBecauseOfUsingPointZero() {
		// given
		final long userId = 1L;
		final long usingPoint = 0L;

		// when, then
		assertThatThrownBy(() -> userPointService.spend(userId, usingPoint))
			.isInstanceOf(InvalidPointSpendException.class);
	}

	@DisplayName("[실패] 사용 포인트가 1회 사용 최대 포인트인 만점(10_000)을 초과하는 경우, 예외를 반환한다")
	@Test
	void failPointSpendingBecauseOfMaxUsingPoint() {
		// given
		final long userId = 1L;
		final long usingPoint = 10_001L;

		// when, then
		assertThatThrownBy(() -> userPointService.spend(userId, usingPoint))
			.isInstanceOf(InvalidPointSpendException.class);
	}

	@DisplayName("[실패] 사용자 식별자가 음수인 경우, 예외를 반환한다")
	@Test
	void failPointSpendingBecauseOfUserIdNegative() {
		// given
		final long userId = -1L;
		final long usingPoint = 1L;

		// when, then
		assertThatThrownBy(() -> userPointService.spend(userId, usingPoint))
			.isInstanceOf(InvalidUserIdException.class);
	}

	@DisplayName("[실패] 사용자 식별자가 0인 경우, 예외를 반환한다")
	@Test
	void failPointSpendingBecauseOfUserIdZero() {
		// given
		final long userId = 0L;
		final long usingPoint = 1L;

		// when, then
		assertThatThrownBy(() -> userPointService.spend(userId, usingPoint))
			.isInstanceOf(InvalidUserIdException.class);
	}

	@DisplayName("[실패] 미등록 사용자 식별자인 경우, 예외를 반환한다")
	@Test
	void failPointSpendingBecauseOfInvalidUserId() {
		// given
		final long userId = 1L;
		final long usingPoint = 1L;

		when(userRepository.exists(anyLong())).thenReturn(false);

		// when, then
		assertThatThrownBy(() -> userPointService.charge(userId, usingPoint))
			.isInstanceOf(UserNotFoundException.class);
	}

	@DisplayName("[실패] 사용 요청 포인트가 사용자 포인트보다 많은 경우, 예외를 반환한다")
	@Test
	void failPointSpendingBecauseOfValueGreaterThanUserPoint() {
		// given
		final long userId = 1L;
		final long currentPoint = 100L;
		final long amount = 1_000L;

		when(userRepository.exists(anyLong())).thenReturn(true);
		when(userPointRepository.selectById(anyLong()))
			.thenReturn(new UserPoint(userId, currentPoint, System.currentTimeMillis()));

		// when, then
		assertThatThrownBy(() -> userPointService.spend(userId, amount))
			.isInstanceOf(InvalidPointSpendException.class);
	}

	@DisplayName("[성공] 등록 사용자 식별자이고 1일 최대 사용 포인트 이하의 양수인 포인트인 경우, 정상 사용된다.")
	@Test
	void successPointSpending() {
		// given
		final long userId = 1L;
		final long currentPoint = 1_000L;
		final long amount = 1_000L;

		when(userRepository.exists(anyLong())).thenReturn(true);
		when(userPointRepository.selectById(anyLong()))
			.thenReturn(new UserPoint(userId, currentPoint, System.currentTimeMillis()));
		when(userPointRepository.insertOrUpdate(anyLong(), anyLong()))
			.thenReturn(new UserPoint(userId, currentPoint - amount, System.currentTimeMillis()));

		// when, then
		assertThat(userPointService.spend(userId, amount))
			.isEqualTo(new UserPointSelectDTO(userId, 0L));
	}
}
