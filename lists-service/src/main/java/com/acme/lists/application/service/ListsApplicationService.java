package com.acme.lists.application.service;

import com.acme.lists.application.port.ListsPort;
import com.acme.lists.application.port.ListsRepositoryPort;
import com.acme.lists.domain.model.Lists;
import com.acme.lists.domain.model.ListsCheckData;
import com.acme.lists.domain.model.ListsCheckResult;
import com.acme.lists.domain.service.ListsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * Serviço de aplicação que implementa a porta de entrada ListsPort.
 * Orquestra a verificação de listas utilizando os serviços de domínio e as portas de saída.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ListsApplicationService implements ListsPort {

    private final ListsService listsService;
    private final ListsRepositoryPort listsRepositoryPort;
    
    private Lists currentLists;

    @PostConstruct
    public void initialize() {
        reloadLists();
    }

    @Override
    public ListsCheckResult checkLists(ListsCheckData checkData) {
        log.info("Iniciando verificação de listas");

        try {
            // 1. Validar dados de entrada
            listsService.validateCheckData(checkData);

            // 2. Verificar se as listas estão carregadas
            if (currentLists == null) {
                log.warn("Listas não carregadas, recarregando...");
                reloadLists();
            }

            // 3. Realizar a verificação
            ListsCheckResult result = listsService.checkLists(checkData, currentLists);

            log.info("Verificação de listas concluída com sucesso");
            return result;

        } catch (Exception e) {
            log.error("Erro durante verificação de listas", e);
            throw e;
        }
    }

    @Override
    public void reloadLists() {
        log.info("Recarregando listas...");
        
        try {
            currentLists = listsRepositoryPort.loadLists();
            
            log.info("Listas recarregadas com sucesso: {} CPFs permissivos, {} CPFs restritivos, {} IPs restritivos, {} devices restritivos",
                    currentLists.getCpfPermissiveList().size(),
                    currentLists.getCpfRestrictiveList().size(),
                    currentLists.getIpRestrictiveList().size(),
                    currentLists.getDeviceRestrictiveList().size());
                    
        } catch (Exception e) {
            log.error("Erro ao recarregar listas", e);
            throw new RuntimeException("Falha ao recarregar listas", e);
        }
    }
}

