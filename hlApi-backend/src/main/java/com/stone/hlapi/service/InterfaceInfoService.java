package com.stone.hlapi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.hlapi.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.stone.hlapicommon.model.entity.InterfaceInfo;


/**
* @author hongs
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-06-01 23:28:14
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

    QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest);

    long addInterfaceInfo(InterfaceInfo interfaceInfo);
}
