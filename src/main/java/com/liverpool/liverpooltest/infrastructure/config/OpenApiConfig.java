package com.liverpool.liverpooltest.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Liverpool Users API")
                        .description("User CRUD microservice with COPOMEX integration for postal code address lookup")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Liverpool Test")
                                .email("test@liverpool.com.mx")));
    }
}
