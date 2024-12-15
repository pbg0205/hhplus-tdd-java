package io.hhplus.tdd.point.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import io.hhplus.tdd.point.business.UserPointService;
import io.hhplus.tdd.point.dto.UserPointSelectDTO;
import io.hhplus.tdd.point.infrastructure.database.PointHistory;
import io.hhplus.tdd.point.infrastructure.database.UserPoint;
import io.hhplus.tdd.point.dto.UserPointResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController {

	private static final Logger log = LoggerFactory.getLogger(PointController.class);

	private final UserPointService userPointService;

	@GetMapping("{id}")
	public ResponseEntity<UserPointResponse> point(
		@PathVariable(name = "id") long id
	) {
		final UserPointSelectDTO userPointSelectDTO = this.userPointService.findById(id);
		return ResponseEntity.ok()
			.body(new UserPointResponse(userPointSelectDTO.userId(), userPointSelectDTO.point()));
	}

	/**
	 * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
	 */
	@GetMapping("{id}/histories")
	public List<PointHistory> history(
		@PathVariable long id
	) {
		return List.of();
	}

	/**
	 * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
	 */
	@PatchMapping("{id}/charge")
	public UserPoint charge(
		@PathVariable long id,
		@RequestBody long amount
	) {
		return new UserPoint(0, 0, 0);
	}

	/**
	 * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
	 */
	@PatchMapping("{id}/use")
	public UserPoint use(
		@PathVariable long id,
		@RequestBody long amount
	) {
		return new UserPoint(0, 0, 0);
	}
}
