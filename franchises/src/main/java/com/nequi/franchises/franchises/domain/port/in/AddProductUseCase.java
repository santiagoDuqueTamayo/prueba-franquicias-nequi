package com.nequi.franchises.franchises.domain.port.in;

import com.nequi.franchises.franchises.domain.model.Product;

public interface AddProductUseCase {
    Product addProduct(String franchiseId, String branchId, String productName, int stock);
}
