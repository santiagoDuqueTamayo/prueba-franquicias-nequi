package com.nequi.franchises.franchises.application.usecase.in.product;

import com.nequi.franchises.franchises.application.dto.request.AddProductRequest;
import com.nequi.franchises.franchises.application.dto.response.ProductResponse;
import com.nequi.franchises.franchises.application.helper.FranchiseTestHelper;
import com.nequi.franchises.franchises.application.helper.ProductTestHelper;
import com.nequi.franchises.franchises.application.mapper.DomainMapper;
import com.nequi.franchises.franchises.domain.exception.NotFoundException;
import com.nequi.franchises.franchises.domain.model.Franchise;
import com.nequi.franchises.franchises.domain.model.Product;
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
@DisplayName("AddProductToBranchUseCaseImpl - Pruebas Unitarias")
class AddProductToBranchUseCaseImplTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepository;

    @Mock
    private DomainMapper mapper;

    @InjectMocks
    private AddProductToBranchUseCaseImpl useCase;

    private String franchiseId;
    private Franchise franchise;

    @BeforeEach
    void setUp() {
        franchiseId = FranchiseTestHelper.DEFAULT_FRANCHISE_ID;
        franchise = FranchiseTestHelper.createFranchiseWithBranch();
    }

    @Nested
    @DisplayName("Execute - Success Cases")
    class ExecuteSuccessTests {

        @Test
        @DisplayName("Given existing franchise and branch with unique product name, When executing use case, Then product is added successfully")
        void givenExistingFranchiseAndBranchWithUniqueProductName_whenExecutingUseCase_thenProductIsAddedSuccessfully() {
            // Arrange
            String productName = "Pizza";
            int stock = 50;
            AddProductRequest request = AddProductRequest.builder()
                    .branchName(FranchiseTestHelper.DEFAULT_BRANCH_NAME)
                    .productName(productName)
                    .stock(stock)
                    .build();

            Franchise savedFranchise = FranchiseTestHelper.createFranchiseWithBranchAndProduct();
            ProductResponse expectedResponse = ProductResponse.builder()
                    .name(productName)
                    .stock(stock)
                    .build();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));
            when(franchiseRepository.save(any(Franchise.class)))
                    .thenReturn(Mono.just(savedFranchise));
            when(mapper.toProductResponse(any(Product.class)))
                    .thenReturn(expectedResponse);

            // Act
            Mono<ProductResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertNotNull(response);
                        assertEquals(productName, response.getName());
                        assertEquals(stock, response.getStock());
                    })
                    .verifyComplete();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, times(1)).save(any(Franchise.class));
            verify(mapper, times(1)).toProductResponse(any(Product.class));
        }

        @Test
        @DisplayName("Given product with zero stock, When executing use case, Then product is added successfully")
        void givenProductWithZeroStock_whenExecutingUseCase_thenProductIsAddedSuccessfully() {
            // Arrange
            AddProductRequest request = AddProductRequest.builder()
                    .branchName(FranchiseTestHelper.DEFAULT_BRANCH_NAME)
                    .productName("Producto Sin Stock")
                    .stock(0)
                    .build();

            Franchise savedFranchise = FranchiseTestHelper.createFranchiseWithBranchAndProduct();
            ProductResponse expectedResponse = ProductResponse.builder()
                    .name("Producto Sin Stock")
                    .stock(0)
                    .build();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));
            when(franchiseRepository.save(any(Franchise.class)))
                    .thenReturn(Mono.just(savedFranchise));
            when(mapper.toProductResponse(any(Product.class)))
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

    @Nested
    @DisplayName("Execute - Error Cases")
    class ExecuteErrorTests {

        @Test
        @DisplayName("Given non-existent franchise ID, When executing use case, Then throws NotFoundException")
        void givenNonExistentFranchiseId_whenExecutingUseCase_thenThrowsNotFoundException() {
            // Arrange
            String nonExistentId = "non-existent-id";
            AddProductRequest request = AddProductRequest.builder()
                    .branchName(FranchiseTestHelper.DEFAULT_BRANCH_NAME)
                    .productName("Producto")
                    .stock(10)
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
        @DisplayName("Given non-existent branch name, When executing use case, Then throws IllegalStateException")
        void givenNonExistentBranchName_whenExecutingUseCase_thenThrowsIllegalStateException() {
            // Arrange
            AddProductRequest request = AddProductRequest.builder()
                    .branchName("Sucursal Inexistente")
                    .productName("Producto")
                    .stock(10)
                    .build();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));

            // Act
            Mono<ProductResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof IllegalStateException &&
                                    throwable.getMessage().contains("No existe una sucursal"))
                    .verify();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, never()).save(any());
            verifyNoInteractions(mapper);
        }

        @Test
        @DisplayName("Given duplicate product name, When executing use case, Then throws IllegalStateException")
        void givenDuplicateProductName_whenExecutingUseCase_thenThrowsIllegalStateException() {
            // Arrange
            AddProductRequest request = AddProductRequest.builder()
                    .branchName(FranchiseTestHelper.DEFAULT_BRANCH_NAME)
                    .productName(ProductTestHelper.DEFAULT_PRODUCT_NAME)
                    .stock(10)
                    .build();

            Franchise franchiseWithProduct = FranchiseTestHelper.createFranchiseWithBranchAndProduct();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchiseWithProduct));

            // Act
            Mono<ProductResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof IllegalStateException &&
                                    throwable.getMessage().contains("El producto ya existe en la sucursal"))
                    .verify();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, never()).save(any());
            verifyNoInteractions(mapper);
        }

        @Test
        @DisplayName("Given negative stock, When executing use case, Then throws IllegalArgumentException")
        void givenNegativeStock_whenExecutingUseCase_thenThrowsIllegalArgumentException() {
            // Arrange
            AddProductRequest request = AddProductRequest.builder()
                    .branchName(FranchiseTestHelper.DEFAULT_BRANCH_NAME)
                    .productName("Producto")
                    .stock(-1)
                    .build();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));

            // Act
            Mono<ProductResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof IllegalArgumentException &&
                                    throwable.getMessage().contains("El stock no puede ser negativo"))
                    .verify();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, never()).save(any());
            verifyNoInteractions(mapper);
        }
    }
}

