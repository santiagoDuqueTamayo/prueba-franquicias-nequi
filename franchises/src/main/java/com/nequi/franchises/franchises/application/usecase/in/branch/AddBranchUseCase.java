package com.nequi.franchises.franchises.application.usecase.in.branch;

import com.nequi.franchises.franchises.domain.model.Branch;

public interface AddBranchUseCase {
    Branch addBranch(String franchiseId, String branchName);
}
