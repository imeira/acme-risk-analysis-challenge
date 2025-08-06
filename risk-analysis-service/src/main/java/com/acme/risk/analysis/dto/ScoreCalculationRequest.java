package com.acme.risk.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ScoreCalculationRequest {
    
    private String cpf;
    private String ip;
    private String deviceId;
    private String txType;
    private BigDecimal txValue;
    private boolean cpfInPermissiveList;
    private boolean cpfInRestrictiveList;
    private boolean ipInRestrictiveList;
    private boolean deviceIdInRestrictiveList;
}

