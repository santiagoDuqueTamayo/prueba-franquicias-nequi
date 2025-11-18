package com.nequi.franchises.franchises.application.usecase.in.product;

import com.nequi.franchises.franchises.application.dto.request.RemoveProductRequest;
import com.nequi.franchises.franchises.application.dto.response.MessageResponse;
import com.nequi.franchises.franchises.application.helper.FranchiseTestHelper;
import com.nequi.franchises.franchises.application.helper.ProductTestHelper;
import com.nequi.franchises.franchises.domain.exception.NotFoundException;
import com.nequi.franchises.franchises.domain.model.Franchise;
import com.nequi.franchises.franchises.domain.port.out.FranchiseRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RemoveProductFromBranchUseCaseImpl - Pruebas Unitarias")
class RemoveProductFromBranchUseCaseImplTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepository;

    @InjectMocks
    private RemoveProductFromBranchUseCaseImpl useCase;

    private String franchiseId;
    private Franchise franchise;

    @BeforeEach
    void setUp() {
        franchiseId = FranchiseTestHelper.DEFAULT_FRANCHISE_ID;
        franchise = FranchiseTestHelper.createFranchiseWithBranchAndProduct();
    }

    @Nested
    @DisplayName("Execute - Success Cases")
    class ExecuteSuccessTests {

        @Test
        @DisplayName("Given existing franchise branch and product, When executing use case, Then product is removed successfully")
        void givenExistingFranchiseBranchAndProduct_whenExecutingUseCase_thenProductIsRemovedSuccessfully() {
            // Arrange
            RemoveProductRequest request = RemoveProductRequest.builder()
                    .branchName(FranchiseTestHelper.DEFAULT_BRANCH_NAME)
                    .productName(ProductTestHelper.DEFAULT_PRODUCT_NAME)
                    .build();

            Franchise savedFranchise = FranchiseTestHelper.createFranchiseWithBranch();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));
            when(franchiseRepository.save(any(Franchise.class)))
                    .thenReturn(Mono.just(savedFranchise));

            // Act
            Mono<MessageResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertNotNull(response);
                        assertTrue(response.isSuccess());
                        assertTrue(response.getMessage().contains(ProductTestHelper.DEFAULT_PRODUCT_NAME));
                        assertTrue(response.getMessage().contains(FranchiseTestHelper.DEFAULT_BRANCH_NAME));
                    })
                    .verifyComplete();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, times(1)).save(any(Franchise.class));
        }
    }

    @Nested
    @DisplayName("Execute - Error Cases")
    class ExecuteErrorTests {

        @Test
        @DisplayName("Given non-existent franchise ID, When executing use case, Then throws NotFoundException")
        void givenNonExistentFranchiseId_whenExecutingUseCase_thenThrowsNotFoundException() {
            // Arrange
            String nonExistentId = "non-existent-id";
            RemoveProductRequest request = RemoveProductRequest.builder()
                    .branchName(FranchiseTestHelper.DEFAULT_BRANCH_NAME)
                    .productName("Producto")
                    .build();

            when(franchiseRepository.findById(nonExistentId))
                    .thenReturn(Mono.empty());

            // Act
            Mono<MessageResponse> result = useCase.execute(nonExistentId, request);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof NotFoundException &&
                                    throwable.getMessage().contains("Franquicia no encontrada con ID: " + nonExistentId))
                    .verify();

            verify(franchiseRepository, times(1)).findById(nonExistentId);
            verify(franchiseRepository, never()).save(any());
        }

        @Test
        @DisplayName("Given non-existent branch name, When executing use case, Then throws NotFoundException")
        void givenNonExistentBranchName_whenExecutingUseCase_thenThrowsNotFoundException() {
            // Arrange
            RemoveProductRequest request = RemoveProductRequest.builder()
                    .branchName("Sucursal Inexistente")
                    .productName("Producto")
                    .build();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));

            // Act
            Mono<MessageResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof NotFoundException &&
                                    throwable.getMessage().contains("Sucursal no encontrada"))
                    .verify();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, never()).save(any());
        }

        @Test
        @DisplayName("Given non-existent product name, When executing use case, Then throws NotFoundException")
        void givenNonExistentProductName_whenExecutingUseCase_thenThrowsNotFoundException() {
            // Arrange
            RemoveProductRequest request = RemoveProductRequest.builder()
                    .branchName(FranchiseTestHelper.DEFAULT_BRANCH_NAME)
                    .productName("Producto Inexistente")
                    .build();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));

            // Act
            Mono<MessageResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof NotFoundException &&
                                    throwable.getMessage().contains("Producto no encontrado"))
                    .verify();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, never()).save(any());
        }
    }
}

