package com.hlstone.hlstoneclientsdk.utils;

import cn.hutool.core.util.RandomUtil;

import java.util.HashMap;

public class NonceUtils {

    /**
     * 生成随机nonce，并将已使用的nonce存储到redis缓存中
     * @return nonce
     */
    public String generateNonce(){
        HashMap<String, Long> userNonce = new HashMap<>();
        String nonce = RandomUtil.randomNumbers(5);
        //记录nonce和时间戳
        userNonce.put(nonce,System.currentTimeMillis());

        return nonce;
    }



}
