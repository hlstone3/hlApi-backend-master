package com.stone.hlapi.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.MD5;

public class GenerateKeyUtils {

    /**
     * 生成用户标识
     * @param userAccount 用户账号
     * @return 标识
     */
    public static String GenerateAccessKey(String userAccount) {
        return MD5.create().digestHex(userAccount + RandomUtil.randomNumbers(5));
    }

    /**
     * 生成用户密钥
     * @param userAccount 用户账号
     * @return 密钥
     */
    public static String GenerateSecretKey(String userAccount) {
        return MD5.create().digestHex(userAccount + RandomUtil.randomNumbers(5) + String.valueOf(System.currentTimeMillis()));
    }


}
