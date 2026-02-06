package com.ms.movimientos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Movimientos ")
                        .version("1.0.0")
                        .description("API REST completa para gestión de movimientos bancarios")
                        .contact(new Contact()
                                .name("Desarrollador")
                                .email("henry.2154@hotmail.com"))
                        .license(new License()
                                .name("MIT")));
    }
}
