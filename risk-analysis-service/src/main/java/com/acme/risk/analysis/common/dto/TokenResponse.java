package com.acme.risk.analysis.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de geração de token JWT.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta contendo token JWT gerado")
public class TokenResponse {

    @Schema(description = "Token JWT válido para autenticação", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Tipo do token", 
            example = "Bearer")
    private String tokenType;

    @Schema(description = "Tempo de expiração em segundos", 
            example = "3600")
    private Long expiresIn;

    @Schema(description = "ID do cliente para o qual o token foi gerado", 
            example = "7f073c43-d91b-4138-b7f0-85f8d73490bf")
    private String clientId;

    @Schema(description = "Nome do serviço/módulo", 
            example = "lists-service")
    private String serviceName;
}

