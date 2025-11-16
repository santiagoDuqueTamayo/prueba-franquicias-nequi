package com.nequi.franchises.franchises.application.usecase.in.branch;

public interface UpdateBranchNameUseCase {
    void updateBranchName(String franchiseId, String branchId, String newName);
}
