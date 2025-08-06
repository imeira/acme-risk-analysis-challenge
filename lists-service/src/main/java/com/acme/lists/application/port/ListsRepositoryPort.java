package com.acme.lists.application.port;

import com.acme.lists.domain.model.Lists;

/**
 * Porta de saída (Driven Port) para acesso aos dados das listas.
 * Define o contrato para carregar listas de uma fonte de dados.
 */
public interface ListsRepositoryPort {
    
    /**
     * Carrega as listas da fonte de dados.
     * 
     * @return listas carregadas
     */
    Lists loadLists();
}

