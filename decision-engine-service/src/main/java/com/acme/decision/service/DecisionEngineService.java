package com.acme.decision.service;

import com.acme.decision.dto.ScoreCalculationRequest;
import com.acme.decision.dto.ScoreCalculationResponse;
import com.acme.decision.engine.RuleEngine;
import com.acme.decision.entity.Rule;
import com.acme.decision.repository.RuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DecisionEngineService {

    private final RuleRepository ruleRepository;
    private final RuleEngine ruleEngine;

    public ScoreCalculationResponse calculateScore(ScoreCalculationRequest request) {
        log.info("Calculando score para transação tipo: {}, valor: {}", 
                request.getTxType(), request.getTxValue());

        // Buscar regras aplicáveis (DEFAULT + específicas do tipo)
        List<Rule> applicableRules = ruleRepository.findByTxTypeInAndActiveTrue(
                Arrays.asList("DEFAULT", request.getTxType())
        );

        log.info("Encontradas {} regras aplicáveis", applicableRules.size());

        int score = ruleEngine.calculateScore(request, applicableRules);

        return new ScoreCalculationResponse(score);
    }
}

