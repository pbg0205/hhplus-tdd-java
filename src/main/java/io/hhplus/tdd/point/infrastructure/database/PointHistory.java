package io.hhplus.tdd.point.infrastructure.database;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {
}
