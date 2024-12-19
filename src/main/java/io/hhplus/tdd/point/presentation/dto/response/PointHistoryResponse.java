package io.hhplus.tdd.point.presentation.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class PointHistoryResponse {
	private final List<SinglePointHistoryResponse> history;
}
