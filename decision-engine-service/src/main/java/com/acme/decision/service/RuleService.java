package com.acme.decision.service;

import com.acme.decision.dto.RuleRequest;
import com.acme.decision.dto.RuleResponse;
import com.acme.decision.entity.Rule;
import com.acme.decision.repository.RuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RuleService {

    private final RuleRepository ruleRepository;

    public RuleResponse createRule(RuleRequest request) {
        log.info("Criando nova regra: {}", request.getName());

        Rule rule = new Rule();
        rule.setName(request.getName());
        rule.setDescription(request.getDescription());
        rule.setTxType(request.getTxType());
        rule.setCondition(request.getCondition());
        rule.setPoints(request.getPoints());
        rule.setActive(request.isActive());

        Rule savedRule = ruleRepository.save(rule);
        
        log.info("Regra criada com ID: {}", savedRule.getId());
        
        return mapToResponse(savedRule);
    }

    public List<RuleResponse> getAllRules() {
        return ruleRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Optional<RuleResponse> getRuleById(Long id) {
        return ruleRepository.findById(id)
                .map(this::mapToResponse);
    }

    public Optional<RuleResponse> updateRule(Long id, RuleRequest request) {
        return ruleRepository.findById(id)
                .map(rule -> {
                    log.info("Atualizando regra ID: {}", id);
                    
                    rule.setName(request.getName());
                    rule.setDescription(request.getDescription());
                    rule.setTxType(request.getTxType());
                    rule.setCondition(request.getCondition());
                    rule.setPoints(request.getPoints());
                    rule.setActive(request.isActive());
                    
                    Rule savedRule = ruleRepository.save(rule);
                    return mapToResponse(savedRule);
                });
    }

    public boolean deleteRule(Long id) {
        if (ruleRepository.existsById(id)) {
            log.info("Excluindo regra ID: {}", id);
            ruleRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private RuleResponse mapToResponse(Rule rule) {
        RuleResponse response = new RuleResponse();
        response.setId(rule.getId());
        response.setName(rule.getName());
        response.setDescription(rule.getDescription());
        response.setTxType(rule.getTxType());
        response.setCondition(rule.getCondition());
        response.setPoints(rule.getPoints());
        response.setActive(rule.isActive());
        return response;
    }
}

