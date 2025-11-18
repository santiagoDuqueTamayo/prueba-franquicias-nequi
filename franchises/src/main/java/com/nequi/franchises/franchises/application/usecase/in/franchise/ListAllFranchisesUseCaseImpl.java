package com.nequi.franchises.franchises.application.usecase.in.franchise;

import com.nequi.franchises.franchises.application.dto.response.FranchisesListResponse;
import com.nequi.franchises.franchises.application.mapper.DomainMapper;
import com.nequi.franchises.franchises.domain.port.out.FranchiseRepositoryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class ListAllFranchisesUseCaseImpl implements ListAllFranchisesUseCase{

    private final FranchiseRepositoryPort franchiseRepository;
    private final DomainMapper domainMapper;

    public ListAllFranchisesUseCaseImpl(FranchiseRepositoryPort franchiseRepository, DomainMapper domainMapper) {
        this.franchiseRepository = franchiseRepository;
        this.domainMapper = domainMapper;
    }

    @Override
    public Flux<FranchisesListResponse> execute() {
        return franchiseRepository.findAll()
                .map(domainMapper::mapToResponse)
                .doOnNext(franchise -> log.info("Mapped Franchise: {}", franchise.getNameFranchises()))
                .switchIfEmpty(Flux.defer(() -> {
                    log.warn("No franchises found in repository.");
                    return Flux.empty();
                }))
                .doOnError(error -> log.error("Error in ListAllFranchisesUseCase: {}", error.getMessage()));
    }
}
