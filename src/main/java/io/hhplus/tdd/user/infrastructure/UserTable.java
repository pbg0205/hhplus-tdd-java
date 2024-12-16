package io.hhplus.tdd.user.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

@Component
public class UserTable implements UserRepository {

	private final Map<Long, User> table = new HashMap<>();

	@Override
	public boolean exists(final long id) {
		throttle(200);
		return table.get(id) != null;
	}

	@Override
	public User save(final long id) {
		return table.put(id, new User(id));
	}

	private void throttle(long millis) {
		try {
			TimeUnit.MILLISECONDS.sleep((long) (Math.random() * millis));
		} catch (InterruptedException ignored) {
		}
	}

}
