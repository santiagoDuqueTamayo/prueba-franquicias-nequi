package com.nequi.franchises.franchises.domain.port.in;

public interface UpdateFranchiseNameUseCase {
    void updateFranchiseName(String franchiseId, String newName);
}
