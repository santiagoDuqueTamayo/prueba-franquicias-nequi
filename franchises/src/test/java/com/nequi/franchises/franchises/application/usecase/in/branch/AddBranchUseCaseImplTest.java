package com.nequi.franchises.franchises.application.usecase.in.branch;

import com.nequi.franchises.franchises.application.dto.request.AddBranchRequest;
import com.nequi.franchises.franchises.application.dto.response.BranchResponse;
import com.nequi.franchises.franchises.application.helper.BranchTestHelper;
import com.nequi.franchises.franchises.application.helper.FranchiseTestHelper;
import com.nequi.franchises.franchises.application.mapper.DomainMapper;
import com.nequi.franchises.franchises.domain.exception.DuplicateEntityException;
import com.nequi.franchises.franchises.domain.exception.NotFoundException;
import com.nequi.franchises.franchises.domain.model.Branch;
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
@DisplayName("AddBranchUseCaseImpl - Pruebas Unitarias")
class AddBranchUseCaseImplTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepository;

    @Mock
    private DomainMapper mapper;

    @InjectMocks
    private AddBranchUseCaseImpl useCase;

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
        @DisplayName("Given existing franchise with unique branch name, When executing use case, Then branch is added successfully")
        void givenExistingFranchiseWithUniqueBranchName_whenExecutingUseCase_thenBranchIsAddedSuccessfully() {
            // Arrange
            String branchName = "Sucursal Norte";
            AddBranchRequest request = AddBranchRequest.builder()
                    .branchName(branchName)
                    .build();

            Franchise savedFranchise = FranchiseTestHelper.createFranchiseWithBranch();
            BranchResponse expectedResponse = BranchResponse.builder()
                    .name(branchName)
                    .build();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));
            when(franchiseRepository.save(any(Franchise.class)))
                    .thenReturn(Mono.just(savedFranchise));
            when(mapper.toBranchResponse(any(Branch.class)))
                    .thenReturn(expectedResponse);

            // Act
            Mono<BranchResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertNotNull(response);
                        assertEquals(branchName, response.getName());
                    })
                    .verifyComplete();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, times(1)).save(any(Franchise.class));
            verify(mapper, times(1)).toBranchResponse(any(Branch.class));
        }

        @Test
        @DisplayName("Given branch name with spaces, When executing use case, Then branch is added with trimmed name")
        void givenBranchNameWithSpaces_whenExecutingUseCase_thenBranchIsAddedWithTrimmedName() {
            // Arrange
            String branchName = "  Sucursal Sur  ";
            AddBranchRequest request = AddBranchRequest.builder()
                    .branchName(branchName)
                    .build();

            Franchise savedFranchise = FranchiseTestHelper.createFranchiseWithBranch();
            BranchResponse expectedResponse = BranchResponse.builder()
                    .name("Sucursal Sur")
                    .build();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));
            when(franchiseRepository.save(any(Franchise.class)))
                    .thenReturn(Mono.just(savedFranchise));
            when(mapper.toBranchResponse(any(Branch.class)))
                    .thenReturn(expectedResponse);

            // Act
            Mono<BranchResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertNotNull(response);
                        assertEquals("Sucursal Sur", response.getName());
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
            AddBranchRequest request = AddBranchRequest.builder()
                    .branchName("Sucursal")
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
        @DisplayName("Given duplicate branch name, When executing use case, Then throws DuplicateEntityException")
        void givenDuplicateBranchName_whenExecutingUseCase_thenThrowsDuplicateEntityException() {
            // Arrange
            String branchName = "Sucursal Existente";
            AddBranchRequest request = AddBranchRequest.builder()
                    .branchName(branchName)
                    .build();

            Franchise franchiseWithBranch = FranchiseTestHelper.createFranchiseWithBranch();
            Branch existingBranch = BranchTestHelper.createBranch(branchName);
            franchiseWithBranch.addBranch(existingBranch);

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchiseWithBranch));

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

        @Test
        @DisplayName("Given null branch name, When executing use case, Then throws IllegalArgumentException")
        void givenNullBranchName_whenExecutingUseCase_thenThrowsIllegalArgumentException() {
            // Arrange
            AddBranchRequest request = AddBranchRequest.builder()
                    .branchName(null)
                    .build();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));

            // Act
            Mono<BranchResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof IllegalArgumentException)
                    .verify();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, never()).save(any());
        }

        @Test
        @DisplayName("Given empty branch name, When executing use case, Then throws IllegalArgumentException")
        void givenEmptyBranchName_whenExecutingUseCase_thenThrowsIllegalArgumentException() {
            // Arrange
            AddBranchRequest request = AddBranchRequest.builder()
                    .branchName("")
                    .build();

            when(franchiseRepository.findById(franchiseId))
                    .thenReturn(Mono.just(franchise));

            // Act
            Mono<BranchResponse> result = useCase.execute(franchiseId, request);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable ->
                            throwable instanceof IllegalArgumentException)
                    .verify();

            verify(franchiseRepository, times(1)).findById(franchiseId);
            verify(franchiseRepository, never()).save(any());
        }
    }
}

