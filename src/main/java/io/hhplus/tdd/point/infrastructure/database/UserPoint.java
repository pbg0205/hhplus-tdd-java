package io.hhplus.tdd.point.infrastructure.database;

import java.util.Objects;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(final long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof final UserPoint userPoint))
            return false;
		return id == userPoint.id && point == userPoint.point;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, point);
    }
}
