package com.acme.decision.infrastructure.controller;

import com.acme.decision.application.port.DecisionEnginePort;
import com.acme.decision.common.dto.ScoreCalculationRequest;
import com.acme.decision.common.dto.ScoreCalculationResponse;
import com.acme.decision.domain.model.ScoreCalculationData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Adaptador de entrada (Driving Adapter) para cálculo de score via REST.
 * Converte requisições HTTP em chamadas para a porta de entrada DecisionEnginePort.
 */
@RestController
@RequestMapping("/decision-engine")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DecisionEngineController {

    private final DecisionEnginePort decisionEnginePort;

    @PostMapping("/calculate-score")
    public ResponseEntity<ScoreCalculationResponse> calculateScore(@RequestBody ScoreCalculationRequest request) {
        log.info("Recebida requisição de cálculo de score: {}", request);
        
        try {
            // Converter DTO para modelo de domínio
            ScoreCalculationData calculationData = new ScoreCalculationData(
                    request.getCpf(),
                    request.getIp(),
                    request.getDeviceId(),
                    request.getTxType(),
                    request.getTxValue(),
                    request.isCpfInPermissiveList(),
                    request.isCpfInRestrictiveList(),
                    request.isIpInRestrictiveList(),
                    request.isDeviceIdInRestrictiveList()
            );

            // Chamar a porta de entrada
            int score = decisionEnginePort.calculateScore(calculationData);
            
            return ResponseEntity.ok(new ScoreCalculationResponse(score));
            
        } catch (Exception e) {
            log.error("Erro ao calcular score", e);
            return ResponseEntity.internalServerError()
                    .body(new ScoreCalculationResponse(500)); // Score alto para negar por segurança
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Decision Engine Service is running");
    }
}

