package com.acme.decision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rule {
    
    private Long id;
    private String name;
    private String description;
    private String txType; // "DEFAULT" para regras padrão
    private String condition; // JSON string da condição
    private Integer points;
    private boolean active = true;
}

