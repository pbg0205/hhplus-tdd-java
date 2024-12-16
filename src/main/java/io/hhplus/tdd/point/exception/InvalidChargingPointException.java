package io.hhplus.tdd.point.exception;

import io.hhplus.tdd.common.api.support.error.ErrorType;
import lombok.Getter;

@Getter
public class InvalidChargingPointException extends IllegalArgumentException {
	private final ErrorType errorType;

	public InvalidChargingPointException(final ErrorType errorType) {
		this.errorType = errorType;
	}
}
