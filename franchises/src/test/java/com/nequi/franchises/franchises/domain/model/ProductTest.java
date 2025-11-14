package com.nequi.franchises.franchises.domain.model;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    // ------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------

    @Test
    void givenValidParameters_whenCreateProduct_thenReturnProduct() {
        // Arrange & Act
        Product product = new Product("Café", 10);

        // Assert
        assertEquals("Café", product.getName());
        assertEquals(10, product.getStock());
    }

    @Test
    void givenInvalidName_whenCreateProduct_thenThrowIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new Product("", 10));
        assertThrows(IllegalArgumentException.class, () -> new Product(null, 10));
    }

    @Test
    void givenNegativeStock_whenCreateProduct_thenThrowIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new Product("Café", -1));
    }


    // ------------------------------------------------------------
    // updateName
    // ------------------------------------------------------------

    @Test
    void givenValidName_whenUpdateName_thenNameUpdated() {
        // Arrange
        Product product = new Product("Pan", 5);

        // Act
        product.updateName("Harina");

        // Assert
        assertEquals("Harina", product.getName());
    }

    @Test
    void givenNullName_whenUpdateName_thenThrowIllegalArgument() {
        Product product = new Product("Pan", 5);

        assertThrows(IllegalArgumentException.class, () -> product.updateName(null));
    }

    @Test
    void givenEmptyName_whenUpdateName_thenThrowIllegalArgument() {
        Product product = new Product("Pan", 5);

        assertThrows(IllegalArgumentException.class, () -> product.updateName("   "));
    }


    // ------------------------------------------------------------
    // updateStock
    // ------------------------------------------------------------

    @Test
    void givenValidStock_whenUpdateStock_thenStockUpdated() {
        // Arrange
        Product product = new Product("Pan", 5);

        // Act
        product.updateStock(20);

        // Assert
        assertEquals(20, product.getStock());
    }

    @Test
    void givenNegativeStock_whenUpdateStock_thenThrowIllegalArgument() {
        Product product = new Product("Pan", 5);

        assertThrows(IllegalArgumentException.class, () -> product.updateStock(-10));
    }


    // ------------------------------------------------------------
    // increaseStock
    // ------------------------------------------------------------

    @Test
    void givenValidAmount_whenIncreaseStock_thenStockIncreases() {
        // Arrange
        Product product = new Product("Azúcar", 10);

        // Act
        product.increaseStock(5);

        // Assert
        assertEquals(15, product.getStock());
    }

    @Test
    void givenNegativeAmount_whenIncreaseStock_thenThrowIllegalArgument() {
        Product product = new Product("Azúcar", 10);

        assertThrows(IllegalArgumentException.class, () -> product.increaseStock(-3));
    }


    // ------------------------------------------------------------
    // reduceStock
    // ------------------------------------------------------------

    @Test
    void givenValidAmountLessThanStock_whenReduceStock_thenStockReduces() {
        // Arrange
        Product product = new Product("Leche", 10);

        // Act
        product.reduceStock(4);

        // Assert
        assertEquals(6, product.getStock());
    }

    @Test
    void givenNegativeAmount_whenReduceStock_thenThrowIllegalArgument() {
        Product product = new Product("Leche", 10);

        assertThrows(IllegalArgumentException.class, () -> product.reduceStock(-2));
    }

    @Test
    void givenAmountGreaterThanStock_whenReduceStock_thenThrowIllegalState() {
        Product product = new Product("Leche", 10);

        assertThrows(IllegalStateException.class, () -> product.reduceStock(15));
    }
}
