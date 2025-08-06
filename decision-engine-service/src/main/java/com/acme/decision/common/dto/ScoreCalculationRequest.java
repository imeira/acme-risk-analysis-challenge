package com.acme.decision.common.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
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

