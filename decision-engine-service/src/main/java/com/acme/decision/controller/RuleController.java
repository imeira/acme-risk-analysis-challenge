package com.acme.decision.controller;

import com.acme.decision.dto.RuleRequest;
import com.acme.decision.dto.RuleResponse;
import com.acme.decision.service.RuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/decision-engine/rules")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RuleController {

    private final RuleService ruleService;

    @PostMapping
    public ResponseEntity<RuleResponse> createRule(@Valid @RequestBody RuleRequest request) {
        log.info("Recebida requisição de criação de regra: {}", request.getName());
        
        try {
            RuleResponse response = ruleService.createRule(request);
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
            List<RuleResponse> rules = ruleService.getAllRules();
            return ResponseEntity.ok(rules);
        } catch (Exception e) {
            log.error("Erro ao listar regras", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RuleResponse> getRuleById(@PathVariable Long id) {
        log.info("Recebida requisição de busca de regra por ID: {}", id);
        
        return ruleService.getRuleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RuleResponse> updateRule(@PathVariable Long id, @Valid @RequestBody RuleRequest request) {
        log.info("Recebida requisição de atualização de regra ID: {}", id);
        
        return ruleService.updateRule(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        log.info("Recebida requisição de exclusão de regra ID: {}", id);
        
        if (ruleService.deleteRule(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

