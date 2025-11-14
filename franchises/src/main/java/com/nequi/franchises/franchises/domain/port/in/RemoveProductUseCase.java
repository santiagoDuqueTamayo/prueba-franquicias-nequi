package com.nequi.franchises.franchises.domain.port.in;

public interface RemoveProductUseCase {
    void removeProduct(String franchiseId, String branchId, String productName);
}
