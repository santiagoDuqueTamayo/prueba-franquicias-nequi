package com.nequi.franchises.franchises.application.usecase.in.franchise;

import com.nequi.franchises.franchises.domain.model.Franchise;


import java.util.List;

public interface GetTopProductsByStockUseCase {
    List<Franchise.ProductStockInfo> getTopProducts(String franchiseId);
}
