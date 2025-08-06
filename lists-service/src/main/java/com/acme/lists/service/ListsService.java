package com.acme.lists.service;

import com.acme.lists.dto.ListsCheckRequest;
import com.acme.lists.dto.ListsCheckResponse;
import com.acme.lists.model.Lists;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;

@Service
@Slf4j
public class ListsService {

    @Value("classpath:lists.json")
    private Resource listsResource;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Lists lists;

    @PostConstruct
    public void loadLists() {
        try {
            lists = objectMapper.readValue(listsResource.getInputStream(), Lists.class);
            log.info("Listas carregadas com sucesso: {} CPFs permissivos, {} CPFs restritivos, {} IPs restritivos, {} devices restritivos",
                    lists.getCpfPermissiveList().size(),
                    lists.getCpfRestrictiveList().size(),
                    lists.getIpRestrictiveList().size(),
                    lists.getDeviceRestrictiveList().size());
        } catch (IOException e) {
            log.error("Erro ao carregar listas", e);
            // Inicializa listas vazias em caso de erro
            lists = new Lists();
            lists.setCpfPermissiveList(new HashSet<>());
            lists.setCpfRestrictiveList(new HashSet<>());
            lists.setIpRestrictiveList(new HashSet<>());
            lists.setDeviceRestrictiveList(new HashSet<>());
        }
    }

    public ListsCheckResponse checkLists(ListsCheckRequest request) {
        log.info("Verificando listas para CPF: {}, IP: {}, Device: {}", 
                request.getCpf(), request.getIp(), request.getDeviceId());

        ListsCheckResponse response = new ListsCheckResponse();

        // Verificar CPF
        ListsCheckResponse.CpfListStatus cpfStatus = new ListsCheckResponse.CpfListStatus();
        cpfStatus.setInPermissiveList(lists.getCpfPermissiveList().contains(request.getCpf()));
        cpfStatus.setInRestrictiveList(lists.getCpfRestrictiveList().contains(request.getCpf()));
        response.setCpf(cpfStatus);

        // Verificar IP
        ListsCheckResponse.IpListStatus ipStatus = new ListsCheckResponse.IpListStatus();
        ipStatus.setInRestrictiveList(lists.getIpRestrictiveList().contains(request.getIp()));
        response.setIp(ipStatus);

        // Verificar Device ID
        ListsCheckResponse.DeviceListStatus deviceStatus = new ListsCheckResponse.DeviceListStatus();
        deviceStatus.setInRestrictiveList(lists.getDeviceRestrictiveList().contains(request.getDeviceId()));
        response.setDeviceId(deviceStatus);

        log.info("Resultado da verificação: CPF permissivo={}, CPF restritivo={}, IP restritivo={}, Device restritivo={}",
                cpfStatus.isInPermissiveList(), cpfStatus.isInRestrictiveList(),
                ipStatus.isInRestrictiveList(), deviceStatus.isInRestrictiveList());

        return response;
    }

    public void reloadLists() {
        log.info("Recarregando listas...");
        loadLists();
    }
}

