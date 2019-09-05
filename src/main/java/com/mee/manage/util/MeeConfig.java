package com.mee.manage.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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

}
