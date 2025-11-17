package com.nequi.franchises.franchises.application.usecase.in.franchise;

import com.nequi.franchises.franchises.application.dto.request.UpdateFranchiseNameRequest;
import com.nequi.franchises.franchises.application.dto.response.FranchiseResponse;
import reactor.core.publisher.Mono;

public interface UpdateFranchiseNameUseCase {
    Mono<FranchiseResponse> execute(String franchiseId, UpdateFranchiseNameRequest request);
}
