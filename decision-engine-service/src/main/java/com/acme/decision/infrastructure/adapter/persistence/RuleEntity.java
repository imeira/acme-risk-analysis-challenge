package com.acme.decision.infrastructure.adapter.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade JPA para persistência de regras.
 */
@Entity
@Table(name = "rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "tx_type", nullable = false)
    private String txType;
    
    @Column(nullable = false, length = 1000)
    private String condition;
    
    @Column(nullable = false)
    private Integer points;
    
    @Column(nullable = false)
    private boolean active = true;
}

