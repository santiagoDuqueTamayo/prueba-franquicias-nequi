package com.nequi.franchises.franchises.domain.port.in;

import com.nequi.franchises.franchises.domain.model.Franchise;

public interface AddFranchiseUseCase {
    Franchise addFranchise(String franchiseName);
}
