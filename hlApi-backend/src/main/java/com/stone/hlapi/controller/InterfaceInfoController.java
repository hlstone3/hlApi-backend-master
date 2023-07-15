package com.stone.hlapi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlstone.hlstoneclientsdk.client.HlApiClient;
import com.stone.hlapi.annotation.AuthCheck;
import com.stone.hlapi.common.BaseResponse;
import com.stone.hlapi.common.DeleteRequest;
import com.stone.hlapi.common.ErrorCode;
import com.stone.hlapi.common.ResultUtils;
import com.stone.hlapi.constant.CommonConstant;
import com.stone.hlapi.constant.UserConstant;
import com.stone.hlapi.exception.BusinessException;
import com.stone.hlapi.exception.ThrowUtils;
import com.stone.hlapi.model.dto.interfaceInfo.*;
import com.stone.hlapi.service.InterfaceInfoService;
import com.stone.hlapi.service.UserInterfaceInfoService;
import com.stone.hlapi.service.UserService;
import com.stone.hlapicommon.model.entity.InterfaceInfo;
import com.stone.hlapicommon.model.entity.User;
import com.stone.hlapicommon.model.entity.UserInterfaceInfo;
import com.stone.hlapicommon.model.enums.InterfaceInfoStatusEnum;
import com.stone.hlapicommon.model.vo.SelfInterfaceDateVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.stone.hlapi.service.impl.UserServiceImpl.SALT;

