package io.hhplus.tdd.point.presentation;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import io.hhplus.tdd.common.api.support.error.ErrorType;
import io.hhplus.tdd.point.business.UserPointService;
import io.hhplus.tdd.point.business.dto.UserPointSelectDTO;
import io.hhplus.tdd.point.exception.InvalidPointSpendException;
import io.hhplus.tdd.point.exception.InvalidUserIdException;
import io.hhplus.tdd.point.exception.UserNotFoundException;

@DisplayName("포인트 소비 컨트롤러 단위 테스트")
@WebMvcTest(PointController.class)
class PointControllerUseTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserPointService userPointService;

	@DisplayName("[실패] 사용 포인트가 음수인 경우, bad request 를 반환한다")
	@Test
	void failSpendingBecauseOfSpendingPointNegative() throws Exception {
		// given
		long userId = 1L;
		long amount = -1L;

		when(userPointService.spend(anyLong(), anyLong()))
			.thenThrow(new InvalidPointSpendException(ErrorType.SPENDING_POINT_NEGATIVE));

		// when
		final ResultActions result = mockMvc.perform(patch("/point/{id}/use", userId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(amount)));

		// then
		result.andExpectAll(
			status().isBadRequest(),
			jsonPath("$.code").value("POINT_USE01"),
			jsonPath("$.message").exists()
		);
	}

	@DisplayName("[실패] 사용 포인트가 0이면, bad request 를 반환한다")
	@Test
	void failSpendingBecauseOfSpendingPointZero() throws Exception {
		// given
		long userId = 1L;
		long amount = 0L;

		when(userPointService.spend(anyLong(), anyLong()))
			.thenThrow(new InvalidPointSpendException(ErrorType.SPENDING_POINT_ZERO));

		// when
		final ResultActions result = mockMvc.perform(patch("/point/{id}/use", userId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(amount)));

		// then
		result.andExpectAll(
			status().isBadRequest(),
			jsonPath("$.code").value("POINT_USE02"),
			jsonPath("$.message").exists()
		);
	}

	@DisplayName("[실패] 사용 포인트가 1회 사용 최대 포인트를 초과하는 경우, bad request 를 반환한다")
	@Test
	void failBecauseOfMaxSpendingPoint() throws Exception {
		// given
		long userId = 1L;
		long amount = 100_001L; // 단일 충전 최대 포인트: 100_000

		when(userPointService.spend(anyLong(), anyLong()))
			.thenThrow(new InvalidPointSpendException(ErrorType.SPENDING_POINT_MAX));

		// when
		final ResultActions result = mockMvc.perform(patch("/point/{id}/use", userId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(amount)));

		// then
		result.andExpectAll(
			status().isBadRequest(),
			jsonPath("$.code").value("POINT_USE03"),
			jsonPath("$.message").exists()
		);
	}

	@DisplayName("[실패] 유저 식별자가 음수인 경우, bad request 를 반환한다")
	@Test
	void failSpendingBecauseOfUserIdNegative() throws Exception {
		// given
		long userId = -1L;
		long amount = 1L;

		when(userPointService.spend(anyLong(), anyLong()))
			.thenThrow(new InvalidUserIdException());

		// when
		final ResultActions result = mockMvc.perform(patch("/point/{id}/use", userId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(amount)));

		// then
		result.andExpectAll(
			status().isBadRequest(),
			jsonPath("$.code").value("MEMBER01"),
			jsonPath("$.message").exists()
		);
	}

	@DisplayName("[실패] 유저 식별자가 0인 경우, bad request 를 반환한다")
	@Test
	void failSpendingBecauseOfUserIdZero() throws Exception {
		// given
		long userId = 0L;
		long amount = 1L;

		when(userPointService.spend(anyLong(), anyLong()))
			.thenThrow(new InvalidUserIdException());

		// when
		final ResultActions result = mockMvc.perform(patch("/point/{id}/use", userId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(amount)));

		// then
		result.andExpectAll(
			status().isBadRequest(),
			jsonPath("$.code").value("MEMBER01"),
			jsonPath("$.message").exists()
		);
	}

	@DisplayName("[실패] 등록되지 않은 유저 식별자인 경우, bad request 를 반환한다")
	@Test
	void failSpendingBecauseOfUnRegisteredUserId() throws Exception {
		// given
		long userId = 1L;
		long amount = 1L;

		when(userPointService.spend(anyLong(), anyLong()))
			.thenThrow(new UserNotFoundException());

		// when
		final ResultActions result = mockMvc.perform(patch("/point/{id}/use", userId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(amount)));

		// then
		result.andExpectAll(
			status().isBadRequest(),
			jsonPath("$.code").value("MEMBER02"),
			jsonPath("$.message").exists()
		);
	}

	@DisplayName("[성공] 등록된 유저 식별자와 1회 최대 사용 포인트를 초과하지 않는 양수인 경우, ok 를 반환한다")
	@Test
	void successSpending() throws Exception {
		// given
		long userId = 1L;
		long amount = 1000L;
		long availablePoint = 1000L;

		when(userPointService.spend(anyLong(), anyLong()))
			.thenReturn(new UserPointSelectDTO(userId, availablePoint));

		// when
		final ResultActions result = mockMvc.perform(patch("/point/{id}/use", userId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(amount)));

		// then
		result.andExpectAll(
			status().isOk(),
			jsonPath("$.id").value(userId),
			jsonPath("$.point").value(1000L)
		);
	}
}
