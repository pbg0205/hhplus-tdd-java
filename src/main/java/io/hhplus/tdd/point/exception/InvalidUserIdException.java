package io.hhplus.tdd.point.exception;

import io.hhplus.tdd.common.api.support.error.ErrorType;
import lombok.Getter;

@Getter
public class InvalidUserIdException extends IllegalArgumentException {

	private final ErrorType errorType;

	public InvalidUserIdException() {
		this.errorType = ErrorType.INVALID_USER_ID;
	}
}
