package com.acme.risk.analysis.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RiskScore {
    
    private int score;
    
    public boolean isHighRisk(int threshold) {
        return score > threshold;
    }
}

