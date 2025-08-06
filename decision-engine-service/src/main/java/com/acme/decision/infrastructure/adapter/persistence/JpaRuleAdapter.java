package com.acme.decision.infrastructure.adapter.persistence;

import com.acme.decision.application.port.RuleRepositoryPort;
import com.acme.decision.domain.model.Rule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de saída (Driven Adapter) para persistência de regras via JPA.
 * Implementa a porta RuleRepositoryPort.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JpaRuleAdapter implements RuleRepositoryPort {

    private final RuleJpaRepository ruleJpaRepository;

    @Override
    public Rule save(Rule rule) {
        log.debug("Salvando regra: {}", rule.getName());
        
        RuleEntity entity = toEntity(rule);
        RuleEntity savedEntity = ruleJpaRepository.save(entity);
        
        log.debug("Regra salva com ID: {}", savedEntity.getId());
        return toDomain(savedEntity);
    }

    @Override
    public List<Rule> findAll() {
        log.debug("Buscando todas as regras");
        
        List<RuleEntity> entities = ruleJpaRepository.findAll();
        
        log.debug("Encontradas {} regras", entities.size());
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Rule> findById(Long id) {
        log.debug("Buscando regra por ID: {}", id);
        
        Optional<RuleEntity> entityOpt = ruleJpaRepository.findById(id);
        
        if (entityOpt.isPresent()) {
            log.debug("Regra encontrada: {}", entityOpt.get().getName());
            return Optional.of(toDomain(entityOpt.get()));
        } else {
            log.debug("Regra não encontrada para ID: {}", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Rule> findByTxTypeInAndActiveTrue(List<String> txTypes) {
        log.debug("Buscando regras ativas para tipos: {}", txTypes);
        
        List<RuleEntity> entities = ruleJpaRepository.findByTxTypeInAndActiveTrue(txTypes);
        
        log.debug("Encontradas {} regras ativas", entities.size());
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        log.debug("Verificando existência da regra ID: {}", id);
        
        boolean exists = ruleJpaRepository.existsById(id);
        
        log.debug("Regra ID {} existe: {}", id, exists);
        return exists;
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Excluindo regra ID: {}", id);
        
        ruleJpaRepository.deleteById(id);
        
        log.debug("Regra ID {} excluída", id);
    }

    /**
     * Converte entidade JPA para modelo de domínio.
     */
    private Rule toDomain(RuleEntity entity) {
        return new Rule(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getTxType(),
                entity.getCondition(),
                entity.getPoints(),
                entity.isActive()
        );
    }

    /**
     * Converte modelo de domínio para entidade JPA.
     */
    private RuleEntity toEntity(Rule rule) {
        return new RuleEntity(
                rule.getId(),
                rule.getName(),
                rule.getDescription(),
                rule.getTxType(),
                rule.getCondition(),
                rule.getPoints(),
                rule.isActive()
        );
    }
}

