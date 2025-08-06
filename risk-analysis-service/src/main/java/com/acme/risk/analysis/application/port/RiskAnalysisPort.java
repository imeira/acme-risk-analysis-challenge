package com.acme.risk.analysis.application.port;

import com.acme.risk.analysis.domain.model.TransactionData;

/**
 * Porta de entrada (Driving Port) para análise de risco.
 * Define o contrato para iniciar uma análise de risco de transação.
 */
public interface RiskAnalysisPort {
    
    /**
     * Analisa o risco de uma transação e retorna a decisão.
     * 
     * @param transactionData dados da transação a ser analisada
     * @return decisão da análise ("Aprovada" ou "Negada")
     */
    String analyzeRisk(TransactionData transactionData);
}

