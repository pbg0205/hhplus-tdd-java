package io.hhplus.tdd.point.presentation.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class UserPointSpendResponse {
	private final long id;
	private final long point;
}
