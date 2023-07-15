package com.stone.hlapi.model.dto.interfaceInfo;

import lombok.Data;

@Data
public class InterfaceInfoPayRequest {
    private String interfaceName;
    private String adminPsd;
    private String payAccount;
    private int num;
}
