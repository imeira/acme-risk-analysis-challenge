package com.acme.decision.common.dto;

import lombok.Data;

@Data
public class RuleResponse {
    
    private Long id;
    private String name;
    private String description;
    private String txType;
    private String condition;
    private Integer points;
    private boolean active;
}

