package com.acme.risk.analysis.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransactionData {
    
    private String cpf;
    private String ip;
    private String deviceId;
    private String txType;
    private BigDecimal txValue;
}

