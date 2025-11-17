package com.nequi.franchises.franchises.application.usecase.in.branch;

import com.nequi.franchises.franchises.application.dto.request.AddBranchRequest;
import com.nequi.franchises.franchises.application.dto.response.BranchResponse;
import reactor.core.publisher.Mono;

public interface AddBranchUseCase {
    Mono<BranchResponse> execute(String franchiseId, AddBranchRequest request);
}
