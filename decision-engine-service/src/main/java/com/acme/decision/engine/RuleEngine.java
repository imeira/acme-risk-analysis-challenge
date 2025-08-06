package com.acme.decision.engine;

import com.acme.decision.dto.ScoreCalculationRequest;
import com.acme.decision.entity.Rule;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Slf4j
public class RuleEngine {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public int calculateScore(ScoreCalculationRequest request, List<Rule> rules) {
        int totalScore = 0;

        log.info("Iniciando cálculo de score para {} regras", rules.size());

        for (Rule rule : rules) {
            try {
                if (evaluateRule(rule, request)) {
                    totalScore += rule.getPoints();
                    log.info("Regra '{}' aplicada: +{} pontos (Total: {})", 
                            rule.getName(), rule.getPoints(), totalScore);
                } else {
                    log.debug("Regra '{}' não aplicada", rule.getName());
                }
            } catch (Exception e) {
                log.error("Erro ao avaliar regra '{}': {}", rule.getName(), e.getMessage());
            }
        }

        // Garantir que o score seja sempre positivo
        totalScore = Math.max(1, totalScore);

        log.info("Score final calculado: {}", totalScore);
        return totalScore;
    }

    private boolean evaluateRule(Rule rule, ScoreCalculationRequest request) throws Exception {
        JsonNode condition = objectMapper.readTree(rule.getCondition());
        
        String type = condition.get("type").asText();
        
        switch (type) {
            case "value_range":
                return evaluateValueRange(condition, request.getTxValue());
            case "cpf_permissive_list":
                return request.isCpfInPermissiveList();
            case "cpf_restrictive_list":
                return request.isCpfInRestrictiveList();
            case "ip_restrictive_list":
                return request.isIpInRestrictiveList();
            case "device_restrictive_list":
                return request.isDeviceIdInRestrictiveList();
            default:
                log.warn("Tipo de condição desconhecido: {}", type);
                return false;
        }
    }

    private boolean evaluateValueRange(JsonNode condition, BigDecimal txValue) {
        BigDecimal min = new BigDecimal(condition.get("min").asText());
        BigDecimal max = new BigDecimal(condition.get("max").asText());
        
        return txValue.compareTo(min) >= 0 && txValue.compareTo(max) <= 0;
    }
}

