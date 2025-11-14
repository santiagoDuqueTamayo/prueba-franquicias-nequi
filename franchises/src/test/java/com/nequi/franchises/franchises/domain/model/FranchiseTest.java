package com.nequi.franchises.franchises.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Franchise - Pruebas Unitarias")
class FranchiseTest {

    // ========================================================
    // CONSTRUCTOR
    // ========================================================

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("Given valid name, When creating franchise, Then franchise is created successfully")
        void givenValidName_whenCreatingFranchise_thenFranchiseIsCreatedSuccessfully() {
            // Arrange
            String franchiseName = "McDonald's";

            // Act
            Franchise franchise = new Franchise(franchiseName);

            // Assert
            assertNotNull(franchise);
            assertEquals("McDonald's", franchise.getName());
            assertNotNull(franchise.getBranches());
            assertTrue(franchise.getBranches().isEmpty());
        }

        @Test
        @DisplayName("Given null name, When creating franchise, Then throws IllegalArgumentException")
        void givenNullName_whenCreatingFranchise_thenThrowsIllegalArgumentException() {
            // Arrange
            String franchiseName = null;

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Franchise(franchiseName)
            );
            assertEquals("El nombre de la franquicia no puede ser vacío.", exception.getMessage());
        }

        @Test
        @DisplayName("Given empty name, When creating franchise, Then throws IllegalArgumentException")
        void givenEmptyName_whenCreatingFranchise_thenThrowsIllegalArgumentException() {
            // Arrange
            String franchiseName = "";

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Franchise(franchiseName)
            );
            assertEquals("El nombre de la franquicia no puede ser vacío.", exception.getMessage());
        }

        @Test
        @DisplayName("Given blank name, When creating franchise, Then throws IllegalArgumentException")
        void givenBlankName_whenCreatingFranchise_thenThrowsIllegalArgumentException() {
            // Arrange
            String franchiseName = "   ";

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Franchise(franchiseName)
            );
            assertEquals("El nombre de la franquicia no puede ser vacío.", exception.getMessage());
        }

        @Test
        @DisplayName("Given name with spaces, When creating franchise, Then name is trimmed")
        void givenNameWithSpaces_whenCreatingFranchise_thenNameIsTrimmed() {
            // Arrange
            String franchiseName = "  Burger King  ";

            // Act
            Franchise franchise = new Franchise(franchiseName);

            // Assert
            assertEquals("Burger King", franchise.getName());
        }
    }

    // ========================================================
    // UPDATE NAME
    // ========================================================

    @Nested
    @DisplayName("Update Franchise Name")
    class UpdateNameTests {

        private Franchise franchise;

        @BeforeEach
        void setUp() {
            franchise = new Franchise("Original Name");
        }

        @Test
        @DisplayName("Given valid new name, When updating franchise name, Then name is updated")
        void givenValidNewName_whenUpdatingFranchiseName_thenNameIsUpdated() {
            // Arrange
            String newName = "Updated Name";

            // Act
            franchise.updateName(newName);

            // Assert
            assertEquals("Updated Name", franchise.getName());
        }

        @Test
        @DisplayName("Given null name, When updating franchise name, Then throws IllegalArgumentException")
        void givenNullName_whenUpdatingFranchiseName_thenThrowsIllegalArgumentException() {
            // Arrange
            String newName = null;

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> franchise.updateName(newName)
            );
            assertEquals("El nombre de la franquicia no puede ser vacío.", exception.getMessage());
            assertEquals("Original Name", franchise.getName()); // Estado no cambió
        }

        @Test
        @DisplayName("Given empty name, When updating franchise name, Then throws IllegalArgumentException")
        void givenEmptyName_whenUpdatingFranchiseName_thenThrowsIllegalArgumentException() {
            // Arrange
            String newName = "";

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> franchise.updateName(newName)
            );
            assertEquals("El nombre de la franquicia no puede ser vacío.", exception.getMessage());
        }

        @Test
        @DisplayName("Given name with spaces, When updating franchise name, Then name is trimmed")
        void givenNameWithSpaces_whenUpdatingFranchiseName_thenNameIsTrimmed() {
            // Arrange
            String newName = "  Trimmed Name  ";

            // Act
            franchise.updateName(newName);

            // Assert
            assertEquals("Trimmed Name", franchise.getName());
        }
    }

    // ========================================================
    // ADD BRANCH
    // ========================================================

    @Nested
    @DisplayName("Add Branch")
    class AddBranchTests {

        private Franchise franchise;

        @BeforeEach
        void setUp() {
            franchise = new Franchise("Test Franchise");
        }

        @Test
        @DisplayName("Given valid branch, When adding branch, Then branch is added successfully")
        void givenValidBranch_whenAddingBranch_thenBranchIsAddedSuccessfully() {
            // Arrange
            Branch branch = new Branch("Branch 1");

            // Act
            franchise.addBranch(branch);

            // Assert
            assertEquals(1, franchise.getBranches().size());
            assertEquals("Branch 1", franchise.getBranches().get(0).getName());
        }

        @Test
        @DisplayName("Given null branch, When adding branch, Then throws IllegalArgumentException")
        void givenNullBranch_whenAddingBranch_thenThrowsIllegalArgumentException() {
            // Arrange
            Branch branch = null;

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> franchise.addBranch(branch)
            );
            assertEquals("La sucursal no puede ser nula.", exception.getMessage());
            assertTrue(franchise.getBranches().isEmpty());
        }

        @Test
        @DisplayName("Given duplicate branch name, When adding branch, Then throws IllegalStateException")
        void givenDuplicateBranchName_whenAddingBranch_thenThrowsIllegalStateException() {
            // Arrange
            Branch branch1 = new Branch("Branch 1");
            Branch branch2 = new Branch("Branch 1");
            franchise.addBranch(branch1);

            // Act & Assert
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> franchise.addBranch(branch2)
            );
            assertEquals("Ya existe una sucursal con ese nombre.", exception.getMessage());
            assertEquals(1, franchise.getBranches().size());
        }

        @Test
        @DisplayName("Given duplicate branch name with different case, When adding branch, Then throws IllegalStateException")
        void givenDuplicateBranchNameWithDifferentCase_whenAddingBranch_thenThrowsIllegalStateException() {
            // Arrange
            Branch branch1 = new Branch("Branch 1");
            Branch branch2 = new Branch("BRANCH 1");
            franchise.addBranch(branch1);

            // Act & Assert
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> franchise.addBranch(branch2)
            );
            assertEquals("Ya existe una sucursal con ese nombre.", exception.getMessage());
        }

        @Test
        @DisplayName("Given multiple unique branches, When adding branches, Then all branches are added")
        void givenMultipleUniqueBranches_whenAddingBranches_thenAllBranchesAreAdded() {
            // Arrange
            Branch branch1 = new Branch("Branch 1");
            Branch branch2 = new Branch("Branch 2");
            Branch branch3 = new Branch("Branch 3");

            // Act
            franchise.addBranch(branch1);
            franchise.addBranch(branch2);
            franchise.addBranch(branch3);

            // Assert
            assertEquals(3, franchise.getBranches().size());
        }
    }

    // ========================================================
    // UPDATE BRANCH NAME
    // ========================================================

    @Nested
    @DisplayName("Update Branch Name")
    class UpdateBranchNameTests {

        private Franchise franchise;
        private Branch branch;

        @BeforeEach
        void setUp() {
            franchise = new Franchise("Test Franchise");
            branch = new Branch("Original Branch");
            franchise.addBranch(branch);
        }

        @Test
        @DisplayName("Given valid names, When updating branch name, Then branch name is updated")
        void givenValidNames_whenUpdatingBranchName_thenBranchNameIsUpdated() {
            // Arrange
            String oldName = "Original Branch";
            String newName = "Updated Branch";

            // Act
            franchise.updateBranchName(oldName, newName);

            // Assert
            assertEquals("Updated Branch", franchise.getBranches().get(0).getName());
        }

        @Test
        @DisplayName("Given non-existent branch name, When updating branch name, Then throws IllegalStateException")
        void givenNonExistentBranchName_whenUpdatingBranchName_thenThrowsIllegalStateException() {
            // Arrange
            String oldName = "Non Existent";
            String newName = "New Name";

            // Act & Assert
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> franchise.updateBranchName(oldName, newName)
            );
            assertEquals("No existe una sucursal con el nombre: Non Existent", exception.getMessage());
        }

        @Test
        @DisplayName("Given null old name, When updating branch name, Then throws IllegalArgumentException")
        void givenNullOldName_whenUpdatingBranchName_thenThrowsIllegalArgumentException() {
            // Arrange
            String oldName = null;
            String newName = "New Name";

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> franchise.updateBranchName(oldName, newName)
            );
            assertEquals("El nombre de la sucursal no puede ser vacío.", exception.getMessage());
        }

        @Test
        @DisplayName("Given empty old name, When updating branch name, Then throws IllegalArgumentException")
        void givenEmptyOldName_whenUpdatingBranchName_thenThrowsIllegalArgumentException() {
            // Arrange
            String oldName = "";
            String newName = "New Name";

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> franchise.updateBranchName(oldName, newName)
            );
            assertEquals("El nombre de la sucursal no puede ser vacío.", exception.getMessage());
        }

        @Test
        @DisplayName("Given branch name with spaces, When updating branch name, Then name is trimmed and found")
        void givenBranchNameWithSpaces_whenUpdatingBranchName_thenNameIsTrimmedAndFound() {
            // Arrange
            String oldName = "  Original Branch  ";
            String newName = "Updated Branch";

            // Act
            franchise.updateBranchName(oldName, newName);

            // Assert
            assertEquals("Updated Branch", franchise.getBranches().get(0).getName());
        }
    }

    // ========================================================
    // ADD PRODUCT TO BRANCH
    // ========================================================

    @Nested
    @DisplayName("Add Product To Branch")
    class AddProductToBranchTests {

        private Franchise franchise;
        private Branch branch;

        @BeforeEach
        void setUp() {
            franchise = new Franchise("Test Franchise");
            branch = new Branch("Test Branch");
            franchise.addBranch(branch);
        }

        @Test
        @DisplayName("Given valid product and branch, When adding product, Then product is added successfully")
        void givenValidProductAndBranch_whenAddingProduct_thenProductIsAddedSuccessfully() {
            // Arrange
            Product product = new Product("Product 1", 100);

            // Act
            franchise.addProductToBranch("Test Branch", product);

            // Assert
            assertEquals(1, branch.getProducts().size());
            assertEquals("Product 1", branch.getProducts().get(0).getName());
        }

        @Test
        @DisplayName("Given non-existent branch, When adding product, Then throws IllegalStateException")
        void givenNonExistentBranch_whenAddingProduct_thenThrowsIllegalStateException() {
            // Arrange
            Product product = new Product("Product 1", 100);

            // Act & Assert
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> franchise.addProductToBranch("Non Existent", product)
            );
            assertEquals("No existe una sucursal con el nombre: Non Existent", exception.getMessage());
        }

        @Test
        @DisplayName("Given null branch name, When adding product, Then throws IllegalArgumentException")
        void givenNullBranchName_whenAddingProduct_thenThrowsIllegalArgumentException() {
            // Arrange
            Product product = new Product("Product 1", 100);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> franchise.addProductToBranch(null, product)
            );
            assertEquals("El nombre de la sucursal no puede ser vacío.", exception.getMessage());
        }

        @Test
        @DisplayName("Given empty branch name, When adding product, Then throws IllegalArgumentException")
        void givenEmptyBranchName_whenAddingProduct_thenThrowsIllegalArgumentException() {
            // Arrange
            Product product = new Product("Product 1", 100);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> franchise.addProductToBranch("", product)
            );
            assertEquals("El nombre de la sucursal no puede ser vacío.", exception.getMessage());
        }
    }

    // ========================================================
    // REMOVE PRODUCT FROM BRANCH
    // ========================================================

    @Nested
    @DisplayName("Remove Product From Branch")
    class RemoveProductFromBranchTests {

        private Franchise franchise;
        private Branch branch;

        @BeforeEach
        void setUp() {
            franchise = new Franchise("Test Franchise");
            branch = new Branch("Test Branch");
            franchise.addBranch(branch);
            Product product = new Product("Product 1", 100);
            branch.addProduct(product);
        }

        @Test
        @DisplayName("Given existing product, When removing product, Then product is removed successfully")
        void givenExistingProduct_whenRemovingProduct_thenProductIsRemovedSuccessfully() {
            // Arrange
            String branchName = "Test Branch";
            String productName = "Product 1";

            // Act
            franchise.removeProductFromBranch(branchName, productName);

            // Assert
            assertTrue(branch.getProducts().isEmpty());
        }

        @Test
        @DisplayName("Given non-existent branch, When removing product, Then throws IllegalStateException")
        void givenNonExistentBranch_whenRemovingProduct_thenThrowsIllegalStateException() {
            // Arrange
            String branchName = "Non Existent";
            String productName = "Product 1";

            // Act & Assert
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> franchise.removeProductFromBranch(branchName, productName)
            );
            assertEquals("No existe una sucursal con el nombre: Non Existent", exception.getMessage());
        }

        @Test
        @DisplayName("Given null branch name, When removing product, Then throws IllegalArgumentException")
        void givenNullBranchName_whenRemovingProduct_thenThrowsIllegalArgumentException() {
            // Arrange
            String productName = "Product 1";

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> franchise.removeProductFromBranch(null, productName)
            );
            assertEquals("El nombre de la sucursal no puede ser vacío.", exception.getMessage());
        }
    }

    // ========================================================
    // UPDATE PRODUCT STOCK
    // ========================================================

    @Nested
    @DisplayName("Update Product Stock")
    class UpdateProductStockTests {

        private Franchise franchise;
        private Branch branch;

        @BeforeEach
        void setUp() {
            franchise = new Franchise("Test Franchise");
            branch = new Branch("Test Branch");
            franchise.addBranch(branch);
            Product product = new Product("Product 1", 100);
            branch.addProduct(product);
        }

        @Test
        @DisplayName("Given valid data, When updating product stock, Then stock is updated successfully")
        void givenValidData_whenUpdatingProductStock_thenStockIsUpdatedSuccessfully() {
            // Arrange
            String branchName = "Test Branch";
            String productName = "Product 1";
            int newStock = 200;

            // Act
            franchise.updateProductStock(branchName, productName, newStock);

            // Assert
            assertEquals(200, branch.getProducts().get(0).getStock());
        }

        @Test
        @DisplayName("Given non-existent branch, When updating product stock, Then throws IllegalStateException")
        void givenNonExistentBranch_whenUpdatingProductStock_thenThrowsIllegalStateException() {
            // Arrange
            String branchName = "Non Existent";
            String productName = "Product 1";
            int newStock = 200;

            // Act & Assert
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> franchise.updateProductStock(branchName, productName, newStock)
            );
            assertEquals("No existe una sucursal con el nombre: Non Existent", exception.getMessage());
        }

        @Test
        @DisplayName("Given null branch name, When updating product stock, Then throws IllegalArgumentException")
        void givenNullBranchName_whenUpdatingProductStock_thenThrowsIllegalArgumentException() {
            // Arrange
            String productName = "Product 1";
            int newStock = 200;

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> franchise.updateProductStock(null, productName, newStock)
            );
            assertEquals("El nombre de la sucursal no puede ser vacío.", exception.getMessage());
        }
    }

    // ========================================================
    // UPDATE PRODUCT NAME
    // ========================================================

    @Nested
    @DisplayName("Update Product Name")
    class UpdateProductNameTests {

        private Franchise franchise;
        private Branch branch;

        @BeforeEach
        void setUp() {
            franchise = new Franchise("Test Franchise");
            branch = new Branch("Test Branch");
            franchise.addBranch(branch);
            Product product = new Product("Original Product", 100);
            branch.addProduct(product);
        }

        @Test
        @DisplayName("Given valid data, When updating product name, Then product name is updated successfully")
        void givenValidData_whenUpdatingProductName_thenProductNameIsUpdatedSuccessfully() {
            // Arrange
            String branchName = "Test Branch";
            String oldName = "Original Product";
            String newName = "Updated Product";

            // Act
            franchise.updateProductName(branchName, oldName, newName);

            // Assert
            assertEquals("Updated Product", branch.getProducts().get(0).getName());
        }

        @Test
        @DisplayName("Given non-existent branch, When updating product name, Then throws IllegalStateException")
        void givenNonExistentBranch_whenUpdatingProductName_thenThrowsIllegalStateException() {
            // Arrange
            String branchName = "Non Existent";
            String oldName = "Original Product";
            String newName = "Updated Product";

            // Act & Assert
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> franchise.updateProductName(branchName, oldName, newName)
            );
            assertEquals("No existe una sucursal con el nombre: Non Existent", exception.getMessage());
        }

        @Test
        @DisplayName("Given null branch name, When updating product name, Then throws IllegalArgumentException")
        void givenNullBranchName_whenUpdatingProductName_thenThrowsIllegalArgumentException() {
            // Arrange
            String oldName = "Original Product";
            String newName = "Updated Product";

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> franchise.updateProductName(null, oldName, newName)
            );
            assertEquals("El nombre de la sucursal no puede ser vacío.", exception.getMessage());
        }
    }

    // ========================================================
    // GET PRODUCTS WITH HIGHEST STOCK PER BRANCH
    // ========================================================

    @Nested
    @DisplayName("Get Products With Highest Stock Per Branch")
    class GetProductsWithHighestStockPerBranchTests {

        private Franchise franchise;

        @BeforeEach
        void setUp() {
            franchise = new Franchise("Test Franchise");
        }

        @Test
        @DisplayName("Given franchise without branches, When getting highest stock products, Then throws IllegalStateException")
        void givenFranchiseWithoutBranches_whenGettingHighestStockProducts_thenThrowsIllegalStateException() {
            // Act & Assert
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> franchise.getProductsWithHighestStockPerBranch()
            );
            assertEquals("La franquicia no tiene sucursales registradas.", exception.getMessage());
        }

        @Test
        @DisplayName("Given single branch with products, When getting highest stock products, Then returns correct product")
        void givenSingleBranchWithProducts_whenGettingHighestStockProducts_thenReturnsCorrectProduct() {
            // Arrange
            Branch branch = new Branch("Branch 1");
            branch.addProduct(new Product("Product A", 50));
            branch.addProduct(new Product("Product B", 150));
            branch.addProduct(new Product("Product C", 100));
            franchise.addBranch(branch);

            // Act
            List<Franchise.ProductStockInfo> result = franchise.getProductsWithHighestStockPerBranch();

            // Assert
            assertEquals(1, result.size());
            assertEquals("Branch 1", result.get(0).getBranchName());
            assertEquals("Product B", result.get(0).getProduct().getName());
            assertEquals(150, result.get(0).getProduct().getStock());
        }

        @Test
        @DisplayName("Given multiple branches with products, When getting highest stock products, Then returns all sorted by stock")
        void givenMultipleBranchesWithProducts_whenGettingHighestStockProducts_thenReturnsAllSortedByStock() {
            // Arrange
            Branch branch1 = new Branch("Branch 1");
            branch1.addProduct(new Product("Product A", 50));
            branch1.addProduct(new Product("Product B", 300));

            Branch branch2 = new Branch("Branch 2");
            branch2.addProduct(new Product("Product C", 100));
            branch2.addProduct(new Product("Product D", 500));

            Branch branch3 = new Branch("Branch 3");
            branch3.addProduct(new Product("Product E", 200));

            franchise.addBranch(branch1);
            franchise.addBranch(branch2);
            franchise.addBranch(branch3);

            // Act
            List<Franchise.ProductStockInfo> result = franchise.getProductsWithHighestStockPerBranch();

            // Assert
            assertEquals(3, result.size());

            // Verificar ordenamiento descendente por stock
            assertEquals("Product D", result.get(0).getProduct().getName());
            assertEquals(500, result.get(0).getProduct().getStock());

            assertEquals("Product B", result.get(1).getProduct().getName());
            assertEquals(300, result.get(1).getProduct().getStock());

            assertEquals("Product E", result.get(2).getProduct().getName());
            assertEquals(200, result.get(2).getProduct().getStock());
        }

        @Test
        @DisplayName("Given result list, When trying to modify it, Then throws UnsupportedOperationException")
        void givenResultList_whenTryingToModifyIt_thenThrowsUnsupportedOperationException() {
            // Arrange
            Branch branch = new Branch("Branch 1");
            branch.addProduct(new Product("Product A", 100));
            franchise.addBranch(branch);

            // Act
            List<Franchise.ProductStockInfo> result = franchise.getProductsWithHighestStockPerBranch();

            // Assert
            assertThrows(UnsupportedOperationException.class, () ->
                    result.add(new Franchise.ProductStockInfo("Test", new Product("Test", 1)))
            );
        }
    }

    // ========================================================
    // GET BRANCHES (Inmutabilidad)
    // ========================================================

    @Nested
    @DisplayName("Get Branches - Immutability")
    class GetBranchesImmutabilityTests {

        private Franchise franchise;

        @BeforeEach
        void setUp() {
            franchise = new Franchise("Test Franchise");
            franchise.addBranch(new Branch("Branch 1"));
        }

        @Test
        @DisplayName("Given branches list, When trying to modify it, Then throws UnsupportedOperationException")
        void givenBranchesList_whenTryingToModifyIt_thenThrowsUnsupportedOperationException() {
            // Act
            List<Branch> branches = franchise.getBranches();

            // Assert
            assertThrows(UnsupportedOperationException.class, () ->
                    branches.add(new Branch("Branch 2"))
            );
        }

        @Test
        @DisplayName("Given branches list, When trying to remove from it, Then throws UnsupportedOperationException")
        void givenBranchesList_whenTryingToRemoveFromIt_thenThrowsUnsupportedOperationException() {
            // Act
            List<Branch> branches = franchise.getBranches();

            // Assert
            assertThrows(UnsupportedOperationException.class, () ->
                    branches.remove(0)
            );
        }
    }
}