package com.nequi.franchises.franchises.application.usecase.in.product;



import com.nequi.franchises.franchises.application.dto.request.AddProductRequest;
import com.nequi.franchises.franchises.application.dto.response.ProductResponse;
import com.nequi.franchises.franchises.application.mapper.DomainMapper;
import com.nequi.franchises.franchises.domain.exception.NotFoundException;
import com.nequi.franchises.franchises.domain.model.Franchise;
import com.nequi.franchises.franchises.domain.model.Product;
import com.nequi.franchises.franchises.domain.port.out.FranchiseRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddProductToBranchUseCaseImpl implements AddProductToBranchUseCase {

    private final FranchiseRepositoryPort franchiseRepository;
    private final DomainMapper mapper;

    @Override
    public Mono<ProductResponse> execute(String franchiseId, AddProductRequest request) {
        log.info("Executing AddProductToBranch: franchiseId={}, branchName={}, productName={}",
                franchiseId, request.getBranchName(), request.getProductName());

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException(
                        "Franquicia no encontrada con ID: " + franchiseId)))
                .flatMap(franchise -> addProductAndSave(franchise, request))
                .doOnSuccess(response -> log.info("Product added successfully: {}", response.getName()))
                .doOnError(error -> log.error("Error adding product: {}", error.getMessage()));
    }

    private Mono<ProductResponse> addProductAndSave(Franchise franchise, AddProductRequest request) {

        Product product = new Product(request.getProductName(), request.getStock());

        franchise.addProductToBranch(request.getBranchName(), product);

        return franchiseRepository.save(franchise)
                .map(savedFranchise -> mapper.toProductResponse(product));
    }
}

