package com.example.banca.infrastructure.rest.api;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() throws Exception {
        Yaml yaml = new Yaml();
        try (InputStream in = new ClassPathResource("openapi/bank-api.yaml").getInputStream()) {
            return yaml.loadAs(in, OpenAPI.class);
        }
    }
}
