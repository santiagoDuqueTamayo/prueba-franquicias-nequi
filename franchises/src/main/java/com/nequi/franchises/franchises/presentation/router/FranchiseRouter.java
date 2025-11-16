package com.nequi.franchises.franchises.presentation.router;

import com.nequi.franchises.franchises.presentation.handler.FranchiseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class FranchiseRouter {

    @Bean
    public RouterFunction<ServerResponse> productRoutes(FranchiseHandler handler) {
        return route()
                // Agregar producto
                .POST("/api/v1/franchises/{franchiseId}/products",
                        accept(MediaType.APPLICATION_JSON),
                        handler::addProduct)

                // Eliminar producto
                .DELETE("/api/v1/franchises/{franchiseId}/products",
                        accept(MediaType.APPLICATION_JSON),
                        handler::removeProduct)

                // Actualizar nombre de producto
                .PUT("/api/v1/franchises/{franchiseId}/products/name",
                        accept(MediaType.APPLICATION_JSON),
                        handler::updateProductName)

                // Actualizar stock de producto
                .PUT("/api/v1/franchises/{franchiseId}/products/stock",
                        accept(MediaType.APPLICATION_JSON),
                        handler::updateProductStock)

                .build();
    }
}