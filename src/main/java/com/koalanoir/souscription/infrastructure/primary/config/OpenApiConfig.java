package com.koalanoir.souscription.infrastructure.primary.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration OpenAPI / Swagger pour l'API de souscription
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Souscription")
                        .version("1.0.0")
                        .description("API REST pour la gestion des souscriptions")
                        .contact(new Contact()
                                .name("Koala Noir")
                                .url("https://koalanoir.com")
                                .email("contact@koalanoir.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
