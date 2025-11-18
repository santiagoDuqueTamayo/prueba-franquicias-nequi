package com.nequi.franchises.franchises.infrastructure.persistence;

import com.nequi.franchises.franchises.infrastructure.persistence.mongo.document.MongoFranchiseDocument;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface MongoFranchiseRepository extends ReactiveMongoRepository<MongoFranchiseDocument, String> {

    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    Mono<MongoFranchiseDocument> findByNameIgnoreCase(String name);


    Mono<Boolean> existsByNameIgnoreCase(String name);

    Mono<Void> deleteByNameIgnoreCase(String name);
}