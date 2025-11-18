package com.nequi.franchises.franchises.application.helper;

import com.nequi.franchises.franchises.domain.model.Product;

/**
 * Helper class para crear objetos de prueba de Product de forma reutilizable
 */
public class ProductTestHelper {

    public static final String DEFAULT_PRODUCT_NAME = "Hamburguesa";
    public static final int DEFAULT_PRODUCT_STOCK = 100;

    public static Product createBasicProduct() {
        return new Product(DEFAULT_PRODUCT_NAME, DEFAULT_PRODUCT_STOCK);
    }

    public static Product createProduct(String name, int stock) {
        return new Product(name, stock);
    }

    public static Product createProductWithName(String name) {
        return new Product(name, DEFAULT_PRODUCT_STOCK);
    }

    public static Product createProductWithStock(int stock) {
        return new Product(DEFAULT_PRODUCT_NAME, stock);
    }
}

