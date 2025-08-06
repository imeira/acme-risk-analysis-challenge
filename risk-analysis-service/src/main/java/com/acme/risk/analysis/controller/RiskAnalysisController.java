package com.acme.risk.analysis.controller;

import com.acme.risk.analysis.dto.RiskAnalysisRequest;
import com.acme.risk.analysis.dto.RiskAnalysisResponse;
import com.acme.risk.analysis.service.RiskAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/risk-analysis")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RiskAnalysisController {

    private final RiskAnalysisService riskAnalysisService;

    @PostMapping
    public ResponseEntity<RiskAnalysisResponse> analyzeRisk(@Valid @RequestBody RiskAnalysisRequest request) {
        log.info("Recebida requisição de análise de risco: {}", request);
        
        try {
            RiskAnalysisResponse response = riskAnalysisService.analyzeRisk(request);
            return ResponseEntity.ok(response);
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

