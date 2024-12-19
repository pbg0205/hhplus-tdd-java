package io.hhplus.tdd.point.integration;

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

import io.hhplus.tdd.point.infrastructure.database.UserPointRepository;
import io.hhplus.tdd.user.infrastructure.UserRepository;

@SpringBootTest
@DisplayName("포인트 조회 통합 테스트")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserPointListIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserPointRepository userPointRepository;

	@DisplayName("[실패] 음수인 유저 식별자인 경우, bad request 를 반환한다")
	@Test
	void failBecauseOfUserIdNegative() throws Exception {
		// given
		long invalidUserId = -1L;

		// when
		final ResultActions result = mockMvc.perform(get("/point/{id}", invalidUserId)
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
		final ResultActions result = mockMvc.perform(get("/point/{id}", invalidUserId)
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
		long userId = 1L;
		long unRegisteredUserId = 3L;

		userRepository.save(userId);
		userPointRepository.insertOrUpdate(userId, 300L);

		// when
		final ResultActions result = mockMvc.perform(get("/point/{id}", unRegisteredUserId)
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
	void successFindingUserPoint() throws Exception {
		// given
		long userId = 1L;
		long point = 300L;

		userRepository.save(userId);
		userPointRepository.insertOrUpdate(1L, 300L);

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
