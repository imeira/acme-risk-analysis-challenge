package com.acme.decision.application.port;

import com.acme.decision.domain.model.Rule;

import java.util.List;
import java.util.Optional;

/**
 * Porta de saída (Driven Port) para persistência de regras.
 * Define o contrato para operações CRUD de regras.
 */
public interface RuleRepositoryPort {
    
    /**
     * Salva uma regra.
     * 
     * @param rule regra a ser salva
     * @return regra salva com ID
     */
    Rule save(Rule rule);
    
    /**
     * Busca todas as regras.
     * 
     * @return lista de todas as regras
     */
    List<Rule> findAll();
    
    /**
     * Busca uma regra por ID.
     * 
     * @param id ID da regra
     * @return regra encontrada ou vazio
     */
    Optional<Rule> findById(Long id);
    
    /**
     * Busca regras ativas por tipos de transação.
     * 
     * @param txTypes tipos de transação
     * @return lista de regras ativas
     */
    List<Rule> findByTxTypeInAndActiveTrue(List<String> txTypes);
    
    /**
     * Verifica se uma regra existe por ID.
     * 
     * @param id ID da regra
     * @return true se existe, false caso contrário
     */
    boolean existsById(Long id);
    
    /**
     * Exclui uma regra por ID.
     * 
     * @param id ID da regra
     */
    void deleteById(Long id);
}

