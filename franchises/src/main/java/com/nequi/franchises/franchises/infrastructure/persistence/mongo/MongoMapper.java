package com.nequi.franchises.franchises.infrastructure.persistence.mongo.mapper;

import com.nequi.franchises.franchises.domain.model.Branch;
import com.nequi.franchises.franchises.domain.model.Franchise;
import com.nequi.franchises.franchises.domain.model.Product;
import com.nequi.franchises.franchises.infrastructure.persistence.mongo.document.*;

import java.util.List;
import java.util.stream.Collectors;

public class MongoMapper {

    // --------------------------------------------
    // Domain → Mongo Document
    // --------------------------------------------
    public MongoFranchiseDocument toDocument(Franchise franchise) {
        return new MongoFranchiseDocument(
                franchise.getId(),
                franchise.getName(),
                franchise.getBranches()
                        .stream()
                        .map(this::toDocument)
                        .collect(Collectors.toList())
        );
    }

    private MongoBranchDocument toDocument(Branch branch) {
        return new MongoBranchDocument(
                branch.getName(),
                branch.getProducts()
                        .stream()
                        .map(this::toDocument)
                        .collect(Collectors.toList())
        );
    }

    private MongoProductDocument toDocument(Product product) {
        return new MongoProductDocument(
                product.getName(),
                product.getStock()
        );
    }

    // --------------------------------------------
    // Mongo Document → Domain
    // --------------------------------------------
    public Franchise toDomain(MongoFranchiseDocument doc) {
        Franchise franchise = new Franchise(doc.getName());
        franchise.setId(doc.getId());

        doc.getBranches()
                .forEach(b -> franchise.addBranch(toDomain(b)));

        return franchise;
    }

    private Branch toDomain(MongoBranchDocument doc) {
        Branch branch = new Branch(doc.getName());

        doc.getProducts()
                .forEach(p -> branch.addProduct(toDomain(p)));

        return branch;
    }

    private Product toDomain(MongoProductDocument doc) {
        return new Product(doc.getName(), doc.getStock());
    }
}
