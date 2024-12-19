package io.hhplus.tdd.point.business.dto;

import io.hhplus.tdd.point.infrastructure.database.TransactionType;

public record PointHistorySelectDTO(long id, long userId, long amount, TransactionType type, long updateMillis) {
}
