package com.nequi.franchises.franchises.application.usecase.in.product;

import com.nequi.franchises.franchises.application.dto.request.UpdateProductStockRequest;
import com.nequi.franchises.franchises.application.dto.response.ProductResponse;
import reactor.core.publisher.Mono;

public interface UpdateProductStockUseCase {
    Mono<ProductResponse> execute(String franchiseId, UpdateProductStockRequest request);
}
