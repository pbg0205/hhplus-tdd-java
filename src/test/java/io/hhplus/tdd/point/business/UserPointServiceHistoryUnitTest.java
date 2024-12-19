package io.hhplus.tdd.point.business;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.hhplus.tdd.point.business.dto.PointHistorySelectDTO;
import io.hhplus.tdd.point.exception.InvalidUserIdException;
import io.hhplus.tdd.point.exception.UserNotFoundException;
import io.hhplus.tdd.point.infrastructure.database.PointHistory;
import io.hhplus.tdd.point.infrastructure.database.PointHistoryRepository;
import io.hhplus.tdd.point.infrastructure.database.TransactionType;
import io.hhplus.tdd.point.infrastructure.database.UserPointRepository;
import io.hhplus.tdd.user.infrastructure.UserRepository;

@DisplayName("포인트 히스토리 서비스 단위 테스트")
@ExtendWith(MockitoExtension.class)
class UserPointServiceHistoryUnitTest {

	private UserPointService userPointService;

	@Mock
	private UserPointRepository userPointRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PointHistoryRepository pointHistoryRepository;

	@BeforeEach
	void setUp() {
		this.userPointService = new UserPointServiceImpl(userPointRepository, userRepository, pointHistoryRepository);
	}

	@DisplayName("[실패] 사용자 식별자가 음수인 경우, 예외를 반환한다")
	@Test
	void failSelectPointHistoryBecauseOfUserIdNegative() {
		// given
		long userId = -1L;

		// when, then
		assertThatThrownBy(() -> userPointService.findPointHistoryByUserId(userId)).isInstanceOf(
			InvalidUserIdException.class);
	}

	@DisplayName("[실패] 사용자 식별자는 0인 경우, 예외를 반환한다")
	@Test
	void failSelectingUserHistoryBecauseOfUserIdZero() {
		// given
		long userId = 0L;

		// when, then
		assertThatThrownBy(() -> userPointService.findPointHistoryByUserId(userId))
			.isInstanceOf(InvalidUserIdException.class);
	}

	@DisplayName("[실패] 미등록 사용자 식별자인 경우, 예외를 반환한다")
	@Test
	void failSelectingUserHistoryBecauseOfUnRegisteredUser() {
		// given
		long unRegisteredUserId = 1L;

		when(userRepository.exists(anyLong())).thenReturn(false);

		// when, then
		assertThatThrownBy(() -> userPointService.findPointHistoryByUserId(unRegisteredUserId))
			.isInstanceOf(UserNotFoundException.class);
	}

	@DisplayName("[성공] 양수이며 등록된 사용자 식별자인 경우, 사용자의 포인트 히스토리를 반환한다")
	@Test
	void successSelectingUserPoint() {
		// given
		long registeredUserId = 1L;

		when(userRepository.exists(anyLong())).thenReturn(true);
		when(pointHistoryRepository.selectAllByUserId(anyLong()))
			.thenReturn(
				List.of(
					new PointHistory(1L, registeredUserId, 10000L, TransactionType.CHARGE, 1000L),
					new PointHistory(1L, registeredUserId, 2000L, TransactionType.USE, 1000L),
					new PointHistory(1L, registeredUserId, 1000L, TransactionType.USE, 1000L),
					new PointHistory(1L, registeredUserId, 1000L, TransactionType.USE, 1000L)
				)
			);

		// when
		List<PointHistorySelectDTO> pointHistoryList = userPointService.findPointHistoryByUserId(registeredUserId);

		// then
		assertSoftly(softAssertions -> {
			softAssertions.assertThat(pointHistoryList).size().isEqualTo(4);
			softAssertions.assertThat(pointHistoryList)
				.allSatisfy(dto -> softAssertions.assertThat(dto.userId()).isEqualTo(registeredUserId));
		});
	}
}
