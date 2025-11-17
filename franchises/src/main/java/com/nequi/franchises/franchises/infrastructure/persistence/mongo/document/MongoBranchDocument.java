package com.nequi.franchises.franchises.infrastructure.persistence.mongo.document;

import java.util.List;

public class MongoBranchDocument {

    private String name;
    private List<MongoProductDocument> products;

    public MongoBranchDocument() {
    }

    public MongoBranchDocument(String name, List<MongoProductDocument> products) {
        this.name = name;
        this.products = products;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MongoProductDocument> getProducts() {
        return products;
    }

    public void setProducts(List<MongoProductDocument> products) {
        this.products = products;
    }
}
