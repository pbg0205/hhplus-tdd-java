package io.hhplus.tdd.point.business;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.hhplus.tdd.point.infrastructure.database.UserPoint;
import io.hhplus.tdd.point.infrastructure.database.UserPointRepository;
import io.hhplus.tdd.user.infrastructure.UserRepository;

@SpringBootTest
class UserPointServiceConcurrencyTest {

	@Autowired
	private UserPointService userPointService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserPointRepository userPointRepository;

	@DisplayName("포인트 충전 동시성 테스트")
	@Test
	void SuccessPointChargeConcurrency() throws InterruptedException {
		// given
		long userId = 1L;
		long chargingPoint = 10L;

		int threadCount = 16;
		int executionCount = 50;

		userRepository.save(1L);

		// when
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

		for (int i = 0; i < executionCount; i++) {
			executorService.submit(() -> userPointService.charge(userId, chargingPoint));
		}

		executorService.shutdown();
		executorService.awaitTermination(20, TimeUnit.SECONDS);

		// then
		final UserPoint userPoint = userPointRepository.selectById(userId);
		assertThat(userPoint.point()).isEqualTo(500);
	}
}
