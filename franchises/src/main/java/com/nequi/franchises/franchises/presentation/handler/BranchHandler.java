package com.nequi.franchises.franchises.presentation.handler;

import com.nequi.franchises.franchises.application.dto.request.AddBranchRequest;
import com.nequi.franchises.franchises.application.dto.request.UpdateBranchNameRequest;
import com.nequi.franchises.franchises.application.usecase.in.branch.AddBranchUseCase;
import com.nequi.franchises.franchises.application.usecase.in.branch.UpdateBranchNameUseCase;
import com.nequi.franchises.franchises.domain.exception.DomainException;
import com.nequi.franchises.franchises.domain.exception.DuplicateEntityException;
import com.nequi.franchises.franchises.domain.exception.InvalidOperationException;
import com.nequi.franchises.franchises.domain.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class BranchHandler {

    private final AddBranchUseCase addBranchUseCase;
    private final UpdateBranchNameUseCase updateBranchNameUseCase;

    // ========================================
    // ADD BRANCH
    // ========================================
    public Mono<ServerResponse> addBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");

        return request.bodyToMono(AddBranchRequest.class)
                .doOnNext(req -> log.info("Received request to add branch: {} to franchise: {}",
                        req.getBranchName(), franchiseId))
                .flatMap(branchRequest -> addBranchUseCase.execute(franchiseId, branchRequest))
                .flatMap(response -> ServerResponse.status(HttpStatus.CREATED).bodyValue(response))
                .onErrorResume(this::handleError);
    }

    // ========================================
    // UPDATE BRANCH NAME
    // ========================================
    public Mono<ServerResponse> updateBranchName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");

        return request.bodyToMono(UpdateBranchNameRequest.class)
                .doOnNext(req -> log.info("Received request to update branch name: {} -> {} in franchise: {}",
                        req.getOldName(), req.getNewName(), franchiseId))
                .flatMap(updateRequest -> updateBranchNameUseCase.execute(franchiseId, updateRequest))
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(this::handleError);
    }

    // ========================================
    // ERROR HANDLER
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
    // ERROR RESPONSE DTO
    // ========================================
    @lombok.Data
    @lombok.Builder
    private static class ErrorResponse {
        private String message;
        private String error;
        private int status;
    }
}

