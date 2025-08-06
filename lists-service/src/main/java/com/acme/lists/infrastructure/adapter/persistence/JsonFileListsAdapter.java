package com.acme.lists.infrastructure.adapter.persistence;

import com.acme.lists.application.port.ListsRepositoryPort;
import com.acme.lists.domain.model.Lists;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;

/**
 * Adaptador de saída (Driven Adapter) para carregar listas de um arquivo JSON.
 * Implementa a porta ListsRepositoryPort.
 */
@Component
@Slf4j
public class JsonFileListsAdapter implements ListsRepositoryPort {

    @Value("classpath:lists.json")
    private Resource listsResource;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Lists loadLists() {
        try {
            log.info("Carregando listas do arquivo JSON");
            
            Lists lists = objectMapper.readValue(listsResource.getInputStream(), Lists.class);
            
            log.info("Listas carregadas com sucesso do arquivo JSON");
            return lists;
            
        } catch (IOException e) {
            log.error("Erro ao carregar listas do arquivo JSON", e);
            
            // Retorna listas vazias em caso de erro
            Lists emptyLists = new Lists();
            emptyLists.setCpfPermissiveList(new HashSet<>());
            emptyLists.setCpfRestrictiveList(new HashSet<>());
            emptyLists.setIpRestrictiveList(new HashSet<>());
            emptyLists.setDeviceRestrictiveList(new HashSet<>());
            
            log.warn("Retornando listas vazias devido ao erro");
            return emptyLists;
        }
    }
}

