package io.hhplus.tdd.point.presentation;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import io.hhplus.tdd.point.business.UserPointService;
import io.hhplus.tdd.point.business.dto.PointHistorySelectDTO;
import io.hhplus.tdd.point.exception.InvalidUserIdException;
import io.hhplus.tdd.point.exception.UserNotFoundException;
import io.hhplus.tdd.point.infrastructure.database.TransactionType;

@DisplayName("포인트 히스토리 컨트롤러 단위 테스트")
@WebMvcTest(PointController.class)
class PointControllerHistoryUnitTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserPointService userPointService;

	@DisplayName("[실패] 음수인 유저 식별자인 경우, bad request 를 반환한다")
	@Test
	void failBecauseOfUserIdNegative() throws Exception {
		// given
		long userId = -1L;

		when(userPointService.findPointHistoryByUserId(anyLong())).thenThrow(new InvalidUserIdException());

		// when
		final ResultActions result = mockMvc.perform(get("/point/{id}/histories", userId)
			.contentType(MediaType.APPLICATION_JSON));

		// then
		result.andExpectAll(
			status().isBadRequest(),
			jsonPath("$.code").value("MEMBER01"),
			jsonPath("$.message").exists()
		);
	}

	@DisplayName("[실패] 0인 유저 식별자인 경우, bad request 를 반환한다")
	@Test
	void failBecauseOfUserIdZero() throws Exception {
		// given
		long userId = 0L;

		when(userPointService.findPointHistoryByUserId(anyLong())).thenThrow(new InvalidUserIdException());

		// when
		final ResultActions result = mockMvc.perform(get("/point/{id}/histories", userId)
			.contentType(MediaType.APPLICATION_JSON));

		// then
		result.andExpectAll(
			status().isBadRequest(),
			jsonPath("$.code").value("MEMBER01"),
			jsonPath("$.message").exists()
		);
	}

	@DisplayName("[실패] 등록되지 않은 유저 식별자인 경우, bad request 를 반환한다")
	@Test
	void failBecauseOfInvalidUserId() throws Exception {
		// given
		long userId = 3L;

		when(userPointService.findPointHistoryByUserId(anyLong())).thenThrow(new UserNotFoundException());

		// when
		final ResultActions result = mockMvc.perform(get("/point/{id}/histories", userId)
			.contentType(MediaType.APPLICATION_JSON));

		// then
		result.andExpectAll(
			status().isBadRequest(),
			jsonPath("$.code").value("MEMBER02"),
			jsonPath("$.message").exists()
		);
	}

	@DisplayName("[성공] 등록된 유저 식별자인 경우, ok 를 반환한다")
	@Test
	void successFindingUserHistory() throws Exception {
		// given
		long userId = 1L;

		when(userPointService.findPointHistoryByUserId(anyLong()))
			.thenReturn(List.of(
				new PointHistorySelectDTO(1L, userId, 10_000L, TransactionType.CHARGE, 100L),
				new PointHistorySelectDTO(1L, userId, 2_000L, TransactionType.USE, 100L),
				new PointHistorySelectDTO(1L, userId, 1_000L, TransactionType.USE, 100L),
				new PointHistorySelectDTO(1L, userId, 1_000L, TransactionType.USE, 100L)
			));

		// when
		final ResultActions result = mockMvc.perform(get("/point/{id}/histories", userId)
			.contentType(MediaType.APPLICATION_JSON));

		// then
		result.andExpectAll(
			status().isOk(),
			jsonPath("$.history").isArray(),
			jsonPath("$.history", hasSize(4))
		);
	}
}
