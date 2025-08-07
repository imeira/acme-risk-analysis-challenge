package com.acme.risk.analysis.infrastructure.controller;

import com.acme.risk.analysis.common.dto.TokenResponse;
import com.acme.risk.analysis.domain.service.TokenService;
import com.acme.risk.analysis.infrastructure.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller para geração de tokens JWT para testes.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticação", description = "Endpoints para geração de tokens JWT para testes")
public class TokenController {

    private final TokenService tokenService;
    private final JwtUtil jwtUtil;

    @Value("${jwt.expiration:3600000}") // 1 hora em millisegundos
    private long jwtExpiration;

    @Operation(
        summary = "Gerar token JWT para testes",
        description = "Gera um token JWT válido baseado no client_id do módulo. " +
                     "Este endpoint facilita os testes das APIs através da interface Swagger UI. " +
                     "Client IDs válidos: " +
                     "Lists Service: 7f073c43-d91b-4138-b7f0-85f8d73490bf, " +
                     "Decision Engine: a1b2c3d4-e5f6-7890-abcd-ef1234567890, " +
                     "Risk Analysis: 12345678-90ab-cdef-1234-567890abcdef"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Token JWT gerado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TokenResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Client ID inválido",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Client ID inválido\"}")
            )
        )
    })
    @GetMapping("/token")
    public ResponseEntity<?> generateToken(
        @Parameter(
            description = "ID do cliente/módulo que solicita o token",
            required = true,
            examples = {
                @ExampleObject(
                    name = "Lists Service",
                    value = "7f073c43-d91b-4138-b7f0-85f8d73490bf"
                ),
                @ExampleObject(
                    name = "Decision Engine Service",
                    value = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
                ),
                @ExampleObject(
                    name = "Risk Analysis Service",
                    value = "12345678-90ab-cdef-1234-567890abcdef"
                )
            }
        )
        @RequestParam String clientId
    ) {
        log.info("Solicitação de token para client_id: {}", clientId);

        try {
            // Validar client_id
            if (!tokenService.isValidClientId(clientId)) {
                log.warn("Tentativa de geração de token com client_id inválido: {}", clientId);
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Client ID inválido: " + clientId));
            }

            // Obter nome do serviço
            String serviceName = tokenService.getServiceNameByClientId(clientId);

            // Gerar token JWT
            String token = jwtUtil.generateServiceToken(serviceName);

            // Criar resposta
            TokenResponse response = new TokenResponse(
                token,
                "Bearer",
                jwtExpiration / 1000, // converter para segundos
                clientId,
                serviceName
            );

            log.info("Token gerado com sucesso para client_id: {} (serviço: {})", clientId, serviceName);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erro ao gerar token para client_id: {}", clientId, e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erro interno do servidor"));
        }
    }

    @Operation(
        summary = "Listar client IDs válidos",
        description = "Retorna todos os client IDs válidos e seus respectivos serviços"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de client IDs válidos",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = "{\n" +
                       "  \"7f073c43-d91b-4138-b7f0-85f8d73490bf\": \"lists-service\",\n" +
                       "  \"a1b2c3d4-e5f6-7890-abcd-ef1234567890\": \"decision-engine-service\",\n" +
                       "  \"12345678-90ab-cdef-1234-567890abcdef\": \"risk-analysis-service\"\n" +
                       "}"
            )
        )
    )
    @GetMapping("/client-ids")
    public ResponseEntity<Map<String, String>> getValidClientIds() {
        log.info("Solicitação de lista de client IDs válidos");
        Map<String, String> clientIds = tokenService.getAllValidClientIds();
        return ResponseEntity.ok(clientIds);
    }
}

