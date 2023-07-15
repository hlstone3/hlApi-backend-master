package com.stone.hlapi.model.dto.interfaceInfo;

import lombok.Data;

@Data
public class InterfaceInfoInvokeRequest {
    /**
     * id
     */
    private Long id;

    /**
     * 用户请求调用参数
     */
    private String userRequestParams;

    private static final long serialVersionUID = 1L;
}
