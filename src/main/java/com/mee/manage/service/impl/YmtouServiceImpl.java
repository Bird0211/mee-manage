package com.mee.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.mee.manage.config.MeeConfig;
import com.mee.manage.config.YmtouConfig;
import com.mee.manage.service.IYmtouService;
import com.mee.manage.util.*;
import com.mee.manage.vo.*;
import jodd.util.RandomString;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class YmtouServiceImpl implements IYmtouService {

    private static final Logger logger = LoggerFactory.getLogger(IYmtouService.class);


    @Autowired
    YmtouConfig ymtouConfig;

    @Override
    public YmtouOrderVo getOrderList(YmtouOrderListParam param) {
        if(param == null)
            return null;

        String orderStatus = param.getOrderStatus();
        Date startDate = param.getStartDate();
        Date endDate = param.getEndDate();

        Map<String,Object> params = new HashMap<>();
        params.put("order_status",orderStatus);
        params.put("date_type",1);
        params.put("sort_type",1);
        params.put("start_date",startDate);
        params.put("end_date",endDate);
        params.put("page_no",param.getPageNo());
        params.put("page_rows",param.getPageRows());
        params.put("needs_delivery_info",true);

        String method = "ymatou.order.list.get";
        String result = getYmtResult(params,method);
        YmtouOrderVo orderVo = null;
        if(result != null) {
            YmtOrderLsitResult ymtResult = JSON.parseObject(result,YmtOrderLsitResult.class, Feature.IgnoreNotMatch);
            orderVo = ymtResult.getContent();
        }

        return orderVo;
    }

    @Override
    public List<YmtouProduct> getProductList() {
        Integer page_no	= 1;
        Integer	page_rows = 50;
        int pagesize = 0;

        List<YmtouProduct> products = new ArrayList<>();

        do {
            List<YmtouProduct> product = getYmtouProduct(page_no++, page_rows);
            pagesize = product.size();
            products.addAll(product);
        }while (pagesize >= page_rows);

        return products;
    }

    @Override
    public List<YmtGoodInfo> getProdudcts() {
        List<YmtouProduct> products = getProductList();
        if(products == null || products.isEmpty())
            return null;

        List<YmtGoodInfo> goodInfos = new ArrayList<>();
        for (YmtouProduct product : products) {
            List<YmtouSkuInfo> skus = product.getSkus();
            for (YmtouSkuInfo sku : skus) {

                YmtGoodInfo goodInfo = new YmtGoodInfo();

                goodInfo.setBrandName(product.getBrand_name());
                goodInfo.setCategoryId(product.getCategory_id());
                goodInfo.setCategoryName(product.getCategory_name());

                goodInfo.setNewPrice(sku.getNew_price());
                goodInfo.setPrice(sku.getPrice());
                goodInfo.setProductId(product.getProduct_id());
                goodInfo.setProductImage(product.getProduct_images()[0]);
                goodInfo.setProductName(product.getProduct_name());

                goodInfo.setSku(sku.getOuter_id());
                goodInfo.setSkuId(sku.getSku_id());
                goodInfo.setStockNum(sku.getStock_num());

                goodInfo.setVipPrice(sku.getVip_price());
                goodInfo.setWeight(sku.getWeight());

                goodInfos.add(goodInfo);
            }
        }

        return goodInfos;
    }

    private List<YmtouProduct> getYmtouProduct(int pageNo,int pageRows) {
        Map<String,Object> params = new HashMap<>();
        params.put("productid_list",null);
        params.put("page_no",pageNo);
        params.put("page_rows",pageRows);
        params.put("start_updated",null);
        params.put("end_updated",null);
        String method = "ymatou.products.list.get";
        String result = getYmtResult(params,method);

        List<YmtouProduct> product_infos = null;
        if(result != null) {
            YmtProductListResult ymtResult = JSON.parseObject(result,YmtProductListResult.class, Feature.IgnoreNotMatch);
            YmtProductContent ymtProduct = ymtResult.getContent();
            if(ymtProduct != null) {
                product_infos = ymtProduct.getProduct_infos();
            }
        }
        return product_infos;
    }

    private String getParams(Map<String,Object> param, String timestamp,String nonceStr,String method){
        Map<String,Object> params = new HashMap<>();
        params.put("sign_method","MD5");
        params.put("auth_code",ymtouConfig.getAuthCode());
        params.put("timestamp", timestamp);
        params.put("nonce_str", nonceStr);
        params.put("biz_content",JSON.toJSONString(param));
        params.put("sign",getSign(JSON.toJSONString(param),method,timestamp,nonceStr));

        return JSON.toJSONString(params);
    }

    private String getSign(String bizContent,String method,String timestamp,String nonce_str){

        Map<String,Object> signParam = new HashMap<>();

        signParam.put("app_id",ymtouConfig.getAppId());
        signParam.put("method",method);
        signParam.put("sign_method","MD5");
        signParam.put("timestamp",timestamp);
        signParam.put("nonce_str",nonce_str);
        signParam.put("auth_code",ymtouConfig.getAuthCode());
        signParam.put("biz_content", bizContent);

        String stringA = MeeConfig.getUrlParamsByMap(signParam);
        String stringSignTemp = stringA+"&app_secret=" + ymtouConfig.getAppSecret();
        String sign = DigestUtils.md5Hex(stringSignTemp).toUpperCase();

        return sign;

    }

    private String getYmtResult(Map<String,Object> params,String method){

        String timestamp = DateUtil.dateToStringFormat(new Date(), DateUtil.formatPattern_24Full,"Asia/Shanghai");
        String nonceStr = RandomString.get().randomAlpha(32);

        String p = getParams(params,timestamp,nonceStr,method);
        String url = ymtouConfig.getUrl()+"?app_id="+ymtouConfig.getAppId()+"&method="+method;
        logger.info("params : {}",p);
        String result = JoddHttpUtils.sendPostUseBody(url,p);
        logger.info("Result : {}",result);

        return result;
    }


}
