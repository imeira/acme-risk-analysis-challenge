package com.acme.risk.analysis.application.port;

import com.acme.risk.analysis.domain.model.ListsCheckResult;
import com.acme.risk.analysis.domain.model.TransactionData;

/**
 * Porta de saída (Driven Port) para comunicação com o Lists Service.
 * Define o contrato para verificar listas permissivas e restritivas.
 */
public interface ListsServicePort {
    
    /**
     * Verifica se os dados da transação estão em listas permissivas ou restritivas.
     * 
     * @param transactionData dados da transação
     * @return resultado da verificação das listas
     */
    ListsCheckResult checkLists(TransactionData transactionData);
}

