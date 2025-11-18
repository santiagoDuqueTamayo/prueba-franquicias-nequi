package com.nequi.franchises.franchises.application.usecase.in.franchise;

import com.nequi.franchises.franchises.application.dto.response.TopProductStockResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GetTopProductsByStockUseCase {
    Mono<List<TopProductStockResponse>> execute(String franchiseId);
}
