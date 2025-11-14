package com.nequi.franchises.franchises.domain.port.in;

public interface UpdateBranchNameUseCase {
    void updateBranchName(String franchiseId, String branchId, String newName);
}
