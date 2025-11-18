package com.nequi.franchises.franchises.application.helper;

import com.nequi.franchises.franchises.domain.model.Branch;
import com.nequi.franchises.franchises.domain.model.Franchise;
import com.nequi.franchises.franchises.domain.model.Product;

/**
 * Helper class para crear objetos de prueba de Franchise de forma reutilizable
 */
public class FranchiseTestHelper {

    public static final String DEFAULT_FRANCHISE_ID = "franchise-123";
    public static final String DEFAULT_FRANCHISE_NAME = "Test Franchise";
    public static final String DEFAULT_BRANCH_NAME = "Sucursal Centro";
    public static final String DEFAULT_PRODUCT_NAME = "Hamburguesa";
    public static final int DEFAULT_PRODUCT_STOCK = 100;

    public static Franchise createBasicFranchise() {
        Franchise franchise = new Franchise(DEFAULT_FRANCHISE_NAME);
        franchise.setId(DEFAULT_FRANCHISE_ID);
        return franchise;
    }


    public static Franchise createFranchise(String id, String name) {
        Franchise franchise = new Franchise(name);
        franchise.setId(id);
        return franchise;
    }

    public static Franchise createFranchiseWithBranch() {
        Franchise franchise = createBasicFranchise();
        Branch branch = BranchTestHelper.createBasicBranch();
        franchise.addBranch(branch);
        return franchise;
    }

    public static Franchise createFranchiseWithBranchAndProduct() {
        Franchise franchise = createFranchiseWithBranch();
        Product product = ProductTestHelper.createBasicProduct();
        franchise.addProductToBranch(DEFAULT_BRANCH_NAME, product);
        return franchise;
    }

    public static Franchise createFranchiseWithMultipleBranches(int branchCount) {
        Franchise franchise = createBasicFranchise();
        for (int i = 1; i <= branchCount; i++) {
            Branch branch = new Branch("Sucursal " + i);
            franchise.addBranch(branch);
        }
        return franchise;
    }

    public static Franchise createFranchiseWithBranchAndMultipleProducts(int productCount) {
        Franchise franchise = createFranchiseWithBranch();
        for (int i = 1; i <= productCount; i++) {
            Product product = new Product("Producto " + i, i * 10);
            franchise.addProductToBranch(DEFAULT_BRANCH_NAME, product);
        }
        return franchise;
    }
}

