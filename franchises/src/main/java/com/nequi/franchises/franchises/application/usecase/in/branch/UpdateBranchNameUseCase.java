package com.nequi.franchises.franchises.application.usecase.in.branch;

import com.nequi.franchises.franchises.application.dto.request.UpdateBranchNameRequest;
import com.nequi.franchises.franchises.application.dto.response.BranchResponse;
import reactor.core.publisher.Mono;

public interface UpdateBranchNameUseCase {
    Mono<BranchResponse> execute(String franchiseId, UpdateBranchNameRequest request);
}
