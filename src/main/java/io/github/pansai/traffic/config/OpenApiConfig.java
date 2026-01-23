package io.github.pansai.traffic.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("NZ Traffic Analysis Platform API Document - DEMO")
                .version("v1")
                .description("Backend REST API documentation for personal use")
        );
    }
}
