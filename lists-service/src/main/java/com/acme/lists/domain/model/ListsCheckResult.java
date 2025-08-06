package com.acme.lists.domain.model;

import lombok.Data;

@Data
public class ListsCheckResult {
    
    private CpfListStatus cpf;
    private IpListStatus ip;
    private DeviceListStatus deviceId;
    
    @Data
    public static class CpfListStatus {
        private boolean inPermissiveList;
        private boolean inRestrictiveList;
    }
    
    @Data
    public static class IpListStatus {
        private boolean inRestrictiveList;
    }
    
    @Data
    public static class DeviceListStatus {
        private boolean inRestrictiveList;
    }
}

