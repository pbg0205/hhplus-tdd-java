package io.hhplus.tdd.common.api.support.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
	INVALID_USER_ID(ErrorCode.MEMBER01, "유효하지 않은 사용자 식별자 입니다."),
	USER_NOT_FOUND(ErrorCode.MEMBER02, "일치하는 유저가 존재하지 않습니다."),
	CHARGING_POINT_NEGATIVE(ErrorCode.CHARGING_POINT01, "충전 포인트는 음수일 수 없습니다."),
	CHARGING_POINT_ZERO(ErrorCode.CHARGING_POINT02, "충전 포인트는 0일 수 없습니다."),
	CHARGING_POINT_MAX(ErrorCode.CHARGING_POINT03, "단일 포인트 충전은 최대 10만점 가능합니다."),
	SPENDING_POINT_NEGATIVE(ErrorCode.POINT_USE01, "사용 포인트는 음수일 수 없습니다."),
	SPENDING_POINT_ZERO(ErrorCode.POINT_USE02, "사용 포인트는 0일 수 없습니다."),
	SPENDING_POINT_MAX(ErrorCode.POINT_USE03, "1회 사용 포인트는 최대 만점 입니다.");

	private final ErrorCode errorCode;
	private final String message;

}
