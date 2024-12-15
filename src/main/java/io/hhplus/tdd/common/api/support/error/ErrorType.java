package io.hhplus.tdd.common.api.support.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
	INVALID_USER_ID(ErrorCode.MEMBER01, "유효하지 않은 사용자 식별자 입니다."),
	USER_NOT_FOUND(ErrorCode.MEMBER02, "일치하는 유저가 존재하지 않습니다.");

	private final ErrorCode errorCode;
	private final String message;

}
