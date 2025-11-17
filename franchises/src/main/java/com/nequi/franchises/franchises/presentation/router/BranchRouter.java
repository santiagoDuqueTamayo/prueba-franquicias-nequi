package com.nequi.franchises.franchises.presentation.router;

import com.nequi.franchises.franchises.presentation.handler.BranchHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class BranchRouter {

    @Bean
    public RouterFunction<ServerResponse> branchRoutes(BranchHandler handler) {
        return route()
                // Agregar sucursal
                .POST("/api/v1/franchises/{franchiseId}/branches",
                        accept(MediaType.APPLICATION_JSON),
                        handler::addBranch)

                // Actualizar nombre de sucursal
                .PUT("/api/v1/franchises/{franchiseId}/branches/name",
                        accept(MediaType.APPLICATION_JSON),
                        handler::updateBranchName)

                .build();
    }
}

