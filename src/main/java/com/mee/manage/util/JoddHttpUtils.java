
/**
 * @Title: JoddHttpUtils.java
 * @Package: com.hynet.ssn.server.util
 * @Description: 
 * @author: zhangcan
 * @date: 2016年6月15日 下午5:15:37
 * @version: V1.0
 */
package com.mee.manage.util;

import com.alibaba.fastjson.JSON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

import java.util.Map;

/**
 * @moudle: JoddHttpUtils
 * @version:v1.0
 * @Description: 
 * @author: zhangcan
 * @date: 2016年6月15日 下午5:15:37
 *
 */
public class JoddHttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(JoddHttpUtils.class);


    /** 默认的连接时间*/
    private static int DEFAULT_CONNECT_TIME_OUT = 40000;
    /** 默认的读取时间*/
    private static int DEFAULT_READ_TIME_OUT = 40000;
    /** 默认的请求编码格式*/
    private static String DEFAULT_ENCODING_UTF_8 = "utf-8";

    private static String DEFAULT_APPLICATION_JSON = "application/json";
    
    public static String FORM_URLENCODED = "application/x-www-form-urlencoded" ;

    /**
     * 发送POST请求<br>
     * <p>Title: sendPost</p>
     * <p>author : zhangcan</p>
     * <p>date : 2016年6月15日 下午5:27:47</p>
     * 
     * @param url String 请求地址<br>
     * @param params Map<String, Object> 表单数据<br/>
     * @return String 响应内容<br/>
     */
    public static String sendPost(String url, Map<String, Object> params) {
        return sendPost(url, params, DEFAULT_CONNECT_TIME_OUT,
            DEFAULT_READ_TIME_OUT, DEFAULT_ENCODING_UTF_8);
    }

    /**
     * 发送POST请求<br>
     * <p>Title: sendPost</p>
     * <p>author : zhangcan</p>
     * <p>date : 2016年6月15日 下午5:27:47</p>
     * 
     * @param url String 请求地址<br>
     * @param params Map<String, Object> 表单数据<br/>
     * @param connectTimeOut int 连接超时时间(毫秒)<br/>
     * @param readTimeOut int 读取超时时间(毫秒)<br/>
     * @param encoding String 请求编码格式<br/>
     * @return String 响应内容<br/>
     */
    public static String sendPost(String url, Map<String, Object> params,
        int connectTimeOut, int readTimeOut, String encoding) {

        long startTime = System.currentTimeMillis();

        HttpRequest httpRequest = HttpRequest.post(url)
                .connectionTimeout(connectTimeOut).timeout(readTimeOut)
                .formEncoding(encoding);
        if(params != null)
            httpRequest = httpRequest.form(params);

        HttpResponse httpResponse = httpRequest.send();
        String result = httpResponse.accept(encoding).bodyText();
        // 打印出参
        logger.info("sendPost Args  : {}", JSON.toJSON(result));
        // 执行耗时
        logger.info("Time-Consuming : {} ms", System.currentTimeMillis() - startTime);
        return result;
    }


    public static String sendPost(String url, Map<String, Object> params,
                                  int connectTimeOut, int readTimeOut, String encoding,String contentType) {

        long startTime = System.currentTimeMillis();
        HttpResponse httpResponse = HttpRequest.post(url)
                .connectionTimeout(connectTimeOut).timeout(readTimeOut)
                .formEncoding(encoding).form(params).contentType(contentType).send();        
        String result = httpResponse.accept(encoding).bodyText();
        // 打印出参
        logger.info("sendPost Args  : {}", JSON.toJSON(result));
        // 执行耗时
        logger.info("Time-Consuming : {} ms", System.currentTimeMillis() - startTime);
        return result;
    }


    /**
     * <p>Title: sendPostUseBody</p>
     * <p>author : zhangcan</p>
     * <p>date : 2016年6月16日 下午3:07:09</p>
     *
     * @param url String 请求地址<br/>
     * @param jsonBody Ojbect json格式的数据<br/>
     * @return String 响应内容<br/>
     */
    public static String sendPostUseBody(String url, Object jsonBody) {

        return sendPostUseBody(url, JSON.toJSONString(jsonBody), DEFAULT_CONNECT_TIME_OUT,
                DEFAULT_READ_TIME_OUT, DEFAULT_ENCODING_UTF_8);
    }

    /**
     * <p>Title: sendPostUseBody</p>
     * <p>author : zhangcan</p>
     * <p>date : 2016年6月16日 下午3:07:09</p>
     * 
     * @param url String 请求地址<br/>
     * @param jsonParamBody String json格式的数据<br/>
     * @return String 响应内容<br/>
     */
    public static String sendPostUseBody(String url, String jsonParamBody) {
        return sendPostUseBody(url, jsonParamBody, DEFAULT_CONNECT_TIME_OUT,
            DEFAULT_READ_TIME_OUT, DEFAULT_ENCODING_UTF_8);
    }
    
    /**
     * <p>Title: sendPostUseBody</p>
     * <p>author : zhangcan</p>
     * <p>date : 2016年6月16日 下午3:07:09</p>
     * 
     * @param url String 请求地址<br/>
     * @param jsonParamBody String json格式的数据<br/>
     * @param connectTimeOut int 连接超时时间(毫秒)<br/>
     * @param readTimeOut int 读取超时时间(毫秒)<br/>
     * @param encoding String 编码格式<br/>
     * @return String 响应内容<br/>
     */
    public static String sendPostUseBody(String url, String jsonParamBody,
        int connectTimeOut, int readTimeOut, String encoding) {
        
        long startTime = System.currentTimeMillis();
        HttpResponse httpResponse =
            HttpRequest.post(url).connectionTimeout(connectTimeOut)
                .timeout(readTimeOut).formEncoding(encoding)
                .bodyText(jsonParamBody, "application/json;", encoding).send();
        String result = httpResponse.accept(encoding).bodyText();
        // 打印出参
        logger.info("sendPostUseBody Args  : {}", JSON.toJSON(result));
        // 执行耗时
        logger.info("Time-Consuming : {} ms", System.currentTimeMillis() - startTime);
        return result;
    }

     /**
     * <p>Title: sendPostUseBody</p>
     * <p>author : zhangcan</p>
     * <p>date : 2016年6月16日 下午3:07:09</p>
     * 
     * @param url String 请求地址<br/>
     * @param jsonParamBody String json格式的数据<br/>
     * @param connectTimeOut int 连接超时时间(毫秒)<br/>
     * @param readTimeOut int 读取超时时间(毫秒)<br/>
     * @param encoding String 编码格式<br/>
     * @return String 响应内容<br/>
     */
    public static String sendPostUseBody(String url, String jsonParamBody,String token) {
        long startTime = System.currentTimeMillis();

        HttpResponse httpResponse =
            HttpRequest.post(url).connectionTimeout(DEFAULT_CONNECT_TIME_OUT)
                .timeout(DEFAULT_READ_TIME_OUT).formEncoding(DEFAULT_ENCODING_UTF_8).tokenAuthentication(token)
                .bodyText(jsonParamBody, "application/json;", DEFAULT_ENCODING_UTF_8).send();

        String result = httpResponse.accept(DEFAULT_ENCODING_UTF_8).bodyText();
        // 打印出参
        logger.info("sendPostUseBody Args  : {}", JSON.toJSON(result));
        // 执行耗时
        logger.info("Time-Consuming : {} ms", System.currentTimeMillis() - startTime);
        return result;
    }


    /**
     * 
     * 使用Jodd发送Get请求<br/>
     * <p>Title: getData</p>
     * <p>author : zhangcan</p>
     * <p>date : 2016年6月15日 下午5:31:08</p>
     * 
     * @param url String 请求地址<br/>
     * @param params Map<String,String> 请求参数<br/>
     * @return String 响应内容<br/>
     */
    public static String getData(String url, Map<String, String> params) {
        return getData(url, params, DEFAULT_CONNECT_TIME_OUT,
            DEFAULT_READ_TIME_OUT, DEFAULT_ENCODING_UTF_8);
    }

    public static String getData(String url) {
        return getData(url, DEFAULT_CONNECT_TIME_OUT,
                DEFAULT_READ_TIME_OUT, DEFAULT_ENCODING_UTF_8);
    }

    /**
     * 
     * 使用Jodd发送Get请求<br/>
     * <p>Title: getData</p>
     * <p>author : zhangcan</p>
     * <p>date : 2016年6月15日 下午5:31:08</p>
     * 
     * @param url String 请求地址<br/>
     * @param params Map<String, String> 请求参数<br/>
     * @param connectTimeOut int 连接超时时间<br/>
     * @param readReadTimeOut 读取超时时间<br/>
     * @param encoding String 请求编码格式<br/>
     * @return String 响应内容<br/>
     */
    public static String getData(String url, Map<String, String> params,
        int connectTimeOut, int readReadTimeOut, String encoding) {
        long startTime = System.currentTimeMillis();
        
        HttpResponse httpResponse =
            HttpRequest.get(url).connectionTimeout(connectTimeOut)
                .timeout(readReadTimeOut).accept(DEFAULT_APPLICATION_JSON).query(params).send();
        
        String result = httpResponse.accept(DEFAULT_APPLICATION_JSON).bodyText();
        // 打印出参
        logger.info("getData Args  : {}", JSON.toJSON(result));
        // 执行耗时
        logger.info("Time-Consuming : {} ms", System.currentTimeMillis() - startTime);
        return result; 
    }

    /**
     *
     * 使用Jodd发送Get请求<br/>
     * <p>Title: getData</p>
     * <p>author : zhangcan</p>
     * <p>date : 2016年6月15日 下午5:31:08</p>
     *
     * @param url String 请求地址<br/>
     * @param connectTimeOut int 连接超时时间<br/>
     * @param readReadTimeOut 读取超时时间<br/>
     * @param encoding String 请求编码格式<br/>
     * @return String 响应内容<br/>
     */
    public static String getData(String url,
                                 int connectTimeOut, int readReadTimeOut, String encoding) {
        long startTime = System.currentTimeMillis();

        HttpResponse httpResponse =
                HttpRequest.get(url).connectionTimeout(connectTimeOut)
                        .timeout(readReadTimeOut).accept(DEFAULT_APPLICATION_JSON).send();

        String result = httpResponse.accept(DEFAULT_APPLICATION_JSON).bodyText();
        // 打印出参
        logger.info("getData Args  : {}", JSON.toJSON(result));
        // 执行耗时
        logger.info("Time-Consuming : {} ms", System.currentTimeMillis() - startTime);
        return result; 
    }
    
    public static void main(String[] args) {
          /** 使用参数请求*/
/*        String url = "http://localhost:8080/m/mail";
        Map<String, Object> paramMap = new HashMap<String,Object>();
        paramMap.put("html", "您好，测试一下数据");
        paramMap.put("mailTo","zhangcan0327@sina.com;fshuqing@qq.com");
        paramMap.put("subject", "逗你玩！");
        String result = JoddHttpUtils.sendPost(url, paramMap);
        System.out.println("post:" + result);*/
        
        /** 使用JSON格式数据传输，发送Post请求，数据往body里边写入*/
/*        String url = "http://192.168.200.106:8080/zxBadInfo/query.do";
        Gson gson = new Gson();
        String sid = "";
        String md5pwd = "";
        String despwd = "";
        String transno = RandomStringUtils.randomNumeric(20);
        ZxBadInfo blxx = new ZxBadInfo();
        blxx.setDespwd(despwd);
        blxx.setMd5pwd(md5pwd);
        blxx.setCpserialnum(transno);
        blxx.setIdnum("110201198901205230");
        blxx.setName("奥巴马");
        String jsonParamBody = gson.toJson(blxx);
        String result = sendPostUseBody(url, jsonParamBody);
        System.out.println("result:" + result);*/
        
    }
}
