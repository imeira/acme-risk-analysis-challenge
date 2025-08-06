package com.acme.decision.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "rules")
@Data
public class Rule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(name = "tx_type", nullable = false)
    private String txType; // "DEFAULT" para regras padrão
    
    @Column(name = "condition_json", nullable = false, columnDefinition = "TEXT")
    private String condition; // JSON string da condição
    
    @Column(nullable = false)
    private Integer points;
    
    @Column(nullable = false)
    private boolean active = true;
}

