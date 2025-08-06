package com.acme.decision.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RuleRequest {
    
    @NotBlank(message = "Nome da regra é obrigatório")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Tipo da transação é obrigatório")
    private String txType; // "DEFAULT" para regras padrão
    
    @NotBlank(message = "Condição é obrigatória")
    private String condition; // JSON string da condição
    
    @NotNull(message = "Pontos são obrigatórios")
    private Integer points;
    
    private boolean active = true;
}

