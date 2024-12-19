package io.hhplus.tdd.point.presentation.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class UserPointResponse {
	private final long userId;
	private final long point;
}
