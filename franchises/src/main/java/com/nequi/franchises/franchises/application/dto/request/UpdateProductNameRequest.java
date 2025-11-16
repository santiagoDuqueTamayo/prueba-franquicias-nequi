package com.nequi.franchises.franchises.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductNameRequest {

    private String branchName;
    private String productName;
    private String newName;
}
