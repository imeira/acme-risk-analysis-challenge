package com.acme.risk.analysis.infrastructure.controller;

import com.acme.risk.analysis.application.port.RiskAnalysisPort;
import com.acme.risk.analysis.common.dto.RiskAnalysisRequest;
import com.acme.risk.analysis.common.dto.RiskAnalysisResponse;
import com.acme.risk.analysis.domain.model.TransactionData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Adaptador de entrada (Driving Adapter) para análise de risco via REST.
 * Converte requisições HTTP em chamadas para a porta de entrada RiskAnalysisPort.
 */
@RestController
@RequestMapping("/risk-analysis")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RiskAnalysisController {

    private final RiskAnalysisPort riskAnalysisPort;

    @PostMapping
    public ResponseEntity<RiskAnalysisResponse> analyzeRisk(@Valid @RequestBody RiskAnalysisRequest request) {
        log.info("Recebida requisição de análise de risco: {}", request);
        
        try {
            // Converter DTO para modelo de domínio
            TransactionData transactionData = new TransactionData(
                    request.getCpf(),
                    request.getIp(),
                    request.getDeviceId(),
                    request.getTxType(),
                    request.getTxValue()
            );

            // Chamar a porta de entrada
            String decision = riskAnalysisPort.analyzeRisk(transactionData);
            
            return ResponseEntity.ok(new RiskAnalysisResponse(decision));
            
        } catch (Exception e) {
            log.error("Erro ao processar análise de risco", e);
            return ResponseEntity.internalServerError()
                    .body(new RiskAnalysisResponse("Negada"));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Risk Analysis Service is running");
    }
}

