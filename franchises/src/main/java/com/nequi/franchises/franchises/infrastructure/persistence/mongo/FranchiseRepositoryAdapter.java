package com.nequi.franchises.franchises.infrastructure.persistence.mongo;
import com.nequi.franchises.franchises.domain.model.Franchise;
import com.nequi.franchises.franchises.domain.port.out.FranchiseRepositoryPort;
import com.nequi.franchises.franchises.infrastructure.persistence.MongoFranchiseRepository;
import com.nequi.franchises.franchises.infrastructure.persistence.mongo.mapper.MongoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class FranchiseRepositoryAdapter implements FranchiseRepositoryPort {

    private final MongoFranchiseRepository mongoRepository;
    private final MongoMapper mongoMapper;

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        log.debug("Saving franchise: {}", franchise.getName());

        return Mono.just(franchise)
                .map(mongoMapper::toDocument)
                .flatMap(document -> {
                    if (document.getId() == null) {
                        log.debug("Creating new franchise document");
                        return mongoRepository.insert(document);
                    } else {
                        log.debug("Updating existing franchise document with ID: {}", document.getId());
                        return mongoRepository.save(document);
                    }
                })
                .map(mongoMapper::toDomain)
                .doOnSuccess(saved -> log.info("Franchise saved successfully with ID: {} and name: {}",
                        saved.getId(), saved.getName()))
                .doOnError(error -> log.error("Error saving franchise: {}", error.getMessage(), error));
    }

    @Override
    public Mono<Franchise> findById(String id) {
        log.debug("Finding franchise by ID: {}", id);

        return mongoRepository.findById(id)
                .map(mongoMapper::toDomain)
                .doOnSuccess(franchise -> {
                    if (franchise != null) {
                        log.info("Franchise found with ID: {} and name: {}", id, franchise.getName());
                    } else {
                        log.warn("Franchise not found with ID: {}", id);
                    }
                })
                .doOnError(error -> log.error("Error finding franchise by ID {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<Franchise> findByName(String name) {
        log.debug("Finding franchise by name: {}", name);

        return mongoRepository.findByNameIgnoreCase(name)
                .map(mongoMapper::toDomain)
                .doOnSuccess(franchise -> {
                    if (franchise != null) {
                        log.info("Franchise found by name: {}", name);
                    } else {
                        log.warn("Franchise not found with name: {}", name);
                    }
                })
                .doOnError(error -> log.error("Error finding franchise by name {}: {}", name, error.getMessage()));
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        log.debug("Checking if franchise exists with name: {}", name);

        return mongoRepository.existsByNameIgnoreCase(name)
                .doOnSuccess(exists -> log.debug("Franchise with name '{}' exists: {}", name, exists))
                .doOnError(error -> log.error("Error checking franchise existence: {}", error.getMessage()));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        log.debug("Deleting franchise with ID: {}", id);

        return mongoRepository.deleteById(id)
                .doOnSuccess(v -> log.info("Franchise deleted successfully with ID: {}", id))
                .doOnError(error -> log.error("Error deleting franchise with ID {}: {}", id, error.getMessage()));
    }
}