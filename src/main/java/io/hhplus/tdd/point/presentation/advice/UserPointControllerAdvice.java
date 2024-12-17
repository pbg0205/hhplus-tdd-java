package io.hhplus.tdd.point.presentation.advice;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.hhplus.tdd.common.api.support.error.ErrorResponse;
import io.hhplus.tdd.common.api.support.error.ErrorType;
import io.hhplus.tdd.point.exception.InvalidChargingPointException;
import io.hhplus.tdd.point.exception.InvalidPointSpendException;
import io.hhplus.tdd.point.exception.InvalidUserIdException;
import io.hhplus.tdd.point.exception.UserNotFoundException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class UserPointControllerAdvice {

	@ExceptionHandler({InvalidUserIdException.class})
	public ResponseEntity<ErrorResponse> handleInvalidUserIdException(InvalidUserIdException invalidUserIdException) {
		final ErrorType errorType = invalidUserIdException.getErrorType();
		return ResponseEntity.badRequest()
			.body(new ErrorResponse(errorType.getErrorCode().name(), errorType.getMessage()));
	}

	@ExceptionHandler({UserNotFoundException.class})
	public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException userNotFoundException) {
		final ErrorType errorType = userNotFoundException.getErrorType();
		return ResponseEntity.badRequest()
			.body(new ErrorResponse(errorType.getErrorCode().name(), errorType.getMessage()));
	}

	@ExceptionHandler({InvalidChargingPointException.class})
	public ResponseEntity<ErrorResponse> handleInvalidChargingPointException(
		InvalidChargingPointException invalidChargingPointException) {
		final ErrorType errorType = invalidChargingPointException.getErrorType();
		return ResponseEntity.badRequest()
			.body(new ErrorResponse(errorType.getErrorCode().name(), errorType.getMessage()));
	}

	@ExceptionHandler({InvalidPointSpendException.class})
	public ResponseEntity<ErrorResponse> handleInvalidPointSpendException(
		InvalidPointSpendException invalidPointSpendException) {
		final ErrorType errorType = invalidPointSpendException.getErrorType();
		return ResponseEntity.badRequest()
			.body(new ErrorResponse(errorType.getErrorCode().name(), errorType.getMessage()));
	}
}
