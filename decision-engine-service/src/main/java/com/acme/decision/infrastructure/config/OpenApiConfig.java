package com.acme.decision.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do OpenAPI/Swagger para o Decision Engine Service.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI decisionEngineOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Decision Engine Service API")
                        .description("API para motor de decisão e gerenciamento de regras de negócio. " +
                                "Este serviço aplica regras dinâmicas configuráveis para calcular " +
                                "o score de risco de transações financeiras e permite o CRUD de regras.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Igor Meira")
                                .email("meira.igor@gmail.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Token JWT para autenticação entre microsserviços")));
    }
}

