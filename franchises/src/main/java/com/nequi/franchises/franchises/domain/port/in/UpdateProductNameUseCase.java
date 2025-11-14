package com.nequi.franchises.franchises.domain.port.in;

public interface UpdateProductNameUseCase {
    void updateProductName(String franchiseId, String branchId, String oldName, String newName);
}
