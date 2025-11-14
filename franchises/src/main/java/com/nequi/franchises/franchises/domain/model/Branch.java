package com.nequi.franchises.franchises.domain.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Branch {

    private String name;
    private final List<Product> products;

    public Branch(String name) {
        setName(name);
        this.products = new ArrayList<>();
    }

    public void updateName(String newName) {
        setName(newName);
    }

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }

        boolean exists = products.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(product.getName()));

        if (exists) {
            throw new IllegalStateException("El producto ya existe en la sucursal.");
        }

        products.add(product);
    }

    public void removeProduct(String productName) {
        validateProductName(productName);

        boolean removed = products.removeIf(
                p -> p.getName().equalsIgnoreCase(productName)
        );

        if (!removed) {
            throw new IllegalStateException("El producto no existe en la sucursal.");
        }
    }

    public void updateProductStock(String productName, int newStock) {
        Product product = findProduct(productName);
        product.updateStock(newStock);
    }

    public void updateProductName(String oldName, String newName) {
        Product product = findProduct(oldName);
        product.updateName(newName);
    }

    public Product getProductWithHighestStock() {
        if (products.isEmpty()) {
            throw new IllegalStateException("La sucursal no tiene productos.");
        }

        return products.stream()
                .max(Comparator.comparingInt(Product::getStock))
                .orElseThrow();
    }

    public List<Product> getProducts() {
        return List.copyOf(products);
    }

    // -----------------------------------------------------
    // Métodos internos
    // -----------------------------------------------------

    private Product findProduct(String productName) {
        validateProductName(productName);

        return products.stream()
                .filter(p -> p.getName().equalsIgnoreCase(productName))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("El producto no existe en la sucursal."));
    }

    private void validateProductName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede ser vacío.");
        }
    }

    private void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la sucursal no puede ser vacío.");
        }
        this.name = name.trim();
    }

    // -----------------------------------------------------
    // Getters
    // -----------------------------------------------------
    public String getName() {
        return name;
    }
}
