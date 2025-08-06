package com.acme.decision.infrastructure.controller;

import com.acme.decision.application.port.DecisionEnginePort;
import com.acme.decision.common.dto.RuleRequest;
import com.acme.decision.common.dto.RuleResponse;
import com.acme.decision.domain.model.Rule;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de entrada (Driving Adapter) para gerenciamento de regras via REST.
 * Converte requisições HTTP em chamadas para a porta de entrada DecisionEnginePort.
 */
@RestController
@RequestMapping("/rules")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RuleController {

    private final DecisionEnginePort decisionEnginePort;

    @PostMapping
    public ResponseEntity<RuleResponse> createRule(@Valid @RequestBody RuleRequest request) {
        log.info("Recebida requisição de criação de regra: {}", request.getName());
        
        try {
            // Converter DTO para modelo de domínio
            Rule rule = new Rule(
                    null, // ID será gerado
                    request.getName(),
                    request.getDescription(),
                    request.getTxType(),
                    request.getCondition(),
                    request.getPoints(),
                    request.isActive()
            );

            // Chamar a porta de entrada
            Rule createdRule = decisionEnginePort.createRule(rule);
            
            // Converter resultado para DTO
            RuleResponse response = toResponse(createdRule);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Erro ao criar regra", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<RuleResponse>> getAllRules() {
        log.info("Recebida requisição de listagem de regras");
        
        try {
            List<Rule> rules = decisionEnginePort.getAllRules();
            
            List<RuleResponse> responses = rules.stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            log.error("Erro ao listar regras", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RuleResponse> getRuleById(@PathVariable Long id) {
        log.info("Recebida requisição de busca de regra por ID: {}", id);
        
        try {
            Optional<Rule> ruleOpt = decisionEnginePort.getRuleById(id);
            
            if (ruleOpt.isPresent()) {
                RuleResponse response = toResponse(ruleOpt.get());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            log.error("Erro ao buscar regra por ID", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<RuleResponse> updateRule(@PathVariable Long id, @Valid @RequestBody RuleRequest request) {
        log.info("Recebida requisição de atualização de regra ID: {}", id);
        
        try {
            // Converter DTO para modelo de domínio
            Rule rule = new Rule(
                    null, // ID será definido pelo serviço
                    request.getName(),
                    request.getDescription(),
                    request.getTxType(),
                    request.getCondition(),
                    request.getPoints(),
                    request.isActive()
            );

            // Chamar a porta de entrada
            Optional<Rule> updatedRuleOpt = decisionEnginePort.updateRule(id, rule);
            
            if (updatedRuleOpt.isPresent()) {
                RuleResponse response = toResponse(updatedRuleOpt.get());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            log.error("Erro ao atualizar regra", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        log.info("Recebida requisição de exclusão de regra ID: {}", id);
        
        try {
            boolean deleted = decisionEnginePort.deleteRule(id);
            
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            log.error("Erro ao excluir regra", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Converte modelo de domínio para DTO de resposta.
     */
    private RuleResponse toResponse(Rule rule) {
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

