package com.nequi.franchises.franchises.application.usecase.in.product;

import com.nequi.franchises.franchises.application.dto.request.UpdateProductNameRequest;
import com.nequi.franchises.franchises.application.dto.response.ProductResponse;
import reactor.core.publisher.Mono;

public interface UpdateProductNameUseCase {
    Mono<ProductResponse> execute(String franchiseId, UpdateProductNameRequest request);
}