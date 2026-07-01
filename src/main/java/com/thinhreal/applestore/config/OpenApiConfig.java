package com.thinhreal.applestore.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI appleStoreOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Apple Store API")
                        .description("iStore REST API - Challenge 1 Blueprint")
                        .version("1.0.0"));
    }
}
