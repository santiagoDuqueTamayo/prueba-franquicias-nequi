package com.nequi.franchises.franchises.application.mapper;

import com.nequi.franchises.franchises.application.dto.response.BranchListResponse;
import com.nequi.franchises.franchises.application.dto.response.BranchResponse;
import com.nequi.franchises.franchises.application.dto.response.FranchiseResponse;
import com.nequi.franchises.franchises.application.dto.response.FranchisesListResponse;
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

    public FranchisesListResponse mapToResponse(Franchise franchise) {
        return FranchisesListResponse.builder()
                .nameFranchises(franchise.getName())
                .branches(
                        franchise.getBranches().stream()
                                .map(branch -> BranchListResponse.builder()
                                        .products(
                                                branch.getProducts().stream()
                                                        .map(prod -> ProductResponse.builder()
                                                                .name(prod.getName())
                                                                .stock(prod.getStock())
                                                                .build())
                                                        .toList()
                                        )
                                        .build()
                                )
                                .toList()
                )
                .build();
    }
}