package com.acme.risk.analysis.infrastructure.adapter.client;

import com.acme.risk.analysis.application.port.DecisionEngineServicePort;
import com.acme.risk.analysis.domain.model.ListsCheckResult;
import com.acme.risk.analysis.domain.model.RiskScore;
import com.acme.risk.analysis.domain.model.TransactionData;
import com.acme.risk.analysis.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Adaptador de saída (Driven Adapter) para comunicação com o Decision Engine Service via REST.
 * Implementa a porta DecisionEngineServicePort.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DecisionEngineServiceRestAdapter implements DecisionEngineServicePort {

    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;

    @Value("${services.decision-engine.url}")
    private String decisionEngineServiceUrl;

    @Override
    public RiskScore calculateScore(TransactionData transactionData, ListsCheckResult listsCheckResult) {
        try {
            log.info("Consultando Decision Engine Service para cálculo de score");

            // Preparar o payload da requisição
            Map<String, Object> requestPayload = new HashMap<>();
            requestPayload.put("cpf", transactionData.getCpf());
            requestPayload.put("ip", transactionData.getIp());
            requestPayload.put("deviceId", transactionData.getDeviceId());
            requestPayload.put("txType", transactionData.getTxType());
            requestPayload.put("txValue", transactionData.getTxValue());
            requestPayload.put("cpfInPermissiveList", listsCheckResult.getCpf().isInPermissiveList());
            requestPayload.put("cpfInRestrictiveList", listsCheckResult.getCpf().isInRestrictiveList());
            requestPayload.put("ipInRestrictiveList", listsCheckResult.getIp().isInRestrictiveList());
            requestPayload.put("deviceIdInRestrictiveList", listsCheckResult.getDeviceId().isInRestrictiveList());

            // Preparar headers com JWT
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + jwtUtil.generateServiceToken("risk-analysis-service"));

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestPayload, headers);

            // Fazer a chamada REST
            ResponseEntity<Map> response = restTemplate.exchange(
                    decisionEngineServiceUrl + "/decision-engine/calculate-score",
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            Integer score = (Integer) responseBody.get("score");
            
            RiskScore riskScore = new RiskScore(score != null ? score : 500);
            log.info("Score calculado pelo Decision Engine Service: {}", riskScore.getScore());
            return riskScore;

        } catch (Exception e) {
            log.error("Erro ao consultar Decision Engine Service", e);
            // Retorna score padrão em caso de erro (score alto para negar por segurança)
            return new RiskScore(500);
        }
    }
}

