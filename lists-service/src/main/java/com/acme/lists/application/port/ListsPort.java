package com.acme.lists.application.port;

import com.acme.lists.domain.model.ListsCheckData;
import com.acme.lists.domain.model.ListsCheckResult;

/**
 * Porta de entrada (Driving Port) para gerenciamento de listas.
 * Define o contrato para verificar e recarregar listas.
 */
public interface ListsPort {
    
    /**
     * Verifica se os dados estão em listas permissivas ou restritivas.
     * 
     * @param checkData dados a serem verificados
     * @return resultado da verificação
     */
    ListsCheckResult checkLists(ListsCheckData checkData);
    
    /**
     * Recarrega as listas a partir da fonte de dados.
     */
    void reloadLists();
}

