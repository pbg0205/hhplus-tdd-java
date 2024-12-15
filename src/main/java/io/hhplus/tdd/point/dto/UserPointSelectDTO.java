package io.hhplus.tdd.point.dto;

import java.util.Objects;

public class UserPointSelectDTO {
	private long userId;
	private long point;

	public UserPointSelectDTO(final long userId, final long point) {
		this.userId = userId;
		this.point = point;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (!(o instanceof final UserPointSelectDTO that))
			return false;
		return userId == that.userId && point == that.point;
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, point);
	}
}
