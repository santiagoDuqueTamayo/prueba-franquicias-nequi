package com.nequi.franchises.franchises.application.mapper;

import com.nequi.franchises.franchises.application.dto.response.ProductResponse;
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
}