package com.nequi.franchises.franchises.application.usecase.in.product;

import com.nequi.franchises.franchises.application.dto.request.AddProductRequest;
import com.nequi.franchises.franchises.application.dto.response.ProductResponse;

import reactor.core.publisher.Mono;

public interface AddProductToBranchUseCase {
    Mono<ProductResponse> execute(String franchiseId, AddProductRequest request);
}
