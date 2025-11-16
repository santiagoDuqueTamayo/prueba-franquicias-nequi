package com.nequi.franchises.franchises.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductStockRequest {
    private String branchName;
    private String productName;
    private Integer newStock;
}