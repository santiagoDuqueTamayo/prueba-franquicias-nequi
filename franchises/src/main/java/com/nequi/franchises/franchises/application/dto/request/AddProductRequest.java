package com.nequi.franchises.franchises.application.dto.request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProductRequest {

    private String branchName;
    private String productName;
    private int stock;
}
