package com.acme.risk.analysis.application.port;

import com.acme.risk.analysis.domain.model.ListsCheckResult;
import com.acme.risk.analysis.domain.model.RiskScore;
import com.acme.risk.analysis.domain.model.TransactionData;

/**
 * Porta de saída (Driven Port) para comunicação com o Decision Engine Service.
 * Define o contrato para calcular o score de risco.
 */
public interface DecisionEngineServicePort {
    
    /**
     * Calcula o score de risco baseado nos dados da transação e resultado das listas.
     * 
     * @param transactionData dados da transação
     * @param listsCheckResult resultado da verificação das listas
     * @return score de risco calculado
     */
    RiskScore calculateScore(TransactionData transactionData, ListsCheckResult listsCheckResult);
}

