package com.nequi.franchises.franchises.domain.port.out;

import com.nequi.franchises.franchises.domain.model.Franchise;

import java.util.Optional;

public interface FranchiseRepositoryPort {

    Franchise save(Franchise franchise);

    Optional<Franchise> findById(String franchiseId);

    boolean existsByName(String name);

    void delete(String franchiseId);
}
