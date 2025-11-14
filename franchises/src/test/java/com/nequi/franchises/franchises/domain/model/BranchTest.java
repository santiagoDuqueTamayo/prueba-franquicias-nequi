package com.nequi.franchises.franchises.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BranchTest {

    // ------------------------------------------------------------
    // Constructor & updateName
    // ------------------------------------------------------------

    @Test
    void givenValidName_whenCreateBranch_thenBranchCreated() {
        Branch branch = new Branch("Sucursal A");

        assertEquals("Sucursal A", branch.getName());
        assertTrue(branch.getProducts().isEmpty());
    }

    @Test
    void givenNullName_whenCreateBranch_thenThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Branch(null));
    }

    @Test
    void givenEmptyName_whenCreateBranch_thenThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Branch("   "));
    }

    @Test
    void givenValidNewName_whenUpdateName_thenNameUpdated() {
        Branch branch = new Branch("Old Name");

        branch.updateName("New Name");

        assertEquals("New Name", branch.getName());
    }

    @Test
    void givenNullNewName_whenUpdateName_thenThrowException() {
        Branch branch = new Branch("Sucursal");

        assertThrows(IllegalArgumentException.class,
                () -> branch.updateName(null));
    }

    @Test
    void givenEmptyNewName_whenUpdateName_thenThrowException() {
        Branch branch = new Branch("Sucursal");

        assertThrows(IllegalArgumentException.class,
                () -> branch.updateName("   "));
    }

    // ------------------------------------------------------------
    // addProduct
    // ------------------------------------------------------------

    @Test
    void givenValidProduct_whenAddProduct_thenProductAdded() {
        Branch branch = new Branch("Sucursal");
        Product product = new Product("Arroz", 10);

        branch.addProduct(product);

        assertEquals(1, branch.getProducts().size());
        assertEquals("Arroz", branch.getProducts().get(0).getName());
    }

    @Test
    void givenNullProduct_whenAddProduct_thenThrowException() {
        Branch branch = new Branch("Sucursal");

        assertThrows(IllegalArgumentException.class,
                () -> branch.addProduct(null));
    }

    @Test
    void givenDuplicateProduct_whenAddProduct_thenThrowException() {
        Branch branch = new Branch("Sucursal");
        Product product = new Product("Arroz", 10);

        branch.addProduct(product);

        assertThrows(IllegalStateException.class,
                () -> branch.addProduct(new Product("Arroz", 5))); // mismo nombre
    }

    // ------------------------------------------------------------
    // removeProduct
    // ------------------------------------------------------------

    @Test
    void givenExistingProduct_whenRemoveProduct_thenProductRemoved() {
        Branch branch = new Branch("Sucursal");
        branch.addProduct(new Product("Arroz", 10));

        branch.removeProduct("Arroz");

        assertTrue(branch.getProducts().isEmpty());
    }

    @Test
    void givenNonExistingProduct_whenRemoveProduct_thenThrowException() {
        Branch branch = new Branch("Sucursal");

        assertThrows(IllegalStateException.class,
                () -> branch.removeProduct("NoExiste"));
    }

    @Test
    void givenNullProductName_whenRemoveProduct_thenThrowException() {
        Branch branch = new Branch("Sucursal");

        assertThrows(IllegalArgumentException.class,
                () -> branch.removeProduct(null));
    }

    @Test
    void givenEmptyProductName_whenRemoveProduct_thenThrowException() {
        Branch branch = new Branch("Sucursal");

        assertThrows(IllegalArgumentException.class,
                () -> branch.removeProduct("   "));
    }

    // ------------------------------------------------------------
    // updateProductStock
    // ------------------------------------------------------------

    @Test
    void givenExistingProduct_whenUpdateProductStock_thenStockUpdated() {
        Branch branch = new Branch("Sucursal");
        branch.addProduct(new Product("Arroz", 10));

        branch.updateProductStock("Arroz", 30);

        assertEquals(30, branch.getProducts().get(0).getStock());
    }

    @Test
    void givenNonExistingProduct_whenUpdateProductStock_thenThrowException() {
        Branch branch = new Branch("Sucursal");

        assertThrows(IllegalStateException.class,
                () -> branch.updateProductStock("NoExiste", 20));
    }

    @Test
    void givenInvalidStock_whenUpdateProductStock_thenThrowException() {
        Branch branch = new Branch("Sucursal");
        branch.addProduct(new Product("Arroz", 10));

        assertThrows(IllegalArgumentException.class,
                () -> branch.updateProductStock("Arroz", -1));
    }

    // ------------------------------------------------------------
    // updateProductName
    // ------------------------------------------------------------

    @Test
    void givenExistingProduct_whenUpdateProductName_thenNameUpdated() {
        Branch branch = new Branch("Sucursal");
        branch.addProduct(new Product("Arroz", 10));

        branch.updateProductName("Arroz", "Nuevo Arroz");

        assertEquals("Nuevo Arroz", branch.getProducts().get(0).getName());
    }

    @Test
    void givenNonExistingProduct_whenUpdateProductName_thenThrowException() {
        Branch branch = new Branch("Sucursal");

        assertThrows(IllegalStateException.class,
                () -> branch.updateProductName("NoExiste", "Nuevo"));
    }

    @Test
    void givenInvalidNewName_whenUpdateProductName_thenThrowException() {
        Branch branch = new Branch("Sucursal");
        branch.addProduct(new Product("Arroz", 10));

        assertThrows(IllegalArgumentException.class,
                () -> branch.updateProductName("Arroz", "  "));
    }

    // ------------------------------------------------------------
    // getProductWithHighestStock
    // ------------------------------------------------------------

    @Test
    void givenProducts_whenGetProductWithHighestStock_thenReturnCorrectProduct() {
        Branch branch = new Branch("Sucursal");
        branch.addProduct(new Product("Arroz", 10));
        branch.addProduct(new Product("Frijol", 20));
        branch.addProduct(new Product("Lenteja", 5));

        Product result = branch.getProductWithHighestStock();

        assertEquals("Frijol", result.getName());
        assertEquals(20, result.getStock());
    }

    @Test
    void givenNoProducts_whenGetProductWithHighestStock_thenThrowException() {
        Branch branch = new Branch("Sucursal");

        assertThrows(IllegalStateException.class,
                branch::getProductWithHighestStock);
    }

    // ------------------------------------------------------------
    // getProducts (inmutabilidad)
    // ------------------------------------------------------------

    @Test
    void givenProductList_whenGetProducts_thenReturnUnmodifiableList() {
        Branch branch = new Branch("Sucursal");
        branch.addProduct(new Product("Arroz", 10));

        List<Product> list = branch.getProducts();

        assertThrows(UnsupportedOperationException.class,
                () -> list.add(new Product("Frijol", 5)));
    }
}
