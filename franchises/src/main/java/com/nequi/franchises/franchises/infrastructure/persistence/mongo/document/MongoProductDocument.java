package com.nequi.franchises.franchises.infrastructure.persistence.mongo.document;

public class MongoProductDocument {

    private String name;
    private int stock;

    public MongoProductDocument() {
    }

    public MongoProductDocument(String name, int stock) {
        this.name = name;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
