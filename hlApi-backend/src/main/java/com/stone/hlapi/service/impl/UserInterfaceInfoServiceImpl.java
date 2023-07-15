package com.stone.hlapi.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stone.hlapi.service.UserInterfaceInfoService;
import com.stone.hlapi.mapper.UserInterfaceInfoMapper;
import com.stone.hlapicommon.model.entity.UserInterfaceInfo;
import org.springframework.stereotype.Service;

/**
* @author hongs
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
* @createDate 2023-06-07 09:41:44
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService{

}




