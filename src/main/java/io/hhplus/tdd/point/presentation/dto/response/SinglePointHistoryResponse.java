package io.hhplus.tdd.point.presentation.dto.response;

import io.hhplus.tdd.point.business.dto.PointHistorySelectDTO;
import io.hhplus.tdd.point.infrastructure.database.TransactionType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SinglePointHistoryResponse {
	private final long id;
	private final long userId;
	private final long amount;
	private final TransactionType type;
	private final long updateMillis;

	public static SinglePointHistoryResponse from(final PointHistorySelectDTO pointHistorySelectDTO) {
		return new SinglePointHistoryResponse(
			pointHistorySelectDTO.id(),
			pointHistorySelectDTO.userId(),
			pointHistorySelectDTO.amount(),
			pointHistorySelectDTO.type(),
			pointHistorySelectDTO.updateMillis());
	}
}
