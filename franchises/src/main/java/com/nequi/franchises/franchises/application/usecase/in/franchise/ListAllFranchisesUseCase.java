package com.nequi.franchises.franchises.application.usecase.in.franchise;

import com.nequi.franchises.franchises.application.dto.response.FranchisesListResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ListAllFranchisesUseCase {
    Flux<FranchisesListResponse> execute();
}
