package com.nequi.franchises.franchises.application.mapper;

import com.nequi.franchises.franchises.application.dto.response.BranchResponse;
import com.nequi.franchises.franchises.application.dto.response.FranchiseResponse;
import com.nequi.franchises.franchises.application.dto.response.ProductResponse;
import com.nequi.franchises.franchises.domain.model.Branch;
import com.nequi.franchises.franchises.domain.model.Franchise;
import com.nequi.franchises.franchises.domain.model.Product;
import org.springframework.stereotype.Component;

@Component
public class DomainMapper {

    public ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .name(product.getName())
                .stock(product.getStock())
                .build();
    }

    public BranchResponse toBranchResponse(Branch branch) {
        return BranchResponse.builder()
                .name(branch.getName())
                .build();
    }

    public FranchiseResponse toFranchiseResponse(Franchise franchise) {
        return FranchiseResponse.builder()
                .id(franchise.getId())
                .name(franchise.getName())
                .build();
    }
}