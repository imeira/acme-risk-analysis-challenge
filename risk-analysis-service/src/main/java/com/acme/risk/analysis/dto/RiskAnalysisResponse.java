package com.acme.risk.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RiskAnalysisResponse {
    
    private String txDecision; // "Aprovada" ou "Negada"
}

