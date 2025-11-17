package com.nequi.franchises.franchises.application.usecase.in.franchise;

import com.nequi.franchises.franchises.application.dto.request.AddFranchiseRequest;
import com.nequi.franchises.franchises.application.dto.response.FranchiseResponse;
import com.nequi.franchises.franchises.application.mapper.DomainMapper;
import com.nequi.franchises.franchises.domain.model.Franchise;
import com.nequi.franchises.franchises.domain.port.out.FranchiseRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddFranchiseUseCaseImpl implements AddFranchiseUseCase {

    private final FranchiseRepositoryPort franchiseRepository;
    private final DomainMapper mapper;

    @Override
    public Mono<FranchiseResponse> execute(AddFranchiseRequest request) {
        log.info("Executing AddFranchise: franchiseName={}", request.getFranchiseName());

        return Mono.fromCallable(() -> {
                    // Crear nueva franquicia
                    Franchise franchise = new Franchise(request.getFranchiseName());
                    return franchise;
                })
                .flatMap(franchiseRepository::save)
                .map(mapper::toFranchiseResponse)
                .doOnSuccess(response -> log.info("Franchise added successfully: {} with ID: {}",
                        response.getName(), response.getId()))
                .doOnError(error -> log.error("Error adding franchise: {}", error.getMessage()));
    }
}

