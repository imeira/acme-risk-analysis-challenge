package com.acme.lists.domain.model;

import lombok.Data;

import java.util.Set;

@Data
public class Lists {
    
    private Set<String> cpfPermissiveList;
    private Set<String> cpfRestrictiveList;
    private Set<String> ipRestrictiveList;
    private Set<String> deviceRestrictiveList;
}

