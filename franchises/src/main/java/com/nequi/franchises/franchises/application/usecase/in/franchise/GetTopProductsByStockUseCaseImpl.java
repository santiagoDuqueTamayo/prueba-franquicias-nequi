package com.nequi.franchises.franchises.application.usecase.in.franchise;

import com.nequi.franchises.franchises.application.dto.response.TopProductStockResponse;
import com.nequi.franchises.franchises.application.mapper.DomainMapper;
import com.nequi.franchises.franchises.domain.exception.NotFoundException;
import com.nequi.franchises.franchises.domain.model.Franchise;
import com.nequi.franchises.franchises.domain.port.out.FranchiseRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetTopProductsByStockUseCaseImpl implements GetTopProductsByStockUseCase {

    private final FranchiseRepositoryPort franchiseRepository;
    private final DomainMapper mapper;

    @Override
    public Mono<List<TopProductStockResponse>> execute(String franchiseId) {
        log.info("Executing GetTopProductsByStock: franchiseId={}", franchiseId);

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException(
                        "Franquicia no encontrada con ID: " + franchiseId)))
                .flatMap(this::getTopProductsFromFranchise)
                .doOnSuccess(response -> log.info("Top products retrieved successfully: {} products found",
                        response.size()))
                .doOnError(error -> log.error("Error retrieving top products: {}", error.getMessage()));
    }

    private Mono<List<TopProductStockResponse>> getTopProductsFromFranchise(Franchise franchise) {
        try {
            List<Franchise.ProductStockInfo> productStockInfoList = franchise.getProductsWithHighestStockPerBranch();
            
            List<TopProductStockResponse> responseList = productStockInfoList.stream()
                    .map(info -> TopProductStockResponse.builder()
                            .branchName(info.getBranchName())
                            .productName(info.getProduct().getName())
                            .stock(info.getProduct().getStock())
                            .build())
                    .collect(Collectors.toList());

            return Mono.just(responseList);
        } catch (IllegalStateException e) {
            return Mono.error(new IllegalStateException(e.getMessage()));
        }
    }
}

