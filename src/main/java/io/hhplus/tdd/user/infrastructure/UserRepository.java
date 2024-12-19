package io.hhplus.tdd.user.infrastructure;

public interface UserRepository {
	boolean exists(long id);
	User save(long id);
}
