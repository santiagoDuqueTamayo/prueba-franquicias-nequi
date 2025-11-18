package com.nequi.franchises.franchises.application.usecase.in.franchise;

import com.nequi.franchises.franchises.application.dto.request.AddFranchiseRequest;
import com.nequi.franchises.franchises.application.dto.response.FranchiseResponse;
import com.nequi.franchises.franchises.application.helper.FranchiseTestHelper;
import com.nequi.franchises.franchises.application.mapper.DomainMapper;
import com.nequi.franchises.franchises.domain.model.Franchise;
import com.nequi.franchises.franchises.domain.port.out.FranchiseRepositoryPort;
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
@DisplayName("AddFranchiseUseCaseImpl - Pruebas Unitarias")
class AddFranchiseUseCaseImplTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepository;

    @Mock
    private DomainMapper mapper;

    @InjectMocks
    private AddFranchiseUseCaseImpl useCase;

    @Nested
    @DisplayName("Execute - Success Cases")
    class ExecuteSuccessTests {

        @Test
        @DisplayName("Given valid franchise name, When executing use case, Then franchise is created and saved successfully")
        void givenValidFranchiseName_whenExecutingUseCase_thenFranchiseIsCreatedAndSavedSuccessfully() {
            // Arrange
            String franchiseName = "McDonald's";
            AddFranchiseRequest request = AddFranchiseRequest.builder()
                    .franchiseName(franchiseName)
                    .build();

            Franchise savedFranchise = FranchiseTestHelper.createFranchise("franchise-123", franchiseName);
            FranchiseResponse expectedResponse = FranchiseResponse.builder()
                    .id("franchise-123")
                    .name(franchiseName)
                    .build();

            when(franchiseRepository.save(any(Franchise.class)))
                    .thenReturn(Mono.just(savedFranchise));
            when(mapper.toFranchiseResponse(savedFranchise))
                    .thenReturn(expectedResponse);

            // Act
            Mono<FranchiseResponse> result = useCase.execute(request);

            // Assert
            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertNotNull(response);
                        assertEquals(franchiseName, response.getName());
                        assertEquals("franchise-123", response.getId());
                    })
                    .verifyComplete();

            verify(franchiseRepository, times(1)).save(any(Franchise.class));
            verify(mapper, times(1)).toFranchiseResponse(savedFranchise);
        }

        @Test
        @DisplayName("Given franchise name with spaces, When executing use case, Then franchise is created with trimmed name")
        void givenFranchiseNameWithSpaces_whenExecutingUseCase_thenFranchiseIsCreatedWithTrimmedName() {
            // Arrange
            String franchiseName = "  Burger King  ";
            AddFranchiseRequest request = AddFranchiseRequest.builder()
                    .franchiseName(franchiseName)
                    .build();

            Franchise savedFranchise = FranchiseTestHelper.createFranchise("franchise-456", "Burger King");
            FranchiseResponse expectedResponse = FranchiseResponse.builder()
                    .id("franchise-456")
                    .name("Burger King")
                    .build();

            when(franchiseRepository.save(any(Franchise.class)))
                    .thenReturn(Mono.just(savedFranchise));
            when(mapper.toFranchiseResponse(savedFranchise))
                    .thenReturn(expectedResponse);

            // Act
            Mono<FranchiseResponse> result = useCase.execute(request);

            // Assert
            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertNotNull(response);
                        assertEquals("Burger King", response.getName());
                    })
                    .verifyComplete();

            verify(franchiseRepository, times(1)).save(any(Franchise.class));
        }
    }

    @Nested
    @DisplayName("Execute - Error Cases")
    class ExecuteErrorTests {

        @Test
        @DisplayName("Given null franchise name, When executing use case, Then throws IllegalArgumentException")
        void givenNullFranchiseName_whenExecutingUseCase_thenThrowsIllegalArgumentException() {
            // Arrange
            AddFranchiseRequest request = AddFranchiseRequest.builder()
                    .franchiseName(null)
                    .build();

            // Act
            Mono<FranchiseResponse> result = useCase.execute(request);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof IllegalArgumentException &&
                                    throwable.getMessage().contains("El nombre de la franquicia no puede ser vacío"))
                    .verify();

            verifyNoInteractions(franchiseRepository);
            verifyNoInteractions(mapper);
        }

        @Test
        @DisplayName("Given empty franchise name, When executing use case, Then throws IllegalArgumentException")
        void givenEmptyFranchiseName_whenExecutingUseCase_thenThrowsIllegalArgumentException() {
            // Arrange
            AddFranchiseRequest request = AddFranchiseRequest.builder()
                    .franchiseName("")
                    .build();

            // Act
            Mono<FranchiseResponse> result = useCase.execute(request);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof IllegalArgumentException &&
                                    throwable.getMessage().contains("El nombre de la franquicia no puede ser vacío"))
                    .verify();

            verifyNoInteractions(franchiseRepository);
            verifyNoInteractions(mapper);
        }

        @Test
        @DisplayName("Given repository error, When executing use case, Then propagates error")
        void givenRepositoryError_whenExecutingUseCase_thenPropagatesError() {
            // Arrange
            AddFranchiseRequest request = AddFranchiseRequest.builder()
                    .franchiseName("Test Franchise")
                    .build();

            RuntimeException repositoryError = new RuntimeException("Database connection error");
            when(franchiseRepository.save(any(Franchise.class)))
                    .thenReturn(Mono.error(repositoryError));

            // Act
            Mono<FranchiseResponse> result = useCase.execute(request);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof RuntimeException &&
                                    throwable.getMessage().contains("Database connection error"))
                    .verify();

            verify(franchiseRepository, times(1)).save(any(Franchise.class));
            verifyNoInteractions(mapper);
        }
    }

    @Nested
    @DisplayName("Repository Interactions")
    class RepositoryInteractionTests {

        @Test
        @DisplayName("Given successful execution, When executing use case, Then repository save is called exactly once")
        void givenSuccessfulExecution_whenExecutingUseCase_thenRepositorySaveIsCalledExactlyOnce() {
            // Arrange
            AddFranchiseRequest request = AddFranchiseRequest.builder()
                    .franchiseName("Test Franchise")
                    .build();

            Franchise savedFranchise = FranchiseTestHelper.createBasicFranchise();
            FranchiseResponse response = FranchiseResponse.builder()
                    .id(savedFranchise.getId())
                    .name(savedFranchise.getName())
                    .build();

            when(franchiseRepository.save(any(Franchise.class)))
                    .thenReturn(Mono.just(savedFranchise));
            when(mapper.toFranchiseResponse(savedFranchise))
                    .thenReturn(response);

            // Act
            useCase.execute(request).block();

            // Assert
            verify(franchiseRepository, times(1)).save(any(Franchise.class));
            verify(franchiseRepository, never()).findById(anyString());
            verify(franchiseRepository, never()).findByName(anyString());
        }

        @Test
        @DisplayName("Given successful execution, When executing use case, Then mapper is called exactly once")
        void givenSuccessfulExecution_whenExecutingUseCase_thenMapperIsCalledExactlyOnce() {
            // Arrange
            AddFranchiseRequest request = AddFranchiseRequest.builder()
                    .franchiseName("Test Franchise")
                    .build();

            Franchise savedFranchise = FranchiseTestHelper.createBasicFranchise();
            FranchiseResponse response = FranchiseResponse.builder()
                    .id(savedFranchise.getId())
                    .name(savedFranchise.getName())
                    .build();

            when(franchiseRepository.save(any(Franchise.class)))
                    .thenReturn(Mono.just(savedFranchise));
            when(mapper.toFranchiseResponse(savedFranchise))
                    .thenReturn(response);

            // Act
            useCase.execute(request).block();

            // Assert
            verify(mapper, times(1)).toFranchiseResponse(savedFranchise);
        }
    }
}

