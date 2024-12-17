package io.hhplus.tdd.point.exception;

import io.hhplus.tdd.common.api.support.error.ErrorType;
import lombok.Getter;

@Getter
public class InvalidPointSpendException extends IllegalArgumentException {

	private ErrorType errorType;

	public InvalidPointSpendException(ErrorType errorType) {
		super(errorType.getMessage());
		this.errorType = errorType;
	}
}
