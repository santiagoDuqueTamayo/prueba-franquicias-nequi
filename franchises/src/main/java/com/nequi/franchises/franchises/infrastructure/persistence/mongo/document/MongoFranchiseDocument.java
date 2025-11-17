package com.nequi.franchises.franchises.infrastructure.persistence.mongo.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "franchises")
public class MongoFranchiseDocument {

    @Id
    private String id;
    private String name;
    private List<MongoBranchDocument> branches;

    public MongoFranchiseDocument() {
    }

    public MongoFranchiseDocument(String id, String name, List<MongoBranchDocument> branches) {
        this.id = id;
        this.name = name;
        this.branches = branches;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MongoBranchDocument> getBranches() {
        return branches;
    }

    public void setBranches(List<MongoBranchDocument> branches) {
        this.branches = branches;
    }
}
