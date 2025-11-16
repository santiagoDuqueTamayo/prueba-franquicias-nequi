package com.nequi.franchises.franchises.domain.port.out;

import com.nequi.franchises.franchises.domain.model.Franchise;
import reactor.core.publisher.Mono;


public interface FranchiseRepositoryPort {

    Mono<Franchise> save(Franchise franchise);

    Mono<Franchise> findById(String id);

    Mono<Franchise> findByName(String name);

    Mono<Boolean> existsByName(String name);

    Mono<Void> deleteById(String id);
}
