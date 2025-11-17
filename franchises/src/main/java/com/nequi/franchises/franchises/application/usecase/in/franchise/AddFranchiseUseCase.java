package com.nequi.franchises.franchises.application.usecase.in.franchise;

import com.nequi.franchises.franchises.application.dto.request.AddFranchiseRequest;
import com.nequi.franchises.franchises.application.dto.response.FranchiseResponse;
import reactor.core.publisher.Mono;

public interface AddFranchiseUseCase {
    Mono<FranchiseResponse> execute(AddFranchiseRequest request);
}
