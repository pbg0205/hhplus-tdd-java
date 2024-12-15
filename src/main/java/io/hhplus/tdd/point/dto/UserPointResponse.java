package io.hhplus.tdd.point.dto;

public class UserPointResponse {
	private long userId;
	private long point;

	public UserPointResponse() {
	}

	public UserPointResponse(final long userId, final long point) {
		this.userId = userId;
		this.point = point;
	}

	public long getUserId() {
		return userId;
	}

	public long getPoint() {
		return point;
	}
}
