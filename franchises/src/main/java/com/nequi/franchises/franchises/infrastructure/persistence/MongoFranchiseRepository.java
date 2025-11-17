package com.nequi.franchises.franchises.infrastructure.persistence;

import com.nequi.franchises.franchises.infrastructure.persistence.mongo.document.MongoFranchiseDocument;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface MongoFranchiseRepository extends ReactiveMongoRepository<MongoFranchiseDocument, String> {

    /**
     * Busca una franquicia por nombre (case insensitive)
     */
    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    Mono<MongoFranchiseDocument> findByNameIgnoreCase(String name);

    /**
     * Verifica si existe una franquicia con ese nombre
     */
    Mono<Boolean> existsByNameIgnoreCase(String name);

    /**
     * Elimina una franquicia por nombre
     */
    Mono<Void> deleteByNameIgnoreCase(String name);
}