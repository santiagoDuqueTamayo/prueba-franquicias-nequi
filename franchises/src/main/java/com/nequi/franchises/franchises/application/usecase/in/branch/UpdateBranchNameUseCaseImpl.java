package com.nequi.franchises.franchises.application.usecase.in.branch;

import com.nequi.franchises.franchises.application.dto.request.UpdateBranchNameRequest;
import com.nequi.franchises.franchises.application.dto.response.BranchResponse;
import com.nequi.franchises.franchises.application.mapper.DomainMapper;
import com.nequi.franchises.franchises.domain.exception.DuplicateEntityException;
import com.nequi.franchises.franchises.domain.exception.InvalidOperationException;
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
public class UpdateBranchNameUseCaseImpl implements UpdateBranchNameUseCase {

    private final FranchiseRepositoryPort franchiseRepository;
    private final DomainMapper mapper;

    @Override
    public Mono<BranchResponse> execute(String franchiseId, UpdateBranchNameRequest request) {
        log.info("Executing UpdateBranchName: franchiseId={}, oldName={}, newName={}",
                franchiseId, request.getOldName(), request.getNewName());

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException(
                        "Franquicia no encontrada con ID: " + franchiseId)))
                .flatMap(franchise -> validateAndUpdateBranchName(franchise, request))
                .doOnSuccess(response -> log.info("Branch name updated successfully: {} -> {}",
                        request.getOldName(), request.getNewName()))
                .doOnError(error -> log.error("Error updating branch name: {}", error.getMessage()));
    }

    private Mono<BranchResponse> validateAndUpdateBranchName(Franchise franchise, UpdateBranchNameRequest request) {
        try {
            // Validación: el nuevo nombre debe ser diferente al actual
            if (request.getOldName().trim().equalsIgnoreCase(request.getNewName().trim())) {
                return Mono.error(new InvalidOperationException(
                        "El nuevo nombre debe ser diferente al nombre actual de la sucursal"));
            }

            // Validación: verificar que no exista otra sucursal con el nuevo nombre (excluyendo la actual)
            if (branchExistsWithNameExcluding(franchise, request.getNewName(), request.getOldName())) {
                return Mono.error(new DuplicateEntityException(
                        String.format("Ya existe una sucursal con el nombre '%s' en la franquicia",
                                request.getNewName())));
            }

            // Delegar al agregado (validaciones automáticas del dominio)
            franchise.updateBranchName(request.getOldName(), request.getNewName());

            // Obtener la sucursal actualizada para la respuesta
            Branch updatedBranch = findBranchByName(franchise, request.getNewName());

            // Persistir cambios y retornar respuesta
            return franchiseRepository.save(franchise)
                    .map(savedFranchise -> mapper.toBranchResponse(updatedBranch));

        } catch (IllegalStateException e) {
            // Capturar errores del dominio y convertirlos en excepciones específicas
            if (e.getMessage().contains("No existe una sucursal")) {
                return Mono.error(new NotFoundException(
                        "Sucursal no encontrada con nombre: " + request.getOldName()));
            }
            return Mono.error(e);
        } catch (IllegalArgumentException e) {
            return Mono.error(new InvalidOperationException(e.getMessage()));
        }
    }

    private Branch findBranchByName(Franchise franchise, String branchName) {
        return franchise.getBranches().stream()
                .filter(b -> b.getName().equalsIgnoreCase(branchName.trim()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        "Sucursal no encontrada con nombre: " + branchName));
    }

    private boolean branchExistsWithNameExcluding(Franchise franchise, String branchName, String excludeName) {
        return franchise.getBranches().stream()
                .filter(b -> !b.getName().equalsIgnoreCase(excludeName.trim()))
                .anyMatch(b -> b.getName().equalsIgnoreCase(branchName.trim()));
    }
}

