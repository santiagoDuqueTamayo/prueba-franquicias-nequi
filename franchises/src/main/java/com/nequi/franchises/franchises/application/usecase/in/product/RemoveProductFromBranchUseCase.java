package com.nequi.franchises.franchises.application.usecase.in.product;

import com.nequi.franchises.franchises.application.dto.request.RemoveProductRequest;
import com.nequi.franchises.franchises.application.dto.response.MessageResponse;
import reactor.core.publisher.Mono;

public interface RemoveProductFromBranchUseCase {
    Mono<MessageResponse> execute(String franchiseId, RemoveProductRequest request);
}
