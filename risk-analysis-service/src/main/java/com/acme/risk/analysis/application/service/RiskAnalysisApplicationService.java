package com.acme.risk.analysis.application.service;

import com.acme.risk.analysis.application.port.DecisionEngineServicePort;
import com.acme.risk.analysis.application.port.ListsServicePort;
import com.acme.risk.analysis.application.port.RiskAnalysisPort;
import com.acme.risk.analysis.domain.model.ListsCheckResult;
import com.acme.risk.analysis.domain.model.RiskScore;
import com.acme.risk.analysis.domain.model.TransactionData;
import com.acme.risk.analysis.domain.service.RiskAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Serviço de aplicação que implementa a porta de entrada RiskAnalysisPort.
 * Orquestra a análise de risco utilizando os serviços de domínio e as portas de saída.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RiskAnalysisApplicationService implements RiskAnalysisPort {

    private final RiskAnalysisService riskAnalysisService;
    private final ListsServicePort listsServicePort;
    private final DecisionEngineServicePort decisionEngineServicePort;

    @Override
    public String analyzeRisk(TransactionData transactionData) {
        log.info("Iniciando análise de risco para CPF: {}, Tipo: {}, Valor: {}", 
                transactionData.getCpf(), transactionData.getTxType(), transactionData.getTxValue());

        try {
            // 1. Validar dados da transação
            riskAnalysisService.validateTransactionData(transactionData);

            // 2. Consultar o serviço de listas
            ListsCheckResult listsResult = listsServicePort.checkLists(transactionData);
            log.debug("Resultado da verificação de listas: {}", listsResult);

            // 3. Calcular o score de risco
            RiskScore riskScore = decisionEngineServicePort.calculateScore(transactionData, listsResult);
            log.debug("Score de risco calculado: {}", riskScore.getScore());

            // 4. Determinar a decisão final
            String decision = riskAnalysisService.determineDecision(riskScore);

            log.info("Análise de risco concluída. Score: {}, Decisão: {}", riskScore.getScore(), decision);
            return decision;

        } catch (Exception e) {
            log.error("Erro durante análise de risco", e);
            // Em caso de erro, negar a transação por segurança
            return "Negada";
        }
    }
}

