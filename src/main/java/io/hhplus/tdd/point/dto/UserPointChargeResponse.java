package io.hhplus.tdd.point.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class UserPointChargeResponse {
	private final long id;
	private final long point;
}
