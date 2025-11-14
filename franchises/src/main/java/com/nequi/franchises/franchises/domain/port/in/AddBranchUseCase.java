package com.nequi.franchises.franchises.domain.port.in;

import com.nequi.franchises.franchises.domain.model.Branch;

public interface AddBranchUseCase {
    Branch addBranch(String franchiseId, String branchName);
}
