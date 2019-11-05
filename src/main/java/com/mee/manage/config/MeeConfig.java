package com.mee.manage.config;

import com.mee.manage.util.DateUtil;
import com.mee.manage.util.MD5Util;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;

public class MeeConfig {

    public static String getMeeSign(String bizId,Long time,String token,String nonce){
        String md5ign = MD5Util.MD5Encode(bizId+time+nonce+token,MD5Util.UTF8,false);
        String sign = null;
        try {
            sign = URLEncoder.encode(md5ign,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return sign;
    }

    /**
     * 将map转换成url
     * @param map
     * @return
     */
    public static String getUrlParamsByMap(Map<String, Object> map) {
        if (map == null) {
            return "";
        }

        Object[] keys = map.keySet().toArray();
        Arrays.sort(keys);

        StringBuffer sb = new StringBuffer();
        for (int i = 0;   i < keys.length;   i++)   {
            sb.append(keys[i] + "=" + map.get(keys[i]));
            sb.append("&");
        }

        String s = sb.toString();
        if (s.endsWith("&")) {
            s = StringUtils.substringBeforeLast(s, "&");
        }
        return s;

    }

    /**
     * 将map转换成url
     * @param map
     * @return
     */
    public static String getSortUrlParamsByMap(Map<String, Object> map) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = StringUtils.substringBeforeLast(s, "&");
        }
        return s;

    }


        public static void main(String[] args) {
        String bidId = "20";

        Long time = DateUtil.getCurrentTime();

        String token = "g4rhAz32KXx6FsSbI9IKIxsygqaAyDhl";

        String nonce = "3Q4gD2kz";

        String sign = getMeeSign(bidId,time,token,nonce);

        String url = bidId+"/"+time+"/"+nonce+"/"+sign;
        System.out.println(url);


    }

}
