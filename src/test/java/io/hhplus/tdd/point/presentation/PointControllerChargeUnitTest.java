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
import io.hhplus.tdd.point.dto.UserPointSelectDTO;
import io.hhplus.tdd.point.exception.InvalidChargingPointException;
import io.hhplus.tdd.point.exception.InvalidUserIdException;
import io.hhplus.tdd.point.exception.UserNotFoundException;

@DisplayName("포인트 충천 컨트롤러 단위 테스트")
@WebMvcTest(PointController.class)
class PointControllerChargeUnitTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserPointService userPointService;

	@DisplayName("[실패] 충전 포인트가 음수인 경우, bad request 를 반환한다")
	@Test
	void failChargingBecauseOfChargingPointNegative() throws Exception {
		// given
		long userId = 1L;
		long amount = -1L;

		when(userPointService.charge(anyLong(), anyLong()))
			.thenThrow(new InvalidChargingPointException(ErrorType.CHARGING_POINT01));

		// when
		final ResultActions result = mockMvc.perform(patch("/point/{id}/charge", userId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(amount)));

		// then
		result.andExpectAll(
			status().isBadRequest(),
			jsonPath("$.code").value("CHARGING_POINT01"),
			jsonPath("$.message").exists()
		);
	}

	@DisplayName("[실패] 충전 포인트가 0이면, bad request 를 반환한다")
	@Test
	void failChargingBecauseOfChargingPointZero() throws Exception {
		// given
		long userId = 1L;
		long amount = 0L;

		when(userPointService.charge(anyLong(), anyLong()))
			.thenThrow(new InvalidChargingPointException(ErrorType.CHARGING_POINT02));

		// when
		final ResultActions result = mockMvc.perform(patch("/point/{id}/charge", userId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(amount)));

		// then
		result.andExpectAll(
			status().isBadRequest(),
			jsonPath("$.code").value("CHARGING_POINT02"),
			jsonPath("$.message").exists()
		);
	}

	@DisplayName("[실패] 충전 포인트가 최대 충전 가능 포인트보다 큰 경우, bad request 를 반환한다")
	@Test
	void failChargingBecauseOfMaxChargingPoint() throws Exception {
		// given
		long userId = 1L;
		long amount = 100_001L; // 단일 충전 최대 포인트: 100_000

		when(userPointService.charge(anyLong(), anyLong()))
			.thenThrow(new InvalidChargingPointException(ErrorType.CHARGING_POINT03));

		// when
		final ResultActions result = mockMvc.perform(patch("/point/{id}/charge", userId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(amount)));

		// then
		result.andExpectAll(
			status().isBadRequest(),
			jsonPath("$.code").value("CHARGING_POINT03"),
			jsonPath("$.message").exists()
		);
	}

	@DisplayName("[실패] 유저 식별자가 음수인 경우, bad request 를 반환한다")
	@Test
	void failChargingBecauseOfUserIdNegative() throws Exception {
		// given
		long userId = -1L;
		long amount = 1L;

		when(userPointService.charge(anyLong(), anyLong()))
			.thenThrow(new InvalidUserIdException());

		// when
		final ResultActions result = mockMvc.perform(patch("/point/{id}/charge", userId)
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
	void failChargingBecauseOfUserIdZero() throws Exception {
		// given
		long userId = 0L;
		long amount = 1L;

		when(userPointService.charge(anyLong(), anyLong()))
			.thenThrow(new InvalidUserIdException());

		// when
		final ResultActions result = mockMvc.perform(patch("/point/{id}/charge", userId)
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
	void failChargingBecauseOfUnRegisteredUserId() throws Exception {
		// given
		long userId = 0L;
		long amount = 1L;

		when(userPointService.charge(anyLong(), anyLong()))
			.thenThrow(new UserNotFoundException());

		// when
		final ResultActions result = mockMvc.perform(patch("/point/{id}/charge", userId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(amount)));

		// then
		result.andExpectAll(
			status().isBadRequest(),
			jsonPath("$.code").value("MEMBER02"),
			jsonPath("$.message").exists()
		);
	}

	@DisplayName("[성공] 등록된 유저 식별자와 단일 최대 충전 포인트를 넘지 않는 양수의 충전 포인트인 경우, ok 를 반환한다")
	@Test
	void successCharging() throws Exception {
		// given
		long userId = 1L;
		long amount = 1000L;

		when(userPointService.charge(anyLong(), anyLong()))
			.thenReturn(new UserPointSelectDTO(userId, amount));

		// when
		final ResultActions result = mockMvc.perform(patch("/point/{id}/charge", userId)
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
