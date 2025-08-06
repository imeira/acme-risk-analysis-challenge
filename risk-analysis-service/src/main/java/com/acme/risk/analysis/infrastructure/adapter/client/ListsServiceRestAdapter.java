package com.acme.risk.analysis.infrastructure.adapter.client;

import com.acme.risk.analysis.application.port.ListsServicePort;
import com.acme.risk.analysis.domain.model.ListsCheckResult;
import com.acme.risk.analysis.domain.model.TransactionData;
import com.acme.risk.analysis.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Adaptador de saída (Driven Adapter) para comunicação com o Lists Service via REST.
 * Implementa a porta ListsServicePort.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ListsServiceRestAdapter implements ListsServicePort {

    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;

    @Value("${services.lists.url}")
    private String listsServiceUrl;

    @Override
    public ListsCheckResult checkLists(TransactionData transactionData) {
        try {
            log.info("Consultando Lists Service para CPF: {}, IP: {}, Device: {}", 
                    transactionData.getCpf(), transactionData.getIp(), transactionData.getDeviceId());

            // Preparar o payload da requisição
            Map<String, String> requestPayload = new HashMap<>();
            requestPayload.put("cpf", transactionData.getCpf());
            requestPayload.put("ip", transactionData.getIp());
            requestPayload.put("deviceId", transactionData.getDeviceId());

            // Preparar headers com JWT
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + jwtUtil.generateServiceToken("risk-analysis-service"));

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestPayload, headers);

            // Fazer a chamada REST
            ResponseEntity<ListsCheckResult> response = restTemplate.exchange(
                    listsServiceUrl + "/lists/check",
                    HttpMethod.POST,
                    requestEntity,
                    ListsCheckResult.class
            );

            ListsCheckResult result = response.getBody();
            log.info("Resposta do Lists Service recebida: {}", result);
            return result;

        } catch (Exception e) {
            log.error("Erro ao consultar Lists Service", e);
            // Retorna resposta padrão em caso de erro
            return createDefaultListsCheckResult();
        }
    }

    private ListsCheckResult createDefaultListsCheckResult() {
        ListsCheckResult result = new ListsCheckResult();
        
        ListsCheckResult.CpfListStatus cpfStatus = new ListsCheckResult.CpfListStatus();
        cpfStatus.setInPermissiveList(false);
        cpfStatus.setInRestrictiveList(false);
        result.setCpf(cpfStatus);
        
        ListsCheckResult.IpListStatus ipStatus = new ListsCheckResult.IpListStatus();
        ipStatus.setInRestrictiveList(false);
        result.setIp(ipStatus);
        
        ListsCheckResult.DeviceListStatus deviceStatus = new ListsCheckResult.DeviceListStatus();
        deviceStatus.setInRestrictiveList(false);
        result.setDeviceId(deviceStatus);
        
        return result;
    }
}

