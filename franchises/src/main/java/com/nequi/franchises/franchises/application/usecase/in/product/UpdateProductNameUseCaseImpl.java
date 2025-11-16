package com.nequi.franchises.franchises.application.usecase.in.product;


import com.nequi.franchises.franchises.application.dto.request.UpdateProductNameRequest;
import com.nequi.franchises.franchises.application.dto.response.ProductResponse;
import com.nequi.franchises.franchises.application.mapper.DomainMapper;
import com.nequi.franchises.franchises.domain.exception.DuplicateEntityException;
import com.nequi.franchises.franchises.domain.exception.InvalidOperationException;
import com.nequi.franchises.franchises.domain.exception.NotFoundException;
import com.nequi.franchises.franchises.domain.model.Branch;
import com.nequi.franchises.franchises.domain.model.Franchise;
import com.nequi.franchises.franchises.domain.model.Product;

import com.nequi.franchises.franchises.domain.port.out.FranchiseRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateProductNameUseCaseImpl implements UpdateProductNameUseCase {

    private final FranchiseRepositoryPort franchiseRepository;
    private final DomainMapper mapper;

    @Override
    public Mono<ProductResponse> execute(String franchiseId, UpdateProductNameRequest request) {
        log.info("Executing UpdateProductName: franchiseId={}, branchName={}, productName={}, newName={}",
                franchiseId, request.getBranchName(), request.getProductName(), request.getNewName());

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException(
                        "Franquicia no encontrada con ID: " + franchiseId)))
                .flatMap(franchise -> validateAndUpdateProductName(franchise, request))
                .doOnSuccess(response -> log.info("Product name updated successfully: {} -> {}",
                        request.getProductName(), request.getNewName()))
                .doOnError(error -> log.error("Error updating product name: {}", error.getMessage()));
    }

    private Mono<ProductResponse> validateAndUpdateProductName(Franchise franchise, UpdateProductNameRequest request) {
        try {
            // Validación: el nuevo nombre debe ser diferente al actual
            if (request.getProductName().trim().equalsIgnoreCase(request.getNewName().trim())) {
                return Mono.error(new InvalidOperationException(
                        "El nuevo nombre debe ser diferente al nombre actual del producto"));
            }

            // Validación: verificar que no exista otro producto con el nuevo nombre en la misma sucursal
            Branch branch = findBranchByName(franchise, request.getBranchName());
            if (productExistsWithName(branch, request.getNewName())) {
                return Mono.error(new DuplicateEntityException(
                        String.format("Ya existe un producto con el nombre '%s' en la sucursal '%s'",
                                request.getNewName(), request.getBranchName())));
            }

            // Delegar al agregado (validaciones automáticas del dominio)
            franchise.updateProductName(
                    request.getBranchName(),
                    request.getProductName(),
                    request.getNewName()
            );

            // Obtener el producto actualizado para la respuesta
            Product updatedProduct = findProductByName(branch, request.getNewName());

            // Persistir cambios y retornar respuesta
            return franchiseRepository.save(franchise)
                    .map(savedFranchise -> mapper.toProductResponse(updatedProduct));

        } catch (IllegalStateException e) {
            // Capturar errores del dominio y convertirlos en excepciones específicas
            if (e.getMessage().contains("No existe una sucursal")) {
                return Mono.error(new NotFoundException(
                        "Sucursal no encontrada con nombre: " + request.getBranchName()));
            } else if (e.getMessage().contains("no existe en la sucursal")) {
                return Mono.error(new NotFoundException(
                        String.format("Producto no encontrado con nombre: '%s' en la sucursal: '%s'",
                                request.getProductName(), request.getBranchName())));
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

    private boolean productExistsWithName(Branch branch, String productName) {
        return branch.getProducts().stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(productName.trim()));
    }

    private Product findProductByName(Branch branch, String productName) {
        return branch.getProducts().stream()
                .filter(p -> p.getName().equalsIgnoreCase(productName.trim()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        "Producto no encontrado con nombre: " + productName));
    }
}