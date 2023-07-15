package com.hlstone.hlstoneclientsdk.utils;

import cn.hutool.crypto.digest.MD5;

import java.util.Map;

public class SignUtils {
    /**
     * 生成签名
     */
    public static String getSign(Map<String, String> map, String secretKey) {
        String parameters = map.get("parameters");
        String timestamp = map.get("timestamp");
        String nonce = map.get("nonce");
        String accessKey = map.get("accessKey");
        return MD5.create().digestHex(parameters + timestamp + nonce + accessKey + secretKey);
    }

    public static String getSign(String parameters, String timestamp, String nonce, String accessKey, String secretKey) {
        return MD5.create().digestHex(parameters + timestamp + nonce + accessKey + secretKey);
    }


}
