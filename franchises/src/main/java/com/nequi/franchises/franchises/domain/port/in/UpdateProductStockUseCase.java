package com.nequi.franchises.franchises.domain.port.in;

public interface UpdateProductStockUseCase {
    void updateStock(String franchiseId, String branchId, String productName, int newStock);
}
