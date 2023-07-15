package com.hlstone.hlstoneclientsdk.client;


import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.hlstone.hlstoneclientsdk.utils.SignUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户端使用hutool进行调用后端接口
 */
public class HlApiClient {

    private static final String GATEWAY_HOST = "http://localhost:8090";

    //标识
    private String accessKey;
    //密钥
    private String secretKey;

    public HlApiClient() {
    }

    public HlApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

//    /**
//     * 获取随机图片
//     * @param method 输出壁纸端[mobile|pc|zsy]默认为pc
//     * @param lx 	选择输出分类[meizi|dongman|fengjing|suiji]，为空随机输出
//     * @param format 输出壁纸格式[json|images]默认为json
//     */
//    public String getPicture(String method, String lx, String format) {
//
//        if (CharSequenceUtil.isBlank(format)) {
//            format = "json";
//        }
//        HashMap<String, Object> paramMap = new HashMap<>();
//        paramMap.put("method", method);
//        paramMap.put("lx", lx);
//        paramMap.put("format", format);
//        HttpResponse execute = HttpRequest.get(GATEWAY_HOST + "/api/getPicture/")
//                .addHeaders(getHeader(""))
//                .form(paramMap)
//                .timeout(20000)
//                .execute();
//        return execute.body();
//    }
//
//
//    /**
//     * 获取随机毒鸡汤
//     * @param charset 返回编码类型[gbk|utf-8]默认utf-8
//     * @param encode 返回格式类型[text|js|json]默认json
//     */
//    public String getChickenSoup(String charset,String encode) {
//        if (CharSequenceUtil.isBlank(encode)) {
//            encode = "json";
//        }
//        HashMap<String, Object> paramMap = new HashMap<>();
//        paramMap.put("charset", charset);
//        paramMap.put("encode", encode);
//        HttpResponse execute = HttpRequest.get(GATEWAY_HOST + "/api/getChickenSoup")
//                .addHeaders(getHeader(""))
//                .form(paramMap)
//                .timeout(20000)
//                .execute();
//        return execute.body();
//    }
//
//    /**
//     * 自动识别翻译
//     * @param text 文本需要翻译的内容
//     */
//    public String langTranslator(String text) {
//
//        HashMap<String, Object> paramMap = new HashMap<>();
//        paramMap.put("text", text);
//        HttpResponse execute = HttpRequest.post(GATEWAY_HOST + "/api/tst/")
//                .addHeaders(getHeader(""))
//                .form(paramMap)
//                .timeout(20000)
//                .execute();
//        return execute.body();
//    }

    /**
     * 通用调用接口类
     * @param parameters 请求参数
     * @param url 请求url
     */
    public String onlineInvoke(String parameters,String url) {
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + url)
                .addHeaders(getHeader(parameters))
                .body(parameters)
                .execute();
        System.out.println(httpResponse.getStatus());
        String result = httpResponse.body();
        return result;
    }

    /**
     * 获取请求头
     */
    private Map<String, String> getHeader(String parameters) {
        if (parameters == null) {
            parameters = "stone";
        }
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("parameters",parameters);
        hashMap.put("accessKey", accessKey);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        hashMap.put("nonce", RandomUtil.randomNumbers(5));
        hashMap.put("sign", SignUtils.getSign(hashMap, secretKey));
        try {
            hashMap.put("parameters", URLEncoder.encode(parameters,"utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return hashMap;
    }
}
