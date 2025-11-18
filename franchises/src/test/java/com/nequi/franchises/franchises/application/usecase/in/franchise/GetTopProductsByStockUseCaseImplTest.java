package com.nequi.franchises.franchises.application.usecase.in.franchise;

import com.nequi.franchises.franchises.application.dto.response.TopProductStockResponse;
import com.nequi.franchises.franchises.application.mapper.DomainMapper;
import com.nequi.franchises.franchises.domain.exception.NotFoundException;
import com.nequi.franchises.franchises.domain.model.Branch;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetTopProductsByStockUseCaseImpl - Pruebas Unitarias")
class GetTopProductsByStockUseCaseImplTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepository;

    @Mock
    private DomainMapper mapper;

    @InjectMocks
    private GetTopProductsByStockUseCaseImpl useCase;

    @Nested
    @DisplayName("Execute - Success Cases")
    class ExecuteSuccessTests {

        private Franchise franchise;
        private String franchiseId;

        @BeforeEach
        void setUp() {
            franchiseId = "franchise-123";
            franchise = new Franchise("Test Franchise");
            franchise.setId(franchiseId);
        }

        @Test
        @DisplayName("Given existing franchise with single branch and products, When executing use case, Then returns top products sorted by stock")
        void givenExistingFranchiseWithSingleBranchAndProducts_whenExecutingUseCase_thenReturnsTopProductsSortedByStock() {
            // Arrange
            Branch branch = new Branch("Sucursal Centro");
            branch.addProduct(new Product("Hamburguesa", 50));
            branch.addProduct(new Product("Pizza", 150));
            branch.addProduct(new Product("Papas", 100));
            franchise.addBranch(branch);

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));

            // Act
            Mono<List<TopProductStockResponse>> result = useCase.execute(franchiseId);

            // Assert
            StepVerifier.create(result)
                    .assertNext(responseList -> {
                        assertNotNull(responseList);
                        assertEquals(1, responseList.size());
                        assertEquals("Sucursal Centro", responseList.get(0).getBranchName());
                        assertEquals("Pizza", responseList.get(0).getProductName());
                        assertEquals(150, responseList.get(0).getStock());
                    })
                    .verifyComplete();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verifyNoInteractions(mapper);
        }

        @Test
        @DisplayName("Given existing franchise with multiple branches and products, When executing use case, Then returns top products from each branch sorted by stock")
        void givenExistingFranchiseWithMultipleBranchesAndProducts_whenExecutingUseCase_thenReturnsTopProductsFromEachBranchSortedByStock() {
            // Arrange
            Branch branch1 = new Branch("Sucursal Norte");
            branch1.addProduct(new Product("Hamburguesa", 50));
            branch1.addProduct(new Product("Pizza", 300));

            Branch branch2 = new Branch("Sucursal Sur");
            branch2.addProduct(new Product("Papas", 100));
            branch2.addProduct(new Product("Refresco", 500));

            Branch branch3 = new Branch("Sucursal Este");
            branch3.addProduct(new Product("Helado", 200));

            franchise.addBranch(branch1);
            franchise.addBranch(branch2);
            franchise.addBranch(branch3);

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));

            // Act
            Mono<List<TopProductStockResponse>> result = useCase.execute(franchiseId);

            // Assert
            StepVerifier.create(result)
                    .assertNext(responseList -> {
                        assertNotNull(responseList);
                        assertEquals(3, responseList.size());

                        // Verificar ordenamiento descendente por stock
                        assertEquals("Refresco", responseList.get(0).getProductName());
                        assertEquals(500, responseList.get(0).getStock());
                        assertEquals("Sucursal Sur", responseList.get(0).getBranchName());

                        assertEquals("Pizza", responseList.get(1).getProductName());
                        assertEquals(300, responseList.get(1).getStock());
                        assertEquals("Sucursal Norte", responseList.get(1).getBranchName());

                        assertEquals("Helado", responseList.get(2).getProductName());
                        assertEquals(200, responseList.get(2).getStock());
                        assertEquals("Sucursal Este", responseList.get(2).getBranchName());
                    })
                    .verifyComplete();

            verify(franchiseRepository, times(1)).findById(franchiseId);
        }

        @Test
        @DisplayName("Given existing franchise with single product per branch, When executing use case, Then returns all products")
        void givenExistingFranchiseWithSingleProductPerBranch_whenExecutingUseCase_thenReturnsAllProducts() {
            // Arrange
            Branch branch1 = new Branch("Sucursal A");
            branch1.addProduct(new Product("Producto A", 100));

            Branch branch2 = new Branch("Sucursal B");
            branch2.addProduct(new Product("Producto B", 200));

            franchise.addBranch(branch1);
            franchise.addBranch(branch2);

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));

            // Act
            Mono<List<TopProductStockResponse>> result = useCase.execute(franchiseId);

            // Assert
            StepVerifier.create(result)
                    .assertNext(responseList -> {
                        assertNotNull(responseList);
                        assertEquals(2, responseList.size());
                    })
                    .verifyComplete();

            verify(franchiseRepository, times(1)).findById(franchiseId);
        }

        @Test
        @DisplayName("Given existing franchise with products having same stock, When executing use case, Then returns all products correctly")
        void givenExistingFranchiseWithProductsHavingSameStock_whenExecutingUseCase_thenReturnsAllProductsCorrectly() {
            // Arrange
            Branch branch1 = new Branch("Sucursal A");
            branch1.addProduct(new Product("Producto A", 100));
            branch1.addProduct(new Product("Producto B", 100));

            Branch branch2 = new Branch("Sucursal B");
            branch2.addProduct(new Product("Producto C", 100));

            franchise.addBranch(branch1);
            franchise.addBranch(branch2);

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));

            // Act
            Mono<List<TopProductStockResponse>> result = useCase.execute(franchiseId);

            // Assert
            StepVerifier.create(result)
                    .assertNext(responseList -> {
                        assertNotNull(responseList);
                        assertEquals(2, responseList.size());
                        // Ambos deberían tener stock 100
                        assertEquals(100, responseList.get(0).getStock());
                        assertEquals(100, responseList.get(1).getStock());
                    })
                    .verifyComplete();

            verify(franchiseRepository, times(1)).findById(franchiseId);
        }
    }

    @Nested
    @DisplayName("Execute - Error Cases")
    class ExecuteErrorTests {

        private String franchiseId;

        @BeforeEach
        void setUp() {
            franchiseId = "non-existent-id";
        }

        @Test
        @DisplayName("Given non-existent franchise ID, When executing use case, Then throws NotFoundException")
        void givenNonExistentFranchiseId_whenExecutingUseCase_thenThrowsNotFoundException() {
            // Arrange
            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.empty());

            // Act
            Mono<List<TopProductStockResponse>> result = useCase.execute(franchiseId);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof NotFoundException &&
                                    throwable.getMessage().contains("Franquicia no encontrada con ID: " + franchiseId))
                    .verify();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verifyNoInteractions(mapper);
        }

        @Test
        @DisplayName("Given franchise without branches, When executing use case, Then throws IllegalStateException")
        void givenFranchiseWithoutBranches_whenExecutingUseCase_thenThrowsIllegalStateException() {
            // Arrange
            Franchise franchise = new Franchise("Test Franchise");
            franchise.setId(franchiseId);

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));

            // Act
            Mono<List<TopProductStockResponse>> result = useCase.execute(franchiseId);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof IllegalStateException &&
                                    throwable.getMessage().contains("La franquicia no tiene sucursales registradas"))
                    .verify();

            verify(franchiseRepository, times(1)).findById(franchiseId);
        }

        @Test
        @DisplayName("Given franchise with branch without products, When executing use case, Then throws IllegalStateException")
        void givenFranchiseWithBranchWithoutProducts_whenExecutingUseCase_thenThrowsIllegalStateException() {
            // Arrange
            Franchise franchise = new Franchise("Test Franchise");
            franchise.setId(franchiseId);
            Branch branch = new Branch("Sucursal Sin Productos");
            franchise.addBranch(branch);

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));

            // Act
            Mono<List<TopProductStockResponse>> result = useCase.execute(franchiseId);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof IllegalStateException &&
                                    throwable.getMessage().contains("La sucursal no tiene productos"))
                    .verify();

            verify(franchiseRepository, times(1)).findById(franchiseId);
        }

        @Test
        @DisplayName("Given repository error, When executing use case, Then propagates error")
        void givenRepositoryError_whenExecutingUseCase_thenPropagatesError() {
            // Arrange
            RuntimeException repositoryError = new RuntimeException("Database connection error");
            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.error(repositoryError));

            // Act
            Mono<List<TopProductStockResponse>> result = useCase.execute(franchiseId);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof RuntimeException &&
                                    throwable.getMessage().contains("Database connection error"))
                    .verify();

            verify(franchiseRepository, times(1)).findById(franchiseId);
        }
    }


    @Nested
    @DisplayName("Execute - Edge Cases")
    class ExecuteEdgeCasesTests {

        private Franchise franchise;
        private String franchiseId;

        @BeforeEach
        void setUp() {
            franchiseId = "franchise-123";
            franchise = new Franchise("Test Franchise");
            franchise.setId(franchiseId);
        }

        @Test
        @DisplayName("Given franchise with products having zero stock, When executing use case, Then returns products with zero stock")
        void givenFranchiseWithProductsHavingZeroStock_whenExecutingUseCase_thenReturnsProductsWithZeroStock() {
            // Arrange
            Branch branch1 = new Branch("Sucursal A");
            branch1.addProduct(new Product("Producto A", 0));
            branch1.addProduct(new Product("Producto B", 10));

            Branch branch2 = new Branch("Sucursal B");
            branch2.addProduct(new Product("Producto C", 0));

            franchise.addBranch(branch1);
            franchise.addBranch(branch2);

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));

            // Act
            Mono<List<TopProductStockResponse>> result = useCase.execute(franchiseId);

            // Assert
            StepVerifier.create(result)
                    .assertNext(responseList -> {
                        assertNotNull(responseList);
                        assertEquals(2, responseList.size());
                        // El producto con mayor stock debería ser primero
                        assertEquals("Producto B", responseList.get(0).getProductName());
                        assertEquals(10, responseList.get(0).getStock());
                    })
                    .verifyComplete();

            verify(franchiseRepository, times(1)).findById(franchiseId);
        }

        @Test
        @DisplayName("Given franchise with very large stock values, When executing use case, Then returns products correctly")
        void givenFranchiseWithVeryLargeStockValues_whenExecutingUseCase_thenReturnsProductsCorrectly() {
            // Arrange
            Branch branch = new Branch("Sucursal Grande");
            branch.addProduct(new Product("Producto A", 1000000));
            branch.addProduct(new Product("Producto B", 5000000));
            branch.addProduct(new Product("Producto C", 2000000));
            franchise.addBranch(branch);

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));

            // Act
            Mono<List<TopProductStockResponse>> result = useCase.execute(franchiseId);

            // Assert
            StepVerifier.create(result)
                    .assertNext(responseList -> {
                        assertNotNull(responseList);
                        assertEquals(1, responseList.size());
                        assertEquals("Producto B", responseList.get(0).getProductName());
                        assertEquals(5000000, responseList.get(0).getStock());
                    })
                    .verifyComplete();

            verify(franchiseRepository, times(1)).findById(franchiseId);
        }

        @Test
        @DisplayName("Given franchise with many branches, When executing use case, Then returns all top products")
        void givenFranchiseWithManyBranches_whenExecutingUseCase_thenReturnsAllTopProducts() {
            // Arrange
            for (int i = 1; i <= 10; i++) {
                Branch branch = new Branch("Sucursal " + i);
                branch.addProduct(new Product("Producto " + i, i * 10));
                franchise.addBranch(branch);
            }

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));

            // Act
            Mono<List<TopProductStockResponse>> result = useCase.execute(franchiseId);

            // Assert
            StepVerifier.create(result)
                    .assertNext(responseList -> {
                        assertNotNull(responseList);
                        assertEquals(10, responseList.size());
                        // Verificar que está ordenado descendente
                        assertEquals(100, responseList.get(0).getStock());
                        assertEquals(10, responseList.get(9).getStock());
                    })
                    .verifyComplete();

            verify(franchiseRepository, times(1)).findById(franchiseId);
        }
    }

    @Nested
    @DisplayName("Repository Interactions")
    class RepositoryInteractionTests {

        private Franchise franchise;
        private String franchiseId;

        @BeforeEach
        void setUp() {
            franchiseId = "franchise-123";
            franchise = new Franchise("Test Franchise");
            franchise.setId(franchiseId);
        }

        @Test
        @DisplayName("Given successful execution, When executing use case, Then repository is called exactly once")
        void givenSuccessfulExecution_whenExecutingUseCase_thenRepositoryIsCalledExactlyOnce() {
            // Arrange
            Branch branch = new Branch("Sucursal");
            branch.addProduct(new Product("Producto", 100));
            franchise.addBranch(branch);

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));

            // Act
            useCase.execute(franchiseId).block();

            // Assert
            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, never()).save(any());
            verify(franchiseRepository, never()).findByName(anyString());
        }

        @Test
        @DisplayName("Given error case, When executing use case, Then repository is called and mapper is not called")
        void givenErrorCase_whenExecutingUseCase_thenRepositoryIsCalledAndMapperIsNotCalled() {
            // Arrange
            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.empty());

            // Act
            useCase.execute(franchiseId)
                    .onErrorResume(error -> Mono.empty())
                    .block();

            // Assert
            verify(franchiseRepository, times(1)).findById(franchiseId);
            verifyNoInteractions(mapper);
        }
    }
}

