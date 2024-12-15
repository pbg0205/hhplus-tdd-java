package io.hhplus.tdd.point.exception;

import io.hhplus.tdd.common.api.support.error.ErrorType;
import lombok.Getter;

@Getter
public class UserNotFoundException extends IllegalArgumentException {

	private final ErrorType errorType;

	public UserNotFoundException() {
		this.errorType = ErrorType.USER_NOT_FOUND;
	}
}
