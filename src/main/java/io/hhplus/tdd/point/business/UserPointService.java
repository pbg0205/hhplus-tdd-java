package io.hhplus.tdd.point.business;

import io.hhplus.tdd.point.dto.UserPointSelectDTO;

public interface UserPointService {
	UserPointSelectDTO findById(long id);
}
