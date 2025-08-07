package com.acme.risk.analysis.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de geração de token JWT.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para geração de token JWT")
public class TokenRequest {

    @Schema(description = "ID do cliente/módulo que solicita o token", 
            example = "7f073c43-d91b-4138-b7f0-85f8d73490bf",
            required = true)
    private String clientId;
}

