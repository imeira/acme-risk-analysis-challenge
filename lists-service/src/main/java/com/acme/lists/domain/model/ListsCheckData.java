package com.acme.lists.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ListsCheckData {
    
    private String cpf;
    private String ip;
    private String deviceId;
}

