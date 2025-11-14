package com.nequi.franchises.franchises.domain.port.in;

import com.nequi.franchises.franchises.domain.model.Franchise;


import java.util.List;

public interface GetTopProductsByStockUseCase {
    List<Franchise.ProductStockInfo> getTopProducts(String franchiseId);
}
