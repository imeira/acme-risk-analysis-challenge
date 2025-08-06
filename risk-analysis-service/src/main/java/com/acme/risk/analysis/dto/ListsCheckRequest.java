package com.acme.risk.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ListsCheckRequest {
    
    private String cpf;
    private String ip;
    private String deviceId;
}

