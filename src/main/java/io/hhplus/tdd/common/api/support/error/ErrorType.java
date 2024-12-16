package io.hhplus.tdd.common.api.support.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
	INVALID_USER_ID(ErrorCode.MEMBER01, "유효하지 않은 사용자 식별자 입니다."),
	USER_NOT_FOUND(ErrorCode.MEMBER02, "일치하는 유저가 존재하지 않습니다."),
	CHARGING_POINT01(ErrorCode.CHARGING_POINT01, "충전 포인트는 음수일 수 없습니다."),
	CHARGING_POINT02(ErrorCode.CHARGING_POINT02, "충전 포인트는 0일 수 없습니다."),
	CHARGING_POINT03(ErrorCode.CHARGING_POINT03, "단일 포인트 충전은 최대 10만점 가능합니다.");

	private final ErrorCode errorCode;
	private final String message;

}
