package com.acme.lists.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do OpenAPI/Swagger para o Lists Service.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI listsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Lists Service API")
                        .description("API para gerenciamento de listas permissivas e restritivas. " +
                                "Este serviço verifica se CPF, IP ou Device ID estão presentes " +
                                "nas listas configuradas, auxiliando na análise de risco de transações.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Igor Meira")
                                .email("meira.igor@gmail.com")))
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

