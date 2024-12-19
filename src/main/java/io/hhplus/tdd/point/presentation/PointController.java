package io.hhplus.tdd.point.presentation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.hhplus.tdd.point.business.UserPointService;
import io.hhplus.tdd.point.business.dto.PointHistorySelectDTO;
import io.hhplus.tdd.point.business.dto.UserPointSelectDTO;
import io.hhplus.tdd.point.infrastructure.database.PointHistory;
import io.hhplus.tdd.point.presentation.dto.response.PointHistoryResponse;
import io.hhplus.tdd.point.presentation.dto.response.SinglePointHistoryResponse;
import io.hhplus.tdd.point.presentation.dto.response.UserPointSpendResponse;
import io.hhplus.tdd.point.presentation.dto.response.UserPointChargeResponse;
import io.hhplus.tdd.point.presentation.dto.response.UserPointResponse;
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
	public ResponseEntity<PointHistoryResponse> history(
		@PathVariable(name = "id") long id
	) {
		final List<PointHistorySelectDTO> pointHistorySelectDTOListList = userPointService.findPointHistoryByUserId(id);
		final List<SinglePointHistoryResponse> singleHistoryResponseList = pointHistorySelectDTOListList.stream()
			.map(SinglePointHistoryResponse::from)
			.toList();

		return ResponseEntity.ok(new PointHistoryResponse(singleHistoryResponseList));
	}

	@PatchMapping("{id}/charge")
	public ResponseEntity<UserPointChargeResponse> charge(
		@PathVariable(name = "id") long id,
		@RequestBody long amount
	) {
		final UserPointSelectDTO userPointSelectDTO = userPointService.charge(id, amount);
		return ResponseEntity.ok(new UserPointChargeResponse(userPointSelectDTO.userId(), userPointSelectDTO.point()));
	}

	@PatchMapping("{id}/use")
	public ResponseEntity<UserPointSpendResponse> use(
		@PathVariable(name = "id") long id,
		@RequestBody long amount
	) {
		final UserPointSelectDTO userPointSelectDTO = userPointService.spend(id, amount);
		return ResponseEntity.ok(new UserPointSpendResponse(userPointSelectDTO.userId(), userPointSelectDTO.point()));
	}
}
