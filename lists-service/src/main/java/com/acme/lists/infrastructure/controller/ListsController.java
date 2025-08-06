package com.acme.lists.infrastructure.controller;

import com.acme.lists.application.port.ListsPort;
import com.acme.lists.common.dto.ListsCheckRequest;
import com.acme.lists.common.dto.ListsCheckResponse;
import com.acme.lists.domain.model.ListsCheckData;
import com.acme.lists.domain.model.ListsCheckResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Adaptador de entrada (Driving Adapter) para gerenciamento de listas via REST.
 * Converte requisições HTTP em chamadas para a porta de entrada ListsPort.
 */
@RestController
@RequestMapping("/lists")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ListsController {

    private final ListsPort listsPort;

    @PostMapping("/check")
    public ResponseEntity<ListsCheckResponse> checkLists(@RequestBody ListsCheckRequest request) {
        log.info("Recebida requisição de verificação de listas: {}", request);
        
        try {
            // Converter DTO para modelo de domínio
            ListsCheckData checkData = new ListsCheckData(
                    request.getCpf(),
                    request.getIp(),
                    request.getDeviceId()
            );

            // Chamar a porta de entrada
            ListsCheckResult result = listsPort.checkLists(checkData);
            
            // Converter resultado de domínio para DTO
            ListsCheckResponse response = convertToResponse(result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao verificar listas", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/reload")
    public ResponseEntity<String> reloadLists() {
        log.info("Recebida requisição de recarga de listas");
        
        try {
            listsPort.reloadLists();
            return ResponseEntity.ok("Listas recarregadas com sucesso");
        } catch (Exception e) {
            log.error("Erro ao recarregar listas", e);
            return ResponseEntity.internalServerError()
                    .body("Erro ao recarregar listas: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Lists Service is running");
    }

    private ListsCheckResponse convertToResponse(ListsCheckResult result) {
        ListsCheckResponse response = new ListsCheckResponse();
        
        ListsCheckResponse.CpfListStatus cpfStatus = new ListsCheckResponse.CpfListStatus();
        cpfStatus.setInPermissiveList(result.getCpf().isInPermissiveList());
        cpfStatus.setInRestrictiveList(result.getCpf().isInRestrictiveList());
        response.setCpf(cpfStatus);
        
        ListsCheckResponse.IpListStatus ipStatus = new ListsCheckResponse.IpListStatus();
        ipStatus.setInRestrictiveList(result.getIp().isInRestrictiveList());
        response.setIp(ipStatus);
        
        ListsCheckResponse.DeviceListStatus deviceStatus = new ListsCheckResponse.DeviceListStatus();
        deviceStatus.setInRestrictiveList(result.getDeviceId().isInRestrictiveList());
        response.setDeviceId(deviceStatus);
        
        return response;
    }
}

