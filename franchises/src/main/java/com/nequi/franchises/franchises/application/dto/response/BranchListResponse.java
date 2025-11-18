    package com.nequi.franchises.franchises.application.dto.response;

    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.util.List;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class BranchListResponse {
        private List<ProductResponse> products;
    }
