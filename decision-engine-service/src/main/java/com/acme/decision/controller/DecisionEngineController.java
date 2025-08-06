package com.acme.decision.controller;

import com.acme.decision.dto.ScoreCalculationRequest;
import com.acme.decision.dto.ScoreCalculationResponse;
import com.acme.decision.service.DecisionEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/decision-engine")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DecisionEngineController {

    private final DecisionEngineService decisionEngineService;

    @PostMapping("/calculate-score")
    public ResponseEntity<ScoreCalculationResponse> calculateScore(@RequestBody ScoreCalculationRequest request) {
        log.info("Recebida requisição de cálculo de score: {}", request);
        
        try {
            ScoreCalculationResponse response = decisionEngineService.calculateScore(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao calcular score", e);
            return ResponseEntity.internalServerError()
                    .body(new ScoreCalculationResponse(500)); // Score padrão em caso de erro
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Decision Engine Service is running");
    }
}

