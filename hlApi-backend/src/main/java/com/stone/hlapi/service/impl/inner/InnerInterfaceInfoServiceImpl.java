package com.stone.hlapi.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stone.hlapi.common.ErrorCode;
import com.stone.hlapi.exception.BusinessException;
import com.stone.hlapi.service.impl.InterfaceInfoServiceImpl;
import com.stone.hlapicommon.model.entity.InterfaceInfo;
import com.stone.hlapicommon.service.InnerInterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerInterfaceInfoServiceImpl  implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoServiceImpl interfaceInfoService;
    @Override
    public InterfaceInfo getInterfaceInfo(String url, String method) {
        if (StringUtils.isAnyBlank(url, method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url", url);
        queryWrapper.eq("method", method);
        return interfaceInfoService.getOne(queryWrapper);

    }
}