package com.acme.risk.analysis.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Serviço de domínio para gerenciamento de tokens JWT.
 */
@Service
@Slf4j
public class TokenService {

    // Mapeamento de client_id para nome do serviço
    private static final Map<String, String> CLIENT_ID_TO_SERVICE = new HashMap<>();
    
    static {
        CLIENT_ID_TO_SERVICE.put("7f073c43-d91b-4138-b7f0-85f8d73490bf", "lists-service");
        CLIENT_ID_TO_SERVICE.put("a1b2c3d4-e5f6-7890-abcd-ef1234567890", "decision-engine-service");
        CLIENT_ID_TO_SERVICE.put("12345678-90ab-cdef-1234-567890abcdef", "risk-analysis-service");
    }

    /**
     * Valida se um client_id é válido.
     * 
     * @param clientId ID do cliente a ser validado
     * @return true se o client_id for válido, false caso contrário
     */
    public boolean isValidClientId(String clientId) {
        boolean isValid = CLIENT_ID_TO_SERVICE.containsKey(clientId);
        log.debug("Validando client_id {}: {}", clientId, isValid ? "válido" : "inválido");
        return isValid;
    }

    /**
     * Obtém o nome do serviço baseado no client_id.
     * 
     * @param clientId ID do cliente
     * @return nome do serviço correspondente
     * @throws IllegalArgumentException se o client_id for inválido
     */
    public String getServiceNameByClientId(String clientId) {
        String serviceName = CLIENT_ID_TO_SERVICE.get(clientId);
        if (serviceName == null) {
            throw new IllegalArgumentException("Client ID inválido: " + clientId);
        }
        log.debug("Client_id {} mapeado para serviço: {}", clientId, serviceName);
        return serviceName;
    }

    /**
     * Obtém todos os client_ids válidos.
     * 
     * @return mapa de client_id para nome do serviço
     */
    public Map<String, String> getAllValidClientIds() {
        return new HashMap<>(CLIENT_ID_TO_SERVICE);
    }
}

