package com.nequi.franchises.franchises.application.usecase.in.product;

import com.nequi.franchises.franchises.application.dto.request.UpdateProductNameRequest;
import com.nequi.franchises.franchises.application.dto.response.ProductResponse;
import com.nequi.franchises.franchises.application.helper.FranchiseTestHelper;
import com.nequi.franchises.franchises.application.helper.ProductTestHelper;
import com.nequi.franchises.franchises.application.mapper.DomainMapper;
import com.nequi.franchises.franchises.domain.exception.DuplicateEntityException;
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
@DisplayName("UpdateProductNameUseCaseImpl - Pruebas Unitarias")
class UpdateProductNameUseCaseImplTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepository;

    @Mock
    private DomainMapper mapper;

    @InjectMocks
    private UpdateProductNameUseCaseImpl useCase;

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
        @DisplayName("Given existing franchise branch and product with different new name, When executing use case, Then product name is updated successfully")
        void givenExistingFranchiseBranchAndProductWithDifferentNewName_whenExecutingUseCase_thenProductNameIsUpdatedSuccessfully() {
            // Arrange
            String oldName = ProductTestHelper.DEFAULT_PRODUCT_NAME;
            String newName = "Pizza Grande";
            UpdateProductNameRequest request = UpdateProductNameRequest.builder()
                    .branchName(FranchiseTestHelper.DEFAULT_BRANCH_NAME)
                    .productName(oldName)
                    .newName(newName)
                    .build();

            Franchise savedFranchise = FranchiseTestHelper.createFranchiseWithBranchAndProduct();
            ProductResponse expectedResponse = ProductResponse.builder()
                    .name(newName)
                    .stock(ProductTestHelper.DEFAULT_PRODUCT_STOCK)
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
                        assertEquals(newName, response.getName());
                        assertEquals(ProductTestHelper.DEFAULT_PRODUCT_STOCK, response.getStock());
                    })
                    .verifyComplete();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, times(1)).save(any(Franchise.class));
            verify(mapper, times(1)).toProductResponse(any());
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
            UpdateProductNameRequest request = UpdateProductNameRequest.builder()
                    .branchName(FranchiseTestHelper.DEFAULT_BRANCH_NAME)
                    .productName("Producto")
                    .newName("Nuevo Nombre")
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
        @DisplayName("Given same old and new product name, When executing use case, Then throws InvalidOperationException")
        void givenSameOldAndNewProductName_whenExecutingUseCase_thenThrowsInvalidOperationException() {
            // Arrange
            String productName = ProductTestHelper.DEFAULT_PRODUCT_NAME;
            UpdateProductNameRequest request = UpdateProductNameRequest.builder()
                    .branchName(FranchiseTestHelper.DEFAULT_BRANCH_NAME)
                    .productName(productName)
                    .newName(productName)
                    .build();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));

            // Act
            Mono<ProductResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof InvalidOperationException &&
                                    throwable.getMessage().contains("El nuevo nombre debe ser diferente"))
                    .verify();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, never()).save(any());
            verifyNoInteractions(mapper);
        }

        @Test
        @DisplayName("Given duplicate new product name, When executing use case, Then throws DuplicateEntityException")
        void givenDuplicateNewProductName_whenExecutingUseCase_thenThrowsDuplicateEntityException() {
            // Arrange
            String oldName = ProductTestHelper.DEFAULT_PRODUCT_NAME;
            String newName = "Producto Duplicado";
            UpdateProductNameRequest request = UpdateProductNameRequest.builder()
                    .branchName(FranchiseTestHelper.DEFAULT_BRANCH_NAME)
                    .productName(oldName)
                    .newName(newName)
                    .build();

            Franchise franchiseWithMultipleProducts = FranchiseTestHelper.createFranchiseWithBranchAndMultipleProducts(2);
            franchiseWithMultipleProducts.addProductToBranch(
                    FranchiseTestHelper.DEFAULT_BRANCH_NAME,
                    ProductTestHelper.createProduct(newName, 50)
            );

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchiseWithMultipleProducts));

            // Act
            Mono<ProductResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof DuplicateEntityException &&
                                    throwable.getMessage().contains("Ya existe un producto con el nombre"))
                    .verify();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, never()).save(any());
            verifyNoInteractions(mapper);
        }
    }
}

