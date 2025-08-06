package com.acme.lists.domain.service;

import com.acme.lists.domain.model.Lists;
import com.acme.lists.domain.model.ListsCheckData;
import com.acme.lists.domain.model.ListsCheckResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Serviço de domínio responsável pela lógica central de verificação de listas.
 * Contém as regras de negócio para verificar se dados estão em listas permissivas ou restritivas.
 */
@Service
@Slf4j
public class ListsService {

    /**
     * Verifica se os dados estão nas listas permissivas ou restritivas.
     * 
     * @param checkData dados a serem verificados
     * @param lists listas carregadas
     * @return resultado da verificação
     */
    public ListsCheckResult checkLists(ListsCheckData checkData, Lists lists) {
        log.info("Verificando listas para CPF: {}, IP: {}, Device: {}", 
                checkData.getCpf(), checkData.getIp(), checkData.getDeviceId());

        ListsCheckResult result = new ListsCheckResult();

        // Verificar CPF
        ListsCheckResult.CpfListStatus cpfStatus = new ListsCheckResult.CpfListStatus();
        cpfStatus.setInPermissiveList(lists.getCpfPermissiveList().contains(checkData.getCpf()));
        cpfStatus.setInRestrictiveList(lists.getCpfRestrictiveList().contains(checkData.getCpf()));
        result.setCpf(cpfStatus);

        // Verificar IP
        ListsCheckResult.IpListStatus ipStatus = new ListsCheckResult.IpListStatus();
        ipStatus.setInRestrictiveList(lists.getIpRestrictiveList().contains(checkData.getIp()));
        result.setIp(ipStatus);

        // Verificar Device ID
        ListsCheckResult.DeviceListStatus deviceStatus = new ListsCheckResult.DeviceListStatus();
        deviceStatus.setInRestrictiveList(lists.getDeviceRestrictiveList().contains(checkData.getDeviceId()));
        result.setDeviceId(deviceStatus);

        log.info("Resultado da verificação: CPF permissivo={}, CPF restritivo={}, IP restritivo={}, Device restritivo={}",
                cpfStatus.isInPermissiveList(), cpfStatus.isInRestrictiveList(),
                ipStatus.isInRestrictiveList(), deviceStatus.isInRestrictiveList());

        return result;
    }

    /**
     * Valida os dados de entrada para verificação.
     * 
     * @param checkData dados a serem validados
     * @throws IllegalArgumentException se os dados forem inválidos
     */
    public void validateCheckData(ListsCheckData checkData) {
        if (checkData == null) {
            throw new IllegalArgumentException("Dados de verificação não podem ser nulos");
        }
        
        if (checkData.getCpf() == null || checkData.getCpf().trim().isEmpty()) {
            throw new IllegalArgumentException("CPF é obrigatório");
        }
        
        if (checkData.getIp() == null || checkData.getIp().trim().isEmpty()) {
            throw new IllegalArgumentException("IP é obrigatório");
        }
        
        if (checkData.getDeviceId() == null || checkData.getDeviceId().trim().isEmpty()) {
            throw new IllegalArgumentException("Device ID é obrigatório");
        }
        
        log.debug("Dados de verificação validados com sucesso");
    }
}

