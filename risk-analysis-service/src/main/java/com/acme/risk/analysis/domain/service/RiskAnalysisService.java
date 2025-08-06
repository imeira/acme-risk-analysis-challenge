package com.acme.risk.analysis.domain.service;

import com.acme.risk.analysis.domain.model.ListsCheckResult;
import com.acme.risk.analysis.domain.model.RiskScore;
import com.acme.risk.analysis.domain.model.TransactionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Serviço de domínio responsável pela lógica central de análise de risco.
 * Contém as regras de negócio para determinar se uma transação deve ser aprovada ou negada.
 */
@Service
@Slf4j
public class RiskAnalysisService {

    @Value("${risk.score.medium.max:699}")
    private int mediumRiskMaxScore;

    /**
     * Determina a decisão final baseada no score de risco calculado.
     * 
     * @param riskScore score de risco
     * @return decisão ("Aprovada" ou "Negada")
     */
    public String determineDecision(RiskScore riskScore) {
        log.info("Determinando decisão para score: {}", riskScore.getScore());
        
        if (riskScore.isHighRisk(mediumRiskMaxScore)) {
            log.info("Transação negada - score alto: {}", riskScore.getScore());
            return "Negada";
        } else {
            log.info("Transação aprovada - score aceitável: {}", riskScore.getScore());
            return "Aprovada";
        }
    }

    /**
     * Valida os dados da transação antes do processamento.
     * 
     * @param transactionData dados da transação
     * @throws IllegalArgumentException se os dados forem inválidos
     */
    public void validateTransactionData(TransactionData transactionData) {
        if (transactionData == null) {
            throw new IllegalArgumentException("Dados da transação não podem ser nulos");
        }
        
        if (transactionData.getTxValue() == null || transactionData.getTxValue().signum() <= 0) {
            throw new IllegalArgumentException("Valor da transação deve ser positivo");
        }
        
        log.debug("Dados da transação validados com sucesso");
    }
}

