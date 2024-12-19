package io.hhplus.tdd.point.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import io.hhplus.tdd.point.infrastructure.database.PointHistoryRepository;
import io.hhplus.tdd.point.infrastructure.database.TransactionType;
import io.hhplus.tdd.user.infrastructure.UserRepository;

@SpringBootTest
@DisplayName("포인트 히스토리 통합 테스트")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserPointHistoryIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PointHistoryRepository pointHistoryRepository;

	@DisplayName("[실패] 음수인 유저 식별자인 경우, bad request 를 반환한다")
	@Test
	void failBecauseOfUserIdNegative() throws Exception {
		// given
		long invalidUserId = -1L;

		// when
		final ResultActions result = mockMvc.perform(get("/point/{id}/histories", invalidUserId)
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
		long invalidUserId = 0L;

		// when
		final ResultActions result = mockMvc.perform(get("/point/{id}/histories", invalidUserId)
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
		long unRegisteredUserId = 3L;

		userRepository.save(1L);

		// when
		final ResultActions result = mockMvc.perform(get("/point/{id}/histories", unRegisteredUserId)
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

		userRepository.save(userId);

		pointHistoryRepository.insert(1L, 10_000L, TransactionType.CHARGE, System.currentTimeMillis());
		pointHistoryRepository.insert(1L, 3_000L, TransactionType.USE, System.currentTimeMillis());
		pointHistoryRepository.insert(1L, 3_000L, TransactionType.USE, System.currentTimeMillis());

		// when
		final ResultActions result = mockMvc.perform(get("/point/{id}/histories", userId)
			.contentType(MediaType.APPLICATION_JSON));

		// then
		result.andExpectAll(
			status().isOk(),
			jsonPath("$.history").isArray(),
			jsonPath("$.history", hasSize(3))
		);
	}
}
