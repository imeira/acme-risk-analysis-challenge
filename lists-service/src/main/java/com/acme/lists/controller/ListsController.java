package com.acme.lists.controller;

import com.acme.lists.dto.ListsCheckRequest;
import com.acme.lists.dto.ListsCheckResponse;
import com.acme.lists.service.ListsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lists")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ListsController {

    private final ListsService listsService;

    @PostMapping("/check")
    public ResponseEntity<ListsCheckResponse> checkLists(@RequestBody ListsCheckRequest request) {
        log.info("Recebida requisição de verificação de listas: {}", request);
        
        try {
            ListsCheckResponse response = listsService.checkLists(request);
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
            listsService.reloadLists();
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
}

