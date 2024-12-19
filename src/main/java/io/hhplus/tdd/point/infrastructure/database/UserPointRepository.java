package io.hhplus.tdd.point.infrastructure.database;

public interface UserPointRepository {
	UserPoint selectById(Long id);
	UserPoint insertOrUpdate(long id, long amount);
}
