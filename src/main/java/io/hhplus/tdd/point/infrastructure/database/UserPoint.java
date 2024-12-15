package io.hhplus.tdd.point.infrastructure.database;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(final long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }
}
