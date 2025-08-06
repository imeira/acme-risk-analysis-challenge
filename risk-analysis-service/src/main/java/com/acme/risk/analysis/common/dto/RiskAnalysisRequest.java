package com.acme.risk.analysis.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RiskAnalysisRequest {
    
    @NotBlank(message = "CPF é obrigatório")
    private String cpf;
    
    @NotBlank(message = "IP é obrigatório")
    private String ip;
    
    @NotBlank(message = "Device ID é obrigatório")
    private String deviceId;
    
    @NotBlank(message = "Tipo da transação é obrigatório")
    private String txType;
    
    @NotNull(message = "Valor da transação é obrigatório")
    @Positive(message = "Valor da transação deve ser positivo")
    private BigDecimal txValue;
}

