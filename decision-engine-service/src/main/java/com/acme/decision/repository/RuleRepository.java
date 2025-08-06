package com.acme.decision.repository;

import com.acme.decision.entity.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {
    
    List<Rule> findByActiveTrue();
    
    List<Rule> findByTxTypeAndActiveTrue(String txType);
    
    List<Rule> findByTxTypeInAndActiveTrue(List<String> txTypes);
}

