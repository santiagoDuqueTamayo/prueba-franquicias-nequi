package com.nequi.franchises.franchises.application.helper;

import com.nequi.franchises.franchises.domain.model.Branch;
import com.nequi.franchises.franchises.domain.model.Product;

/**
 * Helper class para crear objetos de prueba de Branch de forma reutilizable
 */
public class BranchTestHelper {

    public static final String DEFAULT_BRANCH_NAME = "Sucursal Centro";

    public static Branch createBasicBranch() {
        return new Branch(DEFAULT_BRANCH_NAME);
    }

    public static Branch createBranch(String name) {
        return new Branch(name);
    }

    public static Branch createBranchWithProduct() {
        Branch branch = createBasicBranch();
        Product product = ProductTestHelper.createBasicProduct();
        branch.addProduct(product);
        return branch;
    }

    public static Branch createBranchWithMultipleProducts(int productCount) {
        Branch branch = createBasicBranch();
        for (int i = 1; i <= productCount; i++) {
            Product product = new Product("Producto " + i, i * 10);
            branch.addProduct(product);
        }
        return branch;
    }
}

