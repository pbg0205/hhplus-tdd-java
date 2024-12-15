package io.hhplus.tdd.point.exception;

import io.hhplus.tdd.common.api.support.error.ErrorType;

public class InvalidUserIdException extends IllegalArgumentException {

	private final ErrorType errorType;

	public InvalidUserIdException() {
		this.errorType = ErrorType.INVALID_USER_ID;
	}

	public ErrorType getErrorType() {
		return errorType;
	}
}
