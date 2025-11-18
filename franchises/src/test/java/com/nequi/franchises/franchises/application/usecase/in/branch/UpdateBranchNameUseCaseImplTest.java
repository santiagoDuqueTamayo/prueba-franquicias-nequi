package com.nequi.franchises.franchises.application.usecase.in.branch;

import com.nequi.franchises.franchises.application.dto.request.UpdateBranchNameRequest;
import com.nequi.franchises.franchises.application.dto.response.BranchResponse;
import com.nequi.franchises.franchises.application.helper.BranchTestHelper;
import com.nequi.franchises.franchises.application.helper.FranchiseTestHelper;
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
@DisplayName("UpdateBranchNameUseCaseImpl - Pruebas Unitarias")
class UpdateBranchNameUseCaseImplTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepository;

    @Mock
    private DomainMapper mapper;

    @InjectMocks
    private UpdateBranchNameUseCaseImpl useCase;

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
        @DisplayName("Given existing franchise and branch with different new name, When executing use case, Then branch name is updated successfully")
        void givenExistingFranchiseAndBranchWithDifferentNewName_whenExecutingUseCase_thenBranchNameIsUpdatedSuccessfully() {
            // Arrange
            String oldName = FranchiseTestHelper.DEFAULT_BRANCH_NAME;
            String newName = "Sucursal Actualizada";
            UpdateBranchNameRequest request = UpdateBranchNameRequest.builder()
                    .oldName(oldName)
                    .newName(newName)
                    .build();

            Franchise savedFranchise = FranchiseTestHelper.createFranchiseWithBranch();
            BranchResponse expectedResponse = BranchResponse.builder()
                    .name(newName)
                    .build();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));
            when(franchiseRepository.save(any(Franchise.class)))
                    .thenReturn(Mono.just(savedFranchise));
            when(mapper.toBranchResponse(any()))
                    .thenReturn(expectedResponse);

            // Act
            Mono<BranchResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertNotNull(response);
                        assertEquals(newName, response.getName());
                    })
                    .verifyComplete();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, times(1)).save(any(Franchise.class));
            verify(mapper, times(1)).toBranchResponse(any());
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
            UpdateBranchNameRequest request = UpdateBranchNameRequest.builder()
                    .oldName("Old Name")
                    .newName("New Name")
                    .build();

            when(franchiseRepository.findById(nonExistentId))
                    .thenReturn(Mono.empty());

            // Act
            Mono<BranchResponse> result = useCase.execute(nonExistentId, request);

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
        @DisplayName("Given same old and new name, When executing use case, Then throws InvalidOperationException")
        void givenSameOldAndNewName_whenExecutingUseCase_thenThrowsInvalidOperationException() {
            // Arrange
            String branchName = FranchiseTestHelper.DEFAULT_BRANCH_NAME;
            UpdateBranchNameRequest request = UpdateBranchNameRequest.builder()
                    .oldName(branchName)
                    .newName(branchName)
                    .build();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));

            // Act
            Mono<BranchResponse> result = useCase.execute(franchiseId, request);

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
        @DisplayName("Given duplicate new branch name, When executing use case, Then throws DuplicateEntityException")
        void givenDuplicateNewBranchName_whenExecutingUseCase_thenThrowsDuplicateEntityException() {
            // Arrange
            String oldName = FranchiseTestHelper.DEFAULT_BRANCH_NAME;
            String newName = "Sucursal Duplicada";
            UpdateBranchNameRequest request = UpdateBranchNameRequest.builder()
                    .oldName(oldName)
                    .newName(newName)
                    .build();

            Franchise franchiseWithMultipleBranches = FranchiseTestHelper.createFranchiseWithMultipleBranches(2);
            franchiseWithMultipleBranches.addBranch(BranchTestHelper.createBranch(newName));

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchiseWithMultipleBranches));

            // Act
            Mono<BranchResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof DuplicateEntityException &&
                                    throwable.getMessage().contains("Ya existe una sucursal con el nombre"))
                    .verify();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, never()).save(any());
            verifyNoInteractions(mapper);
        }
    }
}

