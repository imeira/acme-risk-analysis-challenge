package com.acme.decision.domain.service;

import com.acme.decision.domain.model.Rule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Serviço de domínio responsável pela lógica de negócio relacionada às regras.
 */
@Service
@Slf4j
public class RuleService {

    /**
     * Valida uma regra antes de salvar.
     * 
     * @param rule regra a ser validada
     * @throws IllegalArgumentException se a regra for inválida
     */
    public void validateRule(Rule rule) {
        if (rule == null) {
            throw new IllegalArgumentException("Regra não pode ser nula");
        }
        
        if (rule.getName() == null || rule.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da regra é obrigatório");
        }
        
        if (rule.getTxType() == null || rule.getTxType().trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo da transação é obrigatório");
        }
        
        if (rule.getCondition() == null || rule.getCondition().trim().isEmpty()) {
            throw new IllegalArgumentException("Condição da regra é obrigatória");
        }
        
        if (rule.getPoints() == null) {
            throw new IllegalArgumentException("Pontos da regra são obrigatórios");
        }
        
        log.debug("Regra validada com sucesso: {}", rule.getName());
    }

    /**
     * Prepara uma regra para criação.
     * 
     * @param rule regra a ser preparada
     * @return regra preparada
     */
    public Rule prepareRuleForCreation(Rule rule) {
        validateRule(rule);
        
        // Garantir que ID seja nulo para criação
        rule.setId(null);
        
        log.info("Regra preparada para criação: {}", rule.getName());
        return rule;
    }

    /**
     * Prepara uma regra para atualização.
     * 
     * @param existingRule regra existente
     * @param updatedRule dados atualizados
     * @return regra preparada para atualização
     */
    public Rule prepareRuleForUpdate(Rule existingRule, Rule updatedRule) {
        validateRule(updatedRule);
        
        // Manter o ID da regra existente
        updatedRule.setId(existingRule.getId());
        
        log.info("Regra preparada para atualização: {}", updatedRule.getName());
        return updatedRule;
    }
}

