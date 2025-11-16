package com.nequi.franchises.franchises.application.usecase.in.franchise;

import com.nequi.franchises.franchises.domain.model.Franchise;

public interface AddFranchiseUseCase {
    Franchise addFranchise(String franchiseName);
}
