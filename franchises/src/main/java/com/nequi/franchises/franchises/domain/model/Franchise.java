package com.nequi.franchises.franchises.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Franchise {

    private String id;
    private String name;
    private final List<Branch> branches;

    public Franchise(String name) {
        setName(name);
        this.branches = new ArrayList<>();
    }

    // --------------------------------------------------------
    // Actualizar nombre de la franquicia
    // --------------------------------------------------------
    public void updateName(String newName) {
        setName(newName);
    }

    // --------------------------------------------------------
    // Agregar sucursal
    // --------------------------------------------------------
    public void addBranch(Branch branch) {
        if (branch == null) {
            throw new IllegalArgumentException("La sucursal no puede ser nula.");
        }

        boolean exists = branches.stream()
                .anyMatch(b -> b.getName().equalsIgnoreCase(branch.getName()));

        if (exists) {
            throw new IllegalStateException("Ya existe una sucursal con ese nombre.");
        }

        branches.add(branch);
    }

    // --------------------------------------------------------
    // Actualizar nombre de una sucursal
    // --------------------------------------------------------
    public void updateBranchName(String oldName, String newName) {
        Branch branch = findBranchByName(oldName);
        branch.updateName(newName);
    }

    // --------------------------------------------------------
    // Agregar producto a sucursal
    // --------------------------------------------------------
    public void addProductToBranch(String branchName, Product product) {
        Branch branch = findBranchByName(branchName);
        branch.addProduct(product);
    }

    // --------------------------------------------------------
    // Eliminar producto de sucursal
    // --------------------------------------------------------
    public void removeProductFromBranch(String branchName, String productName) {
        Branch branch = findBranchByName(branchName);
        branch.removeProduct(productName);
    }

    // --------------------------------------------------------
    // Modificar stock de producto en sucursal
    // --------------------------------------------------------
    public void updateProductStock(String branchName, String productName, int newStock) {
        Branch branch = findBranchByName(branchName);
        branch.updateProductStock(productName, newStock);
    }

    // --------------------------------------------------------
    // Actualizar nombre de un producto en una sucursal
    // --------------------------------------------------------
    public void updateProductName(String branchName, String oldName, String newName) {
        Branch branch = findBranchByName(branchName);
        branch.updateProductName(oldName, newName);
    }

    // --------------------------------------------------------
    // Obtener el producto con mayor stock **por sucursal**
    // --------------------------------------------------------
    public List<ProductStockInfo> getProductsWithHighestStockPerBranch() {

        if (branches.isEmpty()) {
            throw new IllegalStateException("La franquicia no tiene sucursales registradas.");
        }

        List<ProductStockInfo> result = new ArrayList<>();

        for (Branch branch : branches) {
            Product highest = branch.getProductWithHighestStock();
            result.add(new ProductStockInfo(branch.getName(), highest));
        }

        // Opcional: ordenar por stock descendente
        result.sort(Comparator.comparingInt((ProductStockInfo p) -> p.getProduct().getStock()).reversed());

        return Collections.unmodifiableList(result);
    }

    // --------------------------------------------------------
    // Buscar sucursal por nombre
    // --------------------------------------------------------
    private Branch findBranchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la sucursal no puede ser vacío.");
        }

        return branches.stream()
                .filter(b -> b.getName().equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("No existe una sucursal con el nombre: " + name));
    }

    // --------------------------------------------------------
    // Validación del nombre de franquicia
    // --------------------------------------------------------
    private void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la franquicia no puede ser vacío.");
        }
        this.name = name.trim();
    }

    // --------------------------------------------------------
    // Getters
    // --------------------------------------------------------

    public String getId() {
        return id;
    }

    public List<Branch> getAllBranches() { return branches; }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public List<Branch> getBranches() {
        return Collections.unmodifiableList(branches);
    }

    // --------------------------------------------------------
    // Inner DTO de dominio: Branch + Product
    // --------------------------------------------------------
    public static class ProductStockInfo {
        private final String branchName;
        private final Product product;

        public ProductStockInfo(String branchName, Product product) {
            this.branchName = branchName;
            this.product = product;
        }

        public String getBranchName() {
            return branchName;
        }

        public Product getProduct() {
            return product;
        }
    }
}
