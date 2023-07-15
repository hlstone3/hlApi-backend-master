package com.stone.hlapi.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stone.hlapicommon.model.entity.UserInterfaceInfo;

import java.util.List;

/**
* @author hongs
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Mapper
* @createDate 2023-06-07 09:41:44
* @Entity com.stone.hlapi.model.entity.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);
}




