package com.acme.decision.domain.service;

import com.acme.decision.domain.model.Rule;
import com.acme.decision.domain.model.ScoreCalculationData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Serviço de domínio responsável pela avaliação de regras e cálculo de score.
 * Contém a lógica central do motor de regras.
 */
@Service
@Slf4j
public class RuleEngine {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Calcula o score baseado nas regras aplicáveis.
     * 
     * @param calculationData dados para cálculo
     * @param rules regras a serem aplicadas
     * @return score calculado
     */
    public int calculateScore(ScoreCalculationData calculationData, List<Rule> rules) {
        int totalScore = 0;

        log.info("Iniciando cálculo de score para {} regras", rules.size());

        for (Rule rule : rules) {
            try {
                if (evaluateRule(rule, calculationData)) {
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

    /**
     * Avalia se uma regra deve ser aplicada.
     * 
     * @param rule regra a ser avaliada
     * @param calculationData dados para avaliação
     * @return true se a regra deve ser aplicada, false caso contrário
     */
    private boolean evaluateRule(Rule rule, ScoreCalculationData calculationData) throws Exception {
        JsonNode condition = objectMapper.readTree(rule.getCondition());
        
        String type = condition.get("type").asText();
        
        switch (type) {
            case "value_range":
                return evaluateValueRange(condition, calculationData.getTxValue());
            case "cpf_permissive_list":
                return calculationData.isCpfInPermissiveList();
            case "cpf_restrictive_list":
                return calculationData.isCpfInRestrictiveList();
            case "ip_restrictive_list":
                return calculationData.isIpInRestrictiveList();
            case "device_restrictive_list":
                return calculationData.isDeviceIdInRestrictiveList();
            default:
                log.warn("Tipo de condição desconhecido: {}", type);
                return false;
        }
    }

    /**
     * Avalia uma condição de faixa de valor.
     * 
     * @param condition condição JSON
     * @param txValue valor da transação
     * @return true se o valor está na faixa, false caso contrário
     */
    private boolean evaluateValueRange(JsonNode condition, BigDecimal txValue) {
        BigDecimal min = new BigDecimal(condition.get("min").asText());
        BigDecimal max = new BigDecimal(condition.get("max").asText());
        
        return txValue.compareTo(min) >= 0 && txValue.compareTo(max) <= 0;
    }

    /**
     * Valida os dados de cálculo.
     * 
     * @param calculationData dados a serem validados
     * @throws IllegalArgumentException se os dados forem inválidos
     */
    public void validateCalculationData(ScoreCalculationData calculationData) {
        if (calculationData == null) {
            throw new IllegalArgumentException("Dados de cálculo não podem ser nulos");
        }
        
        if (calculationData.getTxValue() == null || calculationData.getTxValue().signum() <= 0) {
            throw new IllegalArgumentException("Valor da transação deve ser positivo");
        }
        
        if (calculationData.getTxType() == null || calculationData.getTxType().trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo da transação é obrigatório");
        }
        
        log.debug("Dados de cálculo validados com sucesso");
    }
}

