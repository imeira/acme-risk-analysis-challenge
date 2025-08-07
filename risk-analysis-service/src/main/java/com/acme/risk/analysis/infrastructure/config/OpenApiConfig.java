package com.acme.risk.analysis.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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
                                "calcular o score de risco e decidir sobre a aprovação da transação.\n\n" +
                                "**IMPORTANTE: A rota /auth existe, mas exposta APENAS EM DESENVOLVIMENTO para facilitar testes. Assim como os client_ids abaixo:**\n" +
                                "\n" +
                                "**Para testar as APIs protegidas:**\n" +
                                "1. Use o endpoint `/auth/token` para gerar um token JWT\n" +
                                "2. Client IDs válidos:\n" +
                                "   - Lists Service: `7f073c43-d91b-4138-b7f0-85f8d73490bf`\n" +
                                "   - Decision Engine: `a1b2c3d4-e5f6-7890-abcd-ef1234567890`\n" +
                                "   - Risk Analysis: `12345678-90ab-cdef-1234-567890abcdef`\n" +
                                "3. Use o token no cabeçalho: `Authorization: Bearer <token>`")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Igor Meira")
                                .email("meira.igor@gmail.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de desenvolvimento")
                ));
    }
}

