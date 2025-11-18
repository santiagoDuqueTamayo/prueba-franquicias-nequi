package com.nequi.franchises.franchises.application.usecase.in.franchise;

import com.nequi.franchises.franchises.application.dto.request.UpdateFranchiseNameRequest;
import com.nequi.franchises.franchises.application.dto.response.FranchiseResponse;
import com.nequi.franchises.franchises.application.helper.FranchiseTestHelper;
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
@DisplayName("UpdateFranchiseNameUseCaseImpl - Pruebas Unitarias")
class UpdateFranchiseNameUseCaseImplTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepository;

    @Mock
    private DomainMapper mapper;

    @InjectMocks
    private UpdateFranchiseNameUseCaseImpl useCase;

    private String franchiseId;
    private Franchise franchise;

    @BeforeEach
    void setUp() {
        franchiseId = FranchiseTestHelper.DEFAULT_FRANCHISE_ID;
        franchise = FranchiseTestHelper.createBasicFranchise();
    }

    @Nested
    @DisplayName("Execute - Success Cases")
    class ExecuteSuccessTests {

        @Test
        @DisplayName("Given existing franchise with different new name, When executing use case, Then franchise name is updated successfully")
        void givenExistingFranchiseWithDifferentNewName_whenExecutingUseCase_thenFranchiseNameIsUpdatedSuccessfully() {
            // Arrange
            String newName = "Updated Franchise Name";
            UpdateFranchiseNameRequest request = UpdateFranchiseNameRequest.builder()
                    .newName(newName)
                    .build();

            Franchise updatedFranchise = FranchiseTestHelper.createFranchise(franchiseId, newName);
            FranchiseResponse expectedResponse = FranchiseResponse.builder()
                    .id(franchiseId)
                    .name(newName)
                    .build();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));
            when(franchiseRepository.save(any(Franchise.class)))
                    .thenReturn(Mono.just(updatedFranchise));
            when(mapper.toFranchiseResponse(updatedFranchise))
                    .thenReturn(expectedResponse);

            // Act
            Mono<FranchiseResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertNotNull(response);
                        assertEquals(newName, response.getName());
                        assertEquals(franchiseId, response.getId());
                    })
                    .verifyComplete();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, times(1)).save(any(Franchise.class));
            verify(mapper, times(1)).toFranchiseResponse(updatedFranchise);
        }

        @Test
        @DisplayName("Given new name with spaces, When executing use case, Then franchise name is updated with trimmed name")
        void givenNewNameWithSpaces_whenExecutingUseCase_thenFranchiseNameIsUpdatedWithTrimmedName() {
            // Arrange
            String newName = "  Trimmed Name  ";
            UpdateFranchiseNameRequest request = UpdateFranchiseNameRequest.builder()
                    .newName(newName)
                    .build();

            Franchise updatedFranchise = FranchiseTestHelper.createFranchise(franchiseId, "Trimmed Name");
            FranchiseResponse expectedResponse = FranchiseResponse.builder()
                    .id(franchiseId)
                    .name("Trimmed Name")
                    .build();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));
            when(franchiseRepository.save(any(Franchise.class)))
                    .thenReturn(Mono.just(updatedFranchise));
            when(mapper.toFranchiseResponse(updatedFranchise))
                    .thenReturn(expectedResponse);

            // Act
            Mono<FranchiseResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertNotNull(response);
                        assertEquals("Trimmed Name", response.getName());
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
            UpdateFranchiseNameRequest request = UpdateFranchiseNameRequest.builder()
                    .newName("New Name")
                    .build();

            when(franchiseRepository.findById(nonExistentId))
                    .thenReturn(Mono.empty());

            // Act
            Mono<FranchiseResponse> result = useCase.execute(nonExistentId, request);

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
        @DisplayName("Given same name as current, When executing use case, Then throws InvalidOperationException")
        void givenSameNameAsCurrent_whenExecutingUseCase_thenThrowsInvalidOperationException() {
            // Arrange
            UpdateFranchiseNameRequest request = UpdateFranchiseNameRequest.builder()
                    .newName(FranchiseTestHelper.DEFAULT_FRANCHISE_NAME)
                    .build();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));

            // Act
            Mono<FranchiseResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof InvalidOperationException &&
                                    throwable.getMessage().contains("El nuevo nombre debe ser diferente al nombre actual"))
                    .verify();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, never()).save(any());
            verifyNoInteractions(mapper);
        }

        @Test
        @DisplayName("Given empty new name, When executing use case, Then throws InvalidOperationException")
        void givenEmptyNewName_whenExecutingUseCase_thenThrowsInvalidOperationException() {
            // Arrange
            UpdateFranchiseNameRequest request = UpdateFranchiseNameRequest.builder()
                    .newName("")
                    .build();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));

            // Act
            Mono<FranchiseResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof InvalidOperationException)
                    .verify();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, never()).save(any());
        }
    }
}

