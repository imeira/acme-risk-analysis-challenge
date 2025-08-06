package com.acme.decision.application.port;

import com.acme.decision.domain.model.Rule;
import com.acme.decision.domain.model.ScoreCalculationData;

import java.util.List;
import java.util.Optional;

/**
 * Porta de entrada (Driving Port) para o motor de decisão.
 * Define o contrato para calcular scores e gerenciar regras.
 */
public interface DecisionEnginePort {
    
    /**
     * Calcula o score de risco baseado nos dados da transação.
     * 
     * @param calculationData dados para cálculo do score
     * @return score calculado
     */
    int calculateScore(ScoreCalculationData calculationData);
    
    /**
     * Cria uma nova regra.
     * 
     * @param rule regra a ser criada
     * @return regra criada com ID
     */
    Rule createRule(Rule rule);
    
    /**
     * Lista todas as regras.
     * 
     * @return lista de regras
     */
    List<Rule> getAllRules();
    
    /**
     * Busca uma regra por ID.
     * 
     * @param id ID da regra
     * @return regra encontrada ou vazio
     */
    Optional<Rule> getRuleById(Long id);
    
    /**
     * Atualiza uma regra existente.
     * 
     * @param id ID da regra
     * @param rule dados atualizados da regra
     * @return regra atualizada ou vazio se não encontrada
     */
    Optional<Rule> updateRule(Long id, Rule rule);
    
    /**
     * Exclui uma regra.
     * 
     * @param id ID da regra
     * @return true se excluída, false se não encontrada
     */
    boolean deleteRule(Long id);
}

