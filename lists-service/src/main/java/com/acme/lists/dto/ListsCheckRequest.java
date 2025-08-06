package com.acme.lists.dto;

import lombok.Data;

@Data
public class ListsCheckRequest {
    
    private String cpf;
    private String ip;
    private String deviceId;
}

