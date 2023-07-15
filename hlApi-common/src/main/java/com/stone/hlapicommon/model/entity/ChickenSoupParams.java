package com.stone.hlapicommon.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author weiwei
 * @Date 2023/6/18 14:24
 * @Version 1.0
 */
@Data
public class ChickenSoupParams implements Serializable {
    private String charset;
    private String encode;

    public ChickenSoupParams() {
    }

    public ChickenSoupParams(String charset, String encode) {
        this.charset = charset;
        this.encode = encode;
    }
}
