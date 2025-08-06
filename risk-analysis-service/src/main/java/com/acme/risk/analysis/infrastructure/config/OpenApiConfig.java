package com.acme.risk.analysis.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do OpenAPI/Swagger para o Risk Analysis Service.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI riskAnalysisOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Risk Analysis Service API")
                        .description("API para análise de risco de transações financeiras. " +
                                "Este serviço orquestra o fluxo de análise consultando listas " +
                                "permissivas/restritivas e aplicando regras de negócio para " +
                                "calcular o score de risco e decidir sobre a aprovação da transação.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ACME Risk Analysis Team")
                                .email("risk-analysis@acme.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}

