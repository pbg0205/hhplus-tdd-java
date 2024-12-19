package io.hhplus.tdd.point.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import io.hhplus.tdd.point.infrastructure.database.UserPointRepository;
import io.hhplus.tdd.user.infrastructure.UserRepository;

@SpringBootTest
@DisplayName("포인트 충전 통합 테스트")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserPointChargeIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserPointRepository userPointRepository;

	@DisplayName("[실패] 충전 포인트가 음수인 경우, bad request 를 반환한다")
	@Test
	void failChargingBecauseOfChargingPointNegative() throws Exception {
		// given
		long userId = 1L;
		long invalidAmount = -1L;

		// when
		final ResultActions result = mockMvc.perform(patch("/point/{id}/charge", userId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(invalidAmount)));

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
		long invalidAmount = 0L;

		// when
		final ResultActions result = mockMvc.perform(patch("/point/{id}/charge", userId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(invalidAmount)));

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
		long invalidAmount = 100_001L; // 단일 충전 최대 포인트: 100_000

		// when
		final ResultActions result = mockMvc.perform(patch("/point/{id}/charge", userId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(invalidAmount)));

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
		long invalidUserId = -1L;
		long amount = 1L;

		// when
		final ResultActions result = mockMvc.perform(patch("/point/{id}/charge", invalidUserId)
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
		long invalidUserId = 0L;
		long amount = 1L;

		// when
		final ResultActions result = mockMvc.perform(patch("/point/{id}/charge", invalidUserId)
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
	void failChargingBecauseOfUnRegisteredUserId() throws Exception {
		// given
		long unRegisteredUserId = 3L;
		long userId = 1L;
		long amount = 1L;

		userRepository.save(unRegisteredUserId);

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

		userRepository.save(userId);
		userPointRepository.insertOrUpdate(userId, 1000L);

		// when
		final ResultActions result = mockMvc.perform(patch("/point/{id}/charge", userId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(amount)));

		// then
		result.andExpectAll(
			status().isOk(),
			jsonPath("$.id").value(userId),
			jsonPath("$.point").value(2000L)
		);
	}
}
