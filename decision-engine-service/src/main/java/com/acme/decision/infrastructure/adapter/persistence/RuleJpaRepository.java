package com.acme.decision.infrastructure.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório JPA para operações de persistência de regras.
 */
@Repository
public interface RuleJpaRepository extends JpaRepository<RuleEntity, Long> {
    
    /**
     * Busca regras ativas por tipos de transação.
     * 
     * @param txTypes tipos de transação
     * @return lista de regras ativas
     */
    List<RuleEntity> findByTxTypeInAndActiveTrue(List<String> txTypes);
}

