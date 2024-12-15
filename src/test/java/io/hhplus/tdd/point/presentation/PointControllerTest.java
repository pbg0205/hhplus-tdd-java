package io.hhplus.tdd.point.presentation;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import io.hhplus.tdd.point.business.UserPointService;
import io.hhplus.tdd.point.dto.UserPointSelectDTO;
import io.hhplus.tdd.point.exception.InvalidUserIdException;
import io.hhplus.tdd.point.exception.UserNotFoundException;

@WebMvcTest(PointController.class)
class PointControllerTest {
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserPointService userPointService;

	@DisplayName("[fail] 음수인 유저 식별자인 경우 bad request 를 반환한다")
	@Test
	void failBecauseOfUserIdNegative() throws Exception {
		// given
		long userId = -1L;

		when(userPointService.findById(anyLong())).thenThrow(new InvalidUserIdException());

		// when
		final ResultActions result = mockMvc.perform(get("/point/{id}", userId)
			.contentType(MediaType.APPLICATION_JSON));

		// then
		result.andExpectAll(
			status().isBadRequest(),
			jsonPath("$.code").value("MEMBER01"),
			jsonPath("$.message").exists()
		);
	}

	@DisplayName("[fail] 0인 유저 식별자인 경우 bad request 를 반환한다")
	@Test
	void failBecauseOfUserIdZero() throws Exception {
		// given
		long userId = 0L;

		when(userPointService.findById(anyLong())).thenThrow(new InvalidUserIdException());

		// when
		final ResultActions result = mockMvc.perform(get("/point/{id}", userId)
			.contentType(MediaType.APPLICATION_JSON));

		// then
		result.andExpectAll(
			status().isBadRequest(),
			jsonPath("$.code").value("MEMBER01"),
			jsonPath("$.message").exists()
		);
	}

	@DisplayName("[fail] 등록되지 않은 유저 식별자인 경우 bad request 를 반환한다")
	@Test
	void failBecauseOfInvalidUserId() throws Exception {
		// given
		long userId = 3L;

		when(userPointService.findById(anyLong())).thenThrow(new UserNotFoundException());

		// when
		final ResultActions result = mockMvc.perform(get("/point/{id}", userId)
			.contentType(MediaType.APPLICATION_JSON));

		// then
		result.andExpectAll(
			status().isBadRequest(),
			jsonPath("$.code").value("MEMBER02"),
			jsonPath("$.message").exists()
		);
	}

	@DisplayName("[success] 등록된 유저 식별자인 경우 ok 를 반환한다")
	@Test
	void successFindingUserPoint() throws Exception {
		// given
		long userId = 1L;
		long point = 300L;

		when(userPointService.findById(anyLong())).thenReturn(new UserPointSelectDTO(1L, 300L));

		// when
		final ResultActions result = mockMvc.perform(get("/point/{id}", userId)
			.contentType(MediaType.APPLICATION_JSON));

		// then
		result.andExpectAll(
			status().isOk(),
			jsonPath("$.userId").value(userId),
			jsonPath("$.point").value(point)
		);

	}

}
