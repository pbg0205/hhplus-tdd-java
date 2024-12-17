package io.hhplus.tdd.point.infrastructure.database;

import java.util.List;

public interface PointHistoryRepository {
	PointHistory insert(long userId, long amount, TransactionType type, long updateMillis);
	List<PointHistory> selectAllByUserId(long userId);
}
