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
                        .description("API Document for the Applestore project based on the ERD.")
                        .version("1.0.0"));
    }
}
