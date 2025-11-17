package com.nequi.franchises.franchises.application.usecase.in.franchise;

import com.nequi.franchises.franchises.application.dto.request.UpdateFranchiseNameRequest;
import com.nequi.franchises.franchises.application.dto.response.FranchiseResponse;
import com.nequi.franchises.franchises.application.mapper.DomainMapper;
import com.nequi.franchises.franchises.domain.exception.InvalidOperationException;
import com.nequi.franchises.franchises.domain.exception.NotFoundException;
import com.nequi.franchises.franchises.domain.model.Franchise;
import com.nequi.franchises.franchises.domain.port.out.FranchiseRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateFranchiseNameUseCaseImpl implements UpdateFranchiseNameUseCase {

    private final FranchiseRepositoryPort franchiseRepository;
    private final DomainMapper mapper;

    @Override
    public Mono<FranchiseResponse> execute(String franchiseId, UpdateFranchiseNameRequest request) {
        log.info("Executing UpdateFranchiseName: franchiseId={}, newName={}",
                franchiseId, request.getNewName());

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException(
                        "Franquicia no encontrada con ID: " + franchiseId)))
                .flatMap(franchise -> validateAndUpdateFranchiseName(franchise, request))
                .doOnSuccess(response -> log.info("Franchise name updated successfully: {} -> {}",
                        franchiseId, request.getNewName()))
                .doOnError(error -> log.error("Error updating franchise name: {}", error.getMessage()));
    }

    private Mono<FranchiseResponse> validateAndUpdateFranchiseName(Franchise franchise, UpdateFranchiseNameRequest request) {
        try {
            // Validación: el nuevo nombre debe ser diferente al actual
            if (franchise.getName().trim().equalsIgnoreCase(request.getNewName().trim())) {
                return Mono.error(new InvalidOperationException(
                        "El nuevo nombre debe ser diferente al nombre actual de la franquicia"));
            }

            // Delegar al agregado (validaciones automáticas del dominio)
            franchise.updateName(request.getNewName());

            // Persistir cambios y retornar respuesta
            return franchiseRepository.save(franchise)
                    .map(mapper::toFranchiseResponse);

        } catch (IllegalArgumentException e) {
            return Mono.error(new InvalidOperationException(e.getMessage()));
        }
    }
}

