package com.nequi.franchises.franchises.application.usecase.in.product;

import com.nequi.franchises.franchises.application.dto.request.UpdateProductStockRequest;
import com.nequi.franchises.franchises.application.dto.response.ProductResponse;
import com.nequi.franchises.franchises.application.helper.FranchiseTestHelper;
import com.nequi.franchises.franchises.application.helper.ProductTestHelper;
import com.nequi.franchises.franchises.application.mapper.DomainMapper;
import com.nequi.franchises.franchises.domain.exception.InvalidOperationException;
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
@DisplayName("UpdateProductStockUseCaseImpl - Pruebas Unitarias")
class UpdateProductStockUseCaseImplTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepository;

    @Mock
    private DomainMapper mapper;

    @InjectMocks
    private UpdateProductStockUseCaseImpl useCase;

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
        @DisplayName("Given existing franchise branch and product with different new stock, When executing use case, Then product stock is updated successfully")
        void givenExistingFranchiseBranchAndProductWithDifferentNewStock_whenExecutingUseCase_thenProductStockIsUpdatedSuccessfully() {
            // Arrange
            int newStock = 200;
            UpdateProductStockRequest request = UpdateProductStockRequest.builder()
                    .branchName(FranchiseTestHelper.DEFAULT_BRANCH_NAME)
                    .productName(ProductTestHelper.DEFAULT_PRODUCT_NAME)
                    .newStock(newStock)
                    .build();

            Franchise savedFranchise = FranchiseTestHelper.createFranchiseWithBranchAndProduct();
            ProductResponse expectedResponse = ProductResponse.builder()
                    .name(ProductTestHelper.DEFAULT_PRODUCT_NAME)
                    .stock(newStock)
                    .build();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));
            when(franchiseRepository.save(any(Franchise.class)))
                    .thenReturn(Mono.just(savedFranchise));
            when(mapper.toProductResponse(any()))
                    .thenReturn(expectedResponse);

            // Act
            Mono<ProductResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertNotNull(response);
                        assertEquals(newStock, response.getStock());
                        assertEquals(ProductTestHelper.DEFAULT_PRODUCT_NAME, response.getName());
                    })
                    .verifyComplete();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, times(1)).save(any(Franchise.class));
            verify(mapper, times(1)).toProductResponse(any());
        }

        @Test
        @DisplayName("Given product with zero stock update, When executing use case, Then product stock is updated to zero successfully")
        void givenProductWithZeroStockUpdate_whenExecutingUseCase_thenProductStockIsUpdatedToZeroSuccessfully() {
            // Arrange
            int newStock = 0;
            UpdateProductStockRequest request = UpdateProductStockRequest.builder()
                    .branchName(FranchiseTestHelper.DEFAULT_BRANCH_NAME)
                    .productName(ProductTestHelper.DEFAULT_PRODUCT_NAME)
                    .newStock(newStock)
                    .build();

            Franchise savedFranchise = FranchiseTestHelper.createFranchiseWithBranchAndProduct();
            ProductResponse expectedResponse = ProductResponse.builder()
                    .name(ProductTestHelper.DEFAULT_PRODUCT_NAME)
                    .stock(0)
                    .build();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));
            when(franchiseRepository.save(any(Franchise.class)))
                    .thenReturn(Mono.just(savedFranchise));
            when(mapper.toProductResponse(any()))
                    .thenReturn(expectedResponse);

            // Act
            Mono<ProductResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertNotNull(response);
                        assertEquals(0, response.getStock());
                    })
                    .verifyComplete();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, times(1)).save(any(Franchise.class));
        }
    }

    // ========================================================
    // EXECUTE - Error Cases
    // ========================================================

    @Nested
    @DisplayName("Execute - Error Cases")
    class ExecuteErrorTests {

        @Test
        @DisplayName("Given non-existent franchise ID, When executing use case, Then throws NotFoundException")
        void givenNonExistentFranchiseId_whenExecutingUseCase_thenThrowsNotFoundException() {
            // Arrange
            String nonExistentId = "non-existent-id";
            UpdateProductStockRequest request = UpdateProductStockRequest.builder()
                    .branchName(FranchiseTestHelper.DEFAULT_BRANCH_NAME)
                    .productName("Producto")
                    .newStock(100)
                    .build();

            when(franchiseRepository.findById(nonExistentId))
                    .thenReturn(Mono.empty());

            // Act
            Mono<ProductResponse> result = useCase.execute(nonExistentId, request);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof NotFoundException &&
                                    throwable.getMessage().contains("Franquicia no encontrada con ID: " + nonExistentId))
                    .verify();

            verify(franchiseRepository, times(1)).findById(nonExistentId);
            verify(franchiseRepository, never()).save(any());
            verifyNoInteractions(mapper);
        }

        @Test
        @DisplayName("Given negative new stock, When executing use case, Then throws InvalidOperationException")
        void givenNegativeNewStock_whenExecutingUseCase_thenThrowsInvalidOperationException() {
            // Arrange
            UpdateProductStockRequest request = UpdateProductStockRequest.builder()
                    .branchName(FranchiseTestHelper.DEFAULT_BRANCH_NAME)
                    .productName(ProductTestHelper.DEFAULT_PRODUCT_NAME)
                    .newStock(-1)
                    .build();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));

            // Act
            Mono<ProductResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof InvalidOperationException &&
                                    throwable.getMessage().contains("El stock no puede ser negativo"))
                    .verify();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, never()).save(any());
            verifyNoInteractions(mapper);
        }

        @Test
        @DisplayName("Given non-existent branch name, When executing use case, Then throws NotFoundException")
        void givenNonExistentBranchName_whenExecutingUseCase_thenThrowsNotFoundException() {
            // Arrange
            UpdateProductStockRequest request = UpdateProductStockRequest.builder()
                    .branchName("Sucursal Inexistente")
                    .productName(ProductTestHelper.DEFAULT_PRODUCT_NAME)
                    .newStock(200)
                    .build();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));

            // Act
            Mono<ProductResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof NotFoundException &&
                                    throwable.getMessage().contains("Sucursal no encontrada"))
                    .verify();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, never()).save(any());
            verifyNoInteractions(mapper);
        }

        @Test
        @DisplayName("Given non-existent product name, When executing use case, Then throws NotFoundException")
        void givenNonExistentProductName_whenExecutingUseCase_thenThrowsNotFoundException() {
            // Arrange
            UpdateProductStockRequest request = UpdateProductStockRequest.builder()
                    .branchName(FranchiseTestHelper.DEFAULT_BRANCH_NAME)
                    .productName("Producto Inexistente")
                    .newStock(200)
                    .build();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));

            // Act
            Mono<ProductResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof NotFoundException &&
                                    throwable.getMessage().contains("Producto no encontrado"))
                    .verify();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, never()).save(any());
            verifyNoInteractions(mapper);
        }
    }
}

