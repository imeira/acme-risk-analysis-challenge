package com.acme.decision.application.service;

import com.acme.decision.application.port.DecisionEnginePort;
import com.acme.decision.application.port.RuleRepositoryPort;
import com.acme.decision.domain.model.Rule;
import com.acme.decision.domain.model.ScoreCalculationData;
import com.acme.decision.domain.service.RuleEngine;
import com.acme.decision.domain.service.RuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Serviço de aplicação que implementa a porta de entrada DecisionEnginePort.
 * Orquestra o cálculo de score e gerenciamento de regras.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DecisionEngineApplicationService implements DecisionEnginePort {

    private final RuleEngine ruleEngine;
    private final RuleService ruleService;
    private final RuleRepositoryPort ruleRepositoryPort;

    @Override
    public int calculateScore(ScoreCalculationData calculationData) {
        log.info("Calculando score para transação tipo: {}, valor: {}", 
                calculationData.getTxType(), calculationData.getTxValue());

        try {
            // 1. Validar dados de entrada
            ruleEngine.validateCalculationData(calculationData);

            // 2. Buscar regras aplicáveis (DEFAULT + específicas do tipo)
            List<Rule> applicableRules = ruleRepositoryPort.findByTxTypeInAndActiveTrue(
                    Arrays.asList("DEFAULT", calculationData.getTxType())
            );

            log.info("Encontradas {} regras aplicáveis", applicableRules.size());

            // 3. Calcular score usando o motor de regras
            int score = ruleEngine.calculateScore(calculationData, applicableRules);

            log.info("Score calculado com sucesso: {}", score);
            return score;

        } catch (Exception e) {
            log.error("Erro ao calcular score", e);
            // Retorna score padrão em caso de erro (score alto para negar por segurança)
            return 500;
        }
    }

    @Override
    public Rule createRule(Rule rule) {
        log.info("Criando nova regra: {}", rule.getName());

        try {
            Rule preparedRule = ruleService.prepareRuleForCreation(rule);
            Rule savedRule = ruleRepositoryPort.save(preparedRule);
            
            log.info("Regra criada com sucesso. ID: {}", savedRule.getId());
            return savedRule;

        } catch (Exception e) {
            log.error("Erro ao criar regra", e);
            throw e;
        }
    }

    @Override
    public List<Rule> getAllRules() {
        log.info("Listando todas as regras");
        return ruleRepositoryPort.findAll();
    }

    @Override
    public Optional<Rule> getRuleById(Long id) {
        log.info("Buscando regra por ID: {}", id);
        return ruleRepositoryPort.findById(id);
    }

    @Override
    public Optional<Rule> updateRule(Long id, Rule rule) {
        log.info("Atualizando regra ID: {}", id);

        try {
            Optional<Rule> existingRuleOpt = ruleRepositoryPort.findById(id);
            
            if (existingRuleOpt.isPresent()) {
                Rule existingRule = existingRuleOpt.get();
                Rule preparedRule = ruleService.prepareRuleForUpdate(existingRule, rule);
                Rule savedRule = ruleRepositoryPort.save(preparedRule);
                
                log.info("Regra atualizada com sucesso. ID: {}", savedRule.getId());
                return Optional.of(savedRule);
            } else {
                log.warn("Regra não encontrada para atualização. ID: {}", id);
                return Optional.empty();
            }

        } catch (Exception e) {
            log.error("Erro ao atualizar regra", e);
            throw e;
        }
    }

    @Override
    public boolean deleteRule(Long id) {
        log.info("Excluindo regra ID: {}", id);

        try {
            if (ruleRepositoryPort.existsById(id)) {
                ruleRepositoryPort.deleteById(id);
                log.info("Regra excluída com sucesso. ID: {}", id);
                return true;
            } else {
                log.warn("Regra não encontrada para exclusão. ID: {}", id);
                return false;
            }

        } catch (Exception e) {
            log.error("Erro ao excluir regra", e);
            throw e;
        }
    }
}

