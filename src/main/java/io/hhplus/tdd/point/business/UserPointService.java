package io.hhplus.tdd.point.business;

import io.hhplus.tdd.point.business.dto.UserPointSelectDTO;

public interface UserPointService {
	UserPointSelectDTO findById(long id);
	UserPointSelectDTO charge(long id, long amount);
	UserPointSelectDTO spend(long id, long amount);
}
