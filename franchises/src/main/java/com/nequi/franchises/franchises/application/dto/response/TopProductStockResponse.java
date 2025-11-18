package com.nequi.franchises.franchises.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopProductStockResponse {
    private String branchName;
    private String productName;
    private int stock;
}

