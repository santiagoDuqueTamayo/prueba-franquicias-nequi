package com.nequi.franchises.franchises.application.usecase.in.branch;

import com.nequi.franchises.franchises.application.dto.request.AddBranchRequest;
import com.nequi.franchises.franchises.application.dto.response.BranchResponse;
import com.nequi.franchises.franchises.application.mapper.DomainMapper;
import com.nequi.franchises.franchises.domain.exception.DuplicateEntityException;
import com.nequi.franchises.franchises.domain.exception.NotFoundException;
import com.nequi.franchises.franchises.domain.model.Branch;
import com.nequi.franchises.franchises.domain.model.Franchise;
import com.nequi.franchises.franchises.domain.port.out.FranchiseRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddBranchUseCaseImpl implements AddBranchUseCase {

    private final FranchiseRepositoryPort franchiseRepository;
    private final DomainMapper mapper;

    @Override
    public Mono<BranchResponse> execute(String franchiseId, AddBranchRequest request) {
        log.info("Executing AddBranch: franchiseId={}, branchName={}",
                franchiseId, request.getBranchName());

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException(
                        "Franquicia no encontrada con ID: " + franchiseId)))
                .flatMap(franchise -> addBranchAndSave(franchise, request))
                .doOnSuccess(response -> log.info("Branch added successfully: {}", response.getName()))
                .doOnError(error -> log.error("Error adding branch: {}", error.getMessage()));
    }

    private Mono<BranchResponse> addBranchAndSave(Franchise franchise, AddBranchRequest request) {
        try {
            // Validación: verificar que no exista otra sucursal con el mismo nombre
            if (branchExistsWithName(franchise, request.getBranchName())) {
                return Mono.error(new DuplicateEntityException(
                        String.format("Ya existe una sucursal con el nombre '%s' en la franquicia",
                                request.getBranchName())));
            }

            // Crear nueva sucursal
            Branch branch = new Branch(request.getBranchName());

            // Delegar al agregado (validaciones automáticas del dominio)
            franchise.addBranch(branch);

            // Persistir cambios y retornar respuesta
            return franchiseRepository.save(franchise)
                    .map(savedFranchise -> mapper.toBranchResponse(branch));

        } catch (IllegalStateException e) {
            // Capturar errores del dominio y convertirlos en excepciones específicas
            if (e.getMessage().contains("Ya existe una sucursal")) {
                return Mono.error(new DuplicateEntityException(
                        String.format("Ya existe una sucursal con el nombre '%s' en la franquicia",
                                request.getBranchName())));
            }
            return Mono.error(e);
        } catch (IllegalArgumentException e) {
            return Mono.error(new IllegalArgumentException(e.getMessage()));
        }
    }

    private boolean branchExistsWithName(Franchise franchise, String branchName) {
        return franchise.getBranches().stream()
                .anyMatch(b -> b.getName().equalsIgnoreCase(branchName.trim()));
    }
}