/**
 * 接口信息
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;


    /**
     * 创建
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        //数据保存
        long l = interfaceInfoService.addInterfaceInfo(interfaceInfo);
        return ResultUtils.success(l);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param interfaceInfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceInfoVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(interfaceInfo);
    }

    /**
     * 分页获取列表
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        String name = interfaceInfoQueryRequest.getName();
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfoQuery.getDescription();
        // content 需支持模糊搜索
        interfaceInfoQuery.setDescription(null);
        interfaceInfoQuery.setName(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(interfaceInfoPage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<InterfaceInfo>> listMyInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest,
                                                                         HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        interfaceInfoQueryRequest.setUserId(loginUser.getId());
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
                interfaceInfoService.getQueryWrapper(interfaceInfoQueryRequest));
        return ResultUtils.success(interfaceInfoPage);
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param interfaceInfoEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editInterfaceInfo(@RequestBody InterfaceInfoEditRequest interfaceInfoEditRequest, HttpServletRequest request) {
        if (interfaceInfoEditRequest == null || interfaceInfoEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoEditRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        User loginUser = userService.getLoginUser(request);
        long id = interfaceInfoEditRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldInterfaceInfo.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    @PostMapping("/online")
    // 管理员验证
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, HttpServletRequest request) {
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = interfaceInfoInvokeRequest.getId();
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        // 判断接口是否存在
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        HlApiClient hlApiClient = new HlApiClient(accessKey, secretKey);
        // 调用接口
        String url = interfaceInfo.getUrl();
        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();
        String json = hlApiClient.onlineInvoke(userRequestParams, url);
        // 接口返回的数据
        if (StringUtils.isBlank(json)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        // 修改接口数据库中的状态字段为1(仅管理员可操作)
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    @PostMapping("/offline")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody InterfaceInfoIdRequest interfaceInfoIdRequest, HttpServletRequest request) {

        if (interfaceInfoIdRequest == null || interfaceInfoIdRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = interfaceInfoIdRequest.getId();
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        // 判断接口是否存在
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 修改接口数据库中的状态字段为0(仅管理员可操作)
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    @PostMapping("/invoke")
    public BaseResponse<String> invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, HttpServletRequest request) {
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long interfaceInfoId = interfaceInfoInvokeRequest.getId();
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceInfoId);
        // 判断接口是否存在
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        checkInvokeCount(interfaceInfoId, userId);
        // 判断接口是否可调用
        if (interfaceInfo.getStatus() != InterfaceInfoStatusEnum.ONLINE.getValue()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口已关闭");
        }
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        HlApiClient hlApiClient = new HlApiClient(accessKey, secretKey);
        //  根据测试地址来调用
        String url = interfaceInfo.getUrl();
        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();
        String result = hlApiClient.onlineInvoke(userRequestParams, url);
        return ResultUtils.success(result);
    }


    @GetMapping("/selfInterfaceData")
    public BaseResponse<List<SelfInterfaceDateVo>> selfInterfaceData(HttpServletRequest request) {
        User currentUser = userService.getLoginUser(request);
        Long id = currentUser.getId();
        LambdaQueryWrapper<UserInterfaceInfo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserInterfaceInfo::getUserId, id);
        List<UserInterfaceInfo> list = userInterfaceInfoService.list(lqw);
        List<SelfInterfaceDateVo> selfInterfaceDateVos = new ArrayList<>();
        for (UserInterfaceInfo userInterfaceInfo : list) {
            SelfInterfaceDateVo selfInterfaceDateVo = new SelfInterfaceDateVo();
            BeanUtils.copyProperties(userInterfaceInfo, selfInterfaceDateVo);
            Long interfaceInfoId = userInterfaceInfo.getInterfaceInfoId();
            LambdaQueryWrapper<InterfaceInfo> lqw1 = new LambdaQueryWrapper<>();
            lqw1.eq(InterfaceInfo::getId, interfaceInfoId);
            InterfaceInfo one = interfaceInfoService.getOne(lqw1);
            String name = one.getName();
            selfInterfaceDateVo.setInterfaceName(name);
            selfInterfaceDateVos.add(selfInterfaceDateVo);
        }
        return ResultUtils.success(selfInterfaceDateVos);
    }

    @GetMapping("/interfaceNameList")
    public BaseResponse<Map<String, String>> interfaceNameList() {
        HashMap<String, String> map = new HashMap<>();
        for (InterfaceInfo interfaceInfo : interfaceInfoService.list()) {
            map.put(interfaceInfo.getName(), interfaceInfo.getName());
        }
        return ResultUtils.success(map);
    }

    @PostMapping("/payInterface")
    @Transactional
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> payInterface(InterfaceInfoPayRequest interfaceInfoPayRequest, HttpServletRequest request) {
        int num = interfaceInfoPayRequest.getNum();
        if (num <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "请输入正确充值次数");
        }
        // 检验管理员密码是否正确
        String adminPsd = interfaceInfoPayRequest.getAdminPsd();
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + adminPsd).getBytes());
        User loginUser = userService.getLoginUser(request);
        String userPassword = loginUser.getUserPassword();
        if (!encryptPassword.equals(userPassword)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "管理员密码错误");
        }
        String userAccount = interfaceInfoPayRequest.getPayAccount();
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUserAccount, userAccount);
        User user = userService.getOne(userLambdaQueryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户不存在");
        }
        Long userId = user.getId();

        String interfaceName = interfaceInfoPayRequest.getInterfaceName();
        LambdaQueryWrapper<InterfaceInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(InterfaceInfo::getName, interfaceName);
        InterfaceInfo interfaceInfo = interfaceInfoService.getOne(lambdaQueryWrapper);
        Long interfaceInfoId = interfaceInfo.getId();

        LambdaQueryWrapper<UserInterfaceInfo> userInterfaceInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userInterfaceInfoLambdaQueryWrapper.eq(UserInterfaceInfo::getUserId, userId);
        userInterfaceInfoLambdaQueryWrapper.eq(UserInterfaceInfo::getInterfaceInfoId, interfaceInfoId);
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(userInterfaceInfoLambdaQueryWrapper);
        if (userInterfaceInfo == null) {
            userInterfaceInfo = new UserInterfaceInfo();
            userInterfaceInfo.setInterfaceInfoId(interfaceInfoId);
            userInterfaceInfo.setUserId(userId);
            userInterfaceInfo.setLeftNum(num);
            userInterfaceInfoService.save(userInterfaceInfo);
        } else {
            Integer leftNum = userInterfaceInfo.getLeftNum();
            userInterfaceInfo.setLeftNum(leftNum + num);
            userInterfaceInfoService.saveOrUpdate(userInterfaceInfo);
        }
        return ResultUtils.success(true);
    }

    private void checkInvokeCount(Long interfaceInfoId, Long userId) {
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interfaceInfoId", interfaceInfoId);
        queryWrapper.eq("userId", userId);
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(queryWrapper);
        if (userInterfaceInfo == null) {
            userInterfaceInfo = new UserInterfaceInfo();
            userInterfaceInfo.setInterfaceInfoId(interfaceInfoId);
            userInterfaceInfo.setUserId(userId);
            userInterfaceInfo.setLeftNum(10);
            userInterfaceInfo.setTotalNum(0);
            userInterfaceInfoService.save(userInterfaceInfo);
        }
        if (userInterfaceInfo.getLeftNum() <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用次数已用完，请充值。");
        }

    }
}
