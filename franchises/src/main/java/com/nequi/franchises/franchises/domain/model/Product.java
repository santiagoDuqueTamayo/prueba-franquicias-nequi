package com.nequi.franchises.franchises.domain.model;


public class Product {

    private String name;
    private int stock;

    public Product(String name, int stock) {
        setName(name);
        setStock(stock);
    }

    public void updateName(String nuevoNombre) {
        setName(nuevoNombre);
    }

    public void updateStock(int nuevoStock) {
        setStock(nuevoStock);
    }

    public void increaseStock(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("La cantidad a incrementar debe ser positiva.");
        }
        this.stock += amount;
    }

    public void reduceStock(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad a reducir debe ser positiva.");
        }
        if (this.stock - cantidad < 0) {
            throw new IllegalStateException("No es posible dejar el stock en negativo.");
        }
        this.stock -= cantidad;
    }


    private void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede ser vacío.");
        }
        this.name = name.trim();
    }

    private void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public int getStock() {
        return stock;
    }

}
