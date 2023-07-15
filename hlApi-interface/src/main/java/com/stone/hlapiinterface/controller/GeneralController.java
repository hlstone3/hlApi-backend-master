package com.stone.hlapiinterface.controller;

import cn.hutool.http.HttpUtil;
import com.google.gson.Gson;
import com.stone.hlapiinterface.model.entity.AcgParams;
import com.stone.hlapiinterface.model.entity.ChickenSoupParams;

import com.stone.hlapiinterface.model.entity.PictureParams;
import com.stone.hlapiinterface.model.entity.TranslatorParams;
import com.stone.hlapiinterface.model.vo.AcgPictureVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * API通用接口
 */
@RestController
public class GeneralController {

    private static final String Picture_URL = "https://api.btstu.cn/sjbz/api.php";
    private static final String ChickenSoup_URL = "https://api.btstu.cn/yan/api.php";
    private static final String LangTranslator_URL = "https://api.btstu.cn/tst/api.php";
    private static final String ACG_URL = "https://www.loliapi.com/acg/pc/";
    private static final Gson gson = new Gson();


    @PostMapping("/getPicture")
    public String getPicture(@RequestBody(required = false) PictureParams pictureParams) {
        String result = "";
        if (pictureParams == null) {
            pictureParams = new PictureParams();
        }
        HashMap<String, Object> paramMap = new HashMap<>();
        if (pictureParams.getFormat() == null || "".equals(pictureParams.getFormat())) {
            paramMap.put("format", "json");
        } else {
            paramMap.put("format", pictureParams.getFormat());
        }
        if (pictureParams.getLx() == null || "".equals(pictureParams.getLx())) {
            paramMap.put("lx", "");
        } else {
            paramMap.put("lx", pictureParams.getLx());
        }
        if (pictureParams.getMethod() == null || "".equals(pictureParams.getMethod())) {
            paramMap.put("method", "pc");
        } else {
            paramMap.put("method", pictureParams.getMethod());
        }
        if (paramMap.get("format").equals("images")) {
            try {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(Picture_URL);
                stringBuilder.append("?");
                stringBuilder.append("lx=").append(paramMap.get("lx")).append("&");
                stringBuilder.append("method=").append(paramMap.get("method")).append("&");
                stringBuilder.append("format=").append(paramMap.get("format"));
                result = getRedirectUrl(stringBuilder.toString());
                return result;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        result = HttpUtil.get(Picture_URL, paramMap);
        return result;
    }


    @PostMapping("/acg/picture")
    public String AcgPicture(@RequestBody(required = false) AcgParams acgParams) {
        String result = "";
        if (acgParams == null) {
            acgParams = new AcgParams();
        }
        HashMap<String, Object> paramMap = new HashMap<>();

        if (acgParams.getType() != null && "url".equals(acgParams.getType())) {
            paramMap.put("type", "url");
            String url = HttpUtil.get(ACG_URL, paramMap);
            AcgPictureVO acgPictureVO = new AcgPictureVO();
            acgPictureVO.setUrl(url);
            String json = gson.toJson(acgPictureVO);
            return json;
        }
        try {
            result = getRedirectUrl(ACG_URL + "?type=&id=");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }


    @PostMapping("/getChickenSoup")
    public String getChickenSoup(@RequestBody(required = false) ChickenSoupParams chickenSoupParams) {
        if (chickenSoupParams == null) {
            chickenSoupParams = new ChickenSoupParams();
        }
        HashMap<String, Object> paramMap = new HashMap<>();
        if (chickenSoupParams.getCharset() == null || "".equals(chickenSoupParams.getCharset())) {
            paramMap.put("charset", "utf-8");
        } else {
            paramMap.put("charset", chickenSoupParams.getCharset());
        }
        if (chickenSoupParams.getEncode() == null || "".equals(chickenSoupParams.getEncode())) {
            paramMap.put("encode", "json");
        } else {
            paramMap.put("encode", chickenSoupParams.getEncode());
        }
        // 拿到调用API的URL
        String result = HttpUtil.get(ChickenSoup_URL, paramMap);
        return result;
    }

    @PostMapping("/translate")
    public String TranslatorController(@RequestBody TranslatorParams translatorParams) {
        if (translatorParams == null) {
            translatorParams = new TranslatorParams();
        }
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("text", translatorParams.getText());
        String result = HttpUtil.post(LangTranslator_URL, paramMap);
        return result;
    }

    /**
     * 获取重定向地址
     *
     * @param path
     * @return
     * @throws Exception
     */
    private String getRedirectUrl(String path) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(path)
                .openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.setConnectTimeout(5000);
        //得到请求头的所有属性和值
        Map<String, List<String>> map = conn.getHeaderFields();
        Set<String> stringSet = map.keySet();
        for (String str : stringSet) {
            System.out.println(str + "------" + conn.getHeaderField(str));
        }
        String location = conn.getHeaderField("Location");
        conn.disconnect();
        return location;
    }

}
