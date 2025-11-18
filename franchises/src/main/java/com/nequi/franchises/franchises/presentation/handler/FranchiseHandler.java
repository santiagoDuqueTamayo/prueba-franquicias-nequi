package com.nequi.franchises.franchises.presentation.handler;

import com.nequi.franchises.franchises.application.dto.request.AddFranchiseRequest;
import com.nequi.franchises.franchises.application.dto.request.AddProductRequest;
import com.nequi.franchises.franchises.application.dto.request.RemoveProductRequest;
import com.nequi.franchises.franchises.application.dto.request.UpdateFranchiseNameRequest;
import com.nequi.franchises.franchises.application.dto.request.UpdateProductNameRequest;
import com.nequi.franchises.franchises.application.dto.request.UpdateProductStockRequest;
import com.nequi.franchises.franchises.application.usecase.in.franchise.AddFranchiseUseCase;
import com.nequi.franchises.franchises.application.usecase.in.franchise.GetTopProductsByStockUseCase;
import com.nequi.franchises.franchises.application.usecase.in.franchise.ListAllFranchisesUseCase;
import com.nequi.franchises.franchises.application.usecase.in.franchise.UpdateFranchiseNameUseCase;
import com.nequi.franchises.franchises.application.usecase.in.product.AddProductToBranchUseCase;
import com.nequi.franchises.franchises.application.usecase.in.product.RemoveProductFromBranchUseCase;
import com.nequi.franchises.franchises.application.usecase.in.product.UpdateProductNameUseCase;
import com.nequi.franchises.franchises.application.usecase.in.product.UpdateProductStockUseCase;
import com.nequi.franchises.franchises.domain.exception.DomainException;
import com.nequi.franchises.franchises.domain.exception.DuplicateEntityException;
import com.nequi.franchises.franchises.domain.exception.InvalidOperationException;
import com.nequi.franchises.franchises.domain.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class FranchiseHandler {

    private final AddFranchiseUseCase addFranchiseUseCase;
    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    private final GetTopProductsByStockUseCase getTopProductsByStockUseCase;
    private final AddProductToBranchUseCase addProductToBranchUseCase;
    private final RemoveProductFromBranchUseCase removeProductFromBranchUseCase;
    private final UpdateProductNameUseCase updateProductNameUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final ListAllFranchisesUseCase listAllFranchisesUseCase;

    // ========================================
    // ADD FRANCHISE
    // ========================================
    public Mono<ServerResponse> addFranchise(ServerRequest request) {
        return request.bodyToMono(AddFranchiseRequest.class)
                .doOnNext(req -> log.info("Received request to add franchise: {}", req.getFranchiseName()))
                .flatMap(addFranchiseUseCase::execute)
                .flatMap(response -> ServerResponse.status(HttpStatus.CREATED).bodyValue(response))
                .onErrorResume(this::handleError);
    }

    // ========================================
    // UPDATE FRANCHISE NAME
    // ========================================
    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");

        return request.bodyToMono(UpdateFranchiseNameRequest.class)
                .doOnNext(req -> log.info("Received request to update franchise name: {} -> {}",
                        franchiseId, req.getNewName()))
                .flatMap(updateRequest -> updateFranchiseNameUseCase.execute(franchiseId, updateRequest))
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(this::handleError);
    }

    // ========================================
    // ADD PRODUCT (ya existente)
    // ========================================
    public Mono<ServerResponse> addProduct(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");

        return request.bodyToMono(AddProductRequest.class)
                .doOnNext(req -> log.info("Received request to add product: {}", req))
                .flatMap(productRequest -> addProductToBranchUseCase.execute(franchiseId, productRequest))
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(error -> handleError(error));
    }

    // ========================================
    // list franchises
    // ========================================
    public Mono<ServerResponse> getFranchises(ServerRequest request) {
        return listAllFranchisesUseCase.execute()
                .collectList()
                .flatMap(list ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(list)
                )
                .onErrorResume(this::handleError);
    }


    // ========================================
    // REMOVE PRODUCT (nuevo)
    // ========================================
    public Mono<ServerResponse> removeProduct(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");

        return request.bodyToMono(RemoveProductRequest.class)
                .doOnNext(req -> log.info("Received request to remove product: {} from branch: {}",
                        req.getProductName(), req.getBranchName()))
                .flatMap(removeRequest -> removeProductFromBranchUseCase.execute(franchiseId, removeRequest))
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(error -> handleError(error));
    }

    // ========================================
    // UPDATE PRODUCT STOCK (nuevo)
    // ========================================
    public Mono<ServerResponse> updateProductStock(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");

        return request.bodyToMono(UpdateProductStockRequest.class)
                .doOnNext(req -> log.info("Received request to update product stock: {} in branch: {} -> newStock: {}",
                        req.getProductName(), req.getBranchName(), req.getNewStock()))
                .flatMap(updateRequest -> updateProductStockUseCase.execute(franchiseId, updateRequest))
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(this::handleError);
    }


    // ========================================
    // UPDATE PRODUCT NAME (nuevo)
    // ========================================
    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");

        return request.bodyToMono(UpdateProductNameRequest.class)
                .doOnNext(req -> log.info("Received request to update product name: {} -> {} in branch: {}",
                        req.getProductName(), req.getNewName(), req.getBranchName()))
                .flatMap(updateRequest -> updateProductNameUseCase.execute(franchiseId, updateRequest))
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(this::handleError);
    }

    // ========================================
    // GET TOP PRODUCTS BY STOCK
    // ========================================
    public Mono<ServerResponse> getTopProductsByStock(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");

        return Mono.just(franchiseId)
                .doOnNext(id -> log.info("Received request to get top products by stock: franchiseId={}", id))
                .flatMap(getTopProductsByStockUseCase::execute)
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(this::handleError);
    }

    // ========================================
    // ERROR HANDLER (mejorado con nuevos errores)
    // ========================================
    private Mono<ServerResponse> handleError(Throwable error) {
        log.error("Error processing request: {}", error.getMessage(), error);

        // 404 NOT FOUND - Recurso no encontrado
        if (error instanceof NotFoundException) {
            return ServerResponse.status(HttpStatus.NOT_FOUND)
                    .bodyValue(ErrorResponse.builder()
                            .message(error.getMessage())
                            .error("NOT_FOUND")
                            .status(404)
                            .build());
        }

        // 409 CONFLICT - Recurso duplicado
        if (error instanceof DuplicateEntityException) {
            return ServerResponse.status(HttpStatus.CONFLICT)
                    .bodyValue(ErrorResponse.builder()
                            .message(error.getMessage())
                            .error("CONFLICT")
                            .status(409)
                            .build());
        }

        // 400 BAD REQUEST - Operación inválida o validación fallida
        if (error instanceof InvalidOperationException) {
            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .bodyValue(ErrorResponse.builder()
                            .message(error.getMessage())
                            .error("INVALID_OPERATION")
                            .status(400)
                            .build());
        }

        // 400 BAD REQUEST - Argumentos inválidos
        if (error instanceof IllegalArgumentException || error instanceof IllegalStateException) {
            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .bodyValue(ErrorResponse.builder()
                            .message(error.getMessage())
                            .error("BAD_REQUEST")
                            .status(400)
                            .build());
        }

        // 400 BAD REQUEST - Errores genéricos del dominio
        if (error instanceof DomainException) {
            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .bodyValue(ErrorResponse.builder()
                            .message(error.getMessage())
                            .error("DOMAIN_ERROR")
                            .status(400)
                            .build());
        }

        // 500 INTERNAL SERVER ERROR - Errores inesperados
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .bodyValue(ErrorResponse.builder()
                        .message("Error interno del servidor")
                        .error("INTERNAL_SERVER_ERROR")
                        .status(500)
                        .build());
    }

    // ========================================
    // ERROR RESPONSE DTO (mejorado)
    // ========================================
    @lombok.Data
    @lombok.Builder
    private static class ErrorResponse {
        private String message;
        private String error;
        private int status;
    }
}