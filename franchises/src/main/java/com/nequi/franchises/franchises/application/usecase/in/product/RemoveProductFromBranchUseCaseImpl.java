package com.nequi.franchises.franchises.application.usecase.in.product;

import com.nequi.franchises.franchises.application.dto.request.RemoveProductRequest;
import com.nequi.franchises.franchises.application.dto.response.MessageResponse;
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
public class RemoveProductFromBranchUseCaseImpl implements RemoveProductFromBranchUseCase {

    private final FranchiseRepositoryPort franchiseRepository;

    @Override
    public Mono<MessageResponse> execute(String franchiseId, RemoveProductRequest request) {
        log.info("Executing RemoveProductFromBranch: franchiseId={}, branchName={}, productName={}",
                franchiseId, request.getBranchName(), request.getProductName());

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException(
                        "Franquicia no encontrada con ID: " + franchiseId)))
                .flatMap(franchise -> removeProductAndSave(franchise, request))
                .map(franchise -> buildSuccessMessage(request))
                .doOnSuccess(response -> log.info("Product removed successfully: {}", request.getProductName()))
                .doOnError(error -> log.error("Error removing product: {}", error.getMessage()));
    }

    private Mono<Franchise> removeProductAndSave(Franchise franchise, RemoveProductRequest request) {
        try {
            // Delegar al agregado (validaciones automáticas del dominio)
            franchise.removeProductFromBranch(request.getBranchName(), request.getProductName());

            // Persistir cambios
            return franchiseRepository.save(franchise);

        } catch (IllegalStateException e) {
            // Capturar errores del dominio y convertirlos en NotFoundException
            if (e.getMessage().contains("No existe una sucursal")) {
                return Mono.error(new NotFoundException(
                        "Sucursal no encontrada con nombre: " + request.getBranchName()));
            } else if (e.getMessage().contains("no existe en la sucursal")) {
                return Mono.error(new NotFoundException(
                        "Producto no encontrado con nombre: " + request.getProductName() +
                                " en la sucursal: " + request.getBranchName()));
            }
            return Mono.error(e);
        } catch (IllegalArgumentException e) {
            return Mono.error(new NotFoundException(e.getMessage()));
        }
    }

    private MessageResponse buildSuccessMessage(RemoveProductRequest request) {
        return MessageResponse.builder()
                .message(String.format("Producto '%s' eliminado exitosamente de la sucursal '%s'",
                        request.getProductName(), request.getBranchName()))
                .success(true)
                .build();
    }
}

