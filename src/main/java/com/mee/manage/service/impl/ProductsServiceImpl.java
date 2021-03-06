package com.mee.manage.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mee.manage.config.Config;
import com.mee.manage.config.MeeConfig;
import com.mee.manage.mapper.IProductsMapper;
import com.mee.manage.po.Products;
import com.mee.manage.service.GuavaCache;
import com.mee.manage.service.IAuthenticationService;
import com.mee.manage.service.IDataTopService;
import com.mee.manage.service.IProductsService;
import com.mee.manage.util.DateUtil;
import com.mee.manage.util.JoddHttpUtils;
import com.mee.manage.vo.BathProVos;
import com.mee.manage.vo.ComparePricesVo;
import com.mee.manage.vo.InvoiceProduct;
import com.mee.manage.vo.MeeProductResponse;
import com.mee.manage.vo.MeeProductVo;
import com.mee.manage.vo.MeeSuppliersResponse;
import com.mee.manage.vo.ProVo;
import com.mee.manage.vo.SuppliersVo;
import com.mee.manage.vo.Yiyun.YiyunTopProduct;

import org.apache.commons.lang3.RandomStringUtils;
import org.jdmp.core.sample.DefaultSample;
import org.jdmp.core.sample.Sample;
import org.jdmp.core.variable.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ujmp.core.Matrix;

@Service
public class ProductsServiceImpl extends ServiceImpl<IProductsMapper, Products>
        implements IProductsService {

    private static final Logger logger = LoggerFactory.getLogger(IProductsService.class);


    @Autowired
    Config config;

    @Autowired
    IAuthenticationService authService;

    @Autowired
    GuavaCache guavaCache;

    @Autowired
    IDataTopService dataTopService;


    @Override
    public boolean insertProduct(ProVo proVo) {
        if(proVo == null)
            return false;

        Products products = getProducts(proVo);
        return save(products);
    }

    @Override
    public boolean insertProducts(BathProVos products) {

        List<ProVo> proVos = products.getProVos();
        if(proVos == null || proVos.size() <= 0)
            return false;

        List<Products> ps = new ArrayList<>();
        for(int i = 0; i < 50 && i < proVos.size(); i++){
            ProVo proVo = proVos.get(i);
            Products p = getProducts(proVo);
            ps.add(p);
        }

        return saveBatch(ps);
    }

    @Override
    public boolean updateProductBySku(Products product) {
        UpdateWrapper<Products> updateWrapper = new UpdateWrapper<Products>();
        updateWrapper.set("sku",product.getSku());
        return update(product,updateWrapper);
    }

    @Override
    public Products getProductBySku(Long sku) {
        QueryWrapper<Products> queryWrapper = new QueryWrapper<Products>();
        queryWrapper.eq("sku",sku);
        return getOne(queryWrapper);
    }

    @Override
    public List<Products> getProductsBySkus(List<Long> skus) {
        QueryWrapper<Products> queryWrapper = new QueryWrapper<Products>();
        queryWrapper.in("sku",skus);
        return list(queryWrapper);
    }

    @Override
    public List<MeeProductVo> getProductsBySkus(List<MeeProductVo> products, List<String> skus) {
        if (products == null || skus == null)
            return null;

        Map<String,MeeProductVo> mapProducts = new HashMap<>();
        for (MeeProductVo product : products) {
            mapProducts.put(product.getCode(),product);
        }

        List<MeeProductVo> skuProducts = new ArrayList<>();
        for (String sku : skus) {
            skuProducts.add(mapProducts.get(sku));
        }

        return skuProducts;
    }

    @Override
    public List<Sample> getAllProducts() {
        List<Products> allProducts = list();
        List<Sample> samples = null;
        if(allProducts != null) {
            samples = new ArrayList<>();
            for (Products product : allProducts){
                Sample sample = new DefaultSample();
                sample.put("name",product.getName());
                sample.put("sku",product.getSku());
                sample.setLabel(product.getSku());
                samples.add(sample);
            }
        }
        return samples;
    }

    @Override
    public List<MeeProductVo> getMeeProducts(String bizId) {

        List<MeeProductVo> products = null;
        try {
            products = guavaCache.getValue(bizId);
            // products = getMeeProductsByUrl(bizId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return products;
    }

    public List<MeeProductVo> getMeeProductsByUrl(String bizId) {
        String url = getMeeUrl(config.getAllProductUrl(),bizId);
        logger.info(url);
        String result = JoddHttpUtils.getData(url);
        logger.info(result);
        List<MeeProductVo> productVos = null;
        List<MeeProductResponse> allProduct = JSON.parseArray(result,MeeProductResponse.class);
        if(allProduct != null && allProduct.size() > 0) {
            MeeProductResponse meeProductResponse = allProduct.get(0);
            if(meeProductResponse != null && meeProductResponse.getResult().equals("SUCCESS")) {
                productVos = meeProductResponse.getProducts();
            }
        }

        return productVos;
    }


    @Override
    public List<SuppliersVo> getSuppliers(String bizId) {
        String url = getMeeUrl(config.getAllSupplieUrl(),bizId);
        String result = JoddHttpUtils.getData(url);
        logger.info(result);
        List<SuppliersVo> suppliers = null;
        List<MeeSuppliersResponse> suppliersResponse = null;
        try {
            suppliersResponse = JSON.parseArray(result,MeeSuppliersResponse.class);
        } catch (Exception e) {
           logger.error("Json error", e);
        }
        if( suppliersResponse != null && suppliersResponse.size() > 0) {
            MeeSuppliersResponse response = suppliersResponse.get(0);
            if(response != null && response.getResult().equals("SUCCESS")) {
                suppliers = response.getSuppliers();
            }
        }

        return suppliers;
    }

    @Override
    public List<Sample> getSampleProducts(List<MeeProductVo> meeProductVos) {
        if(meeProductVos == null || meeProductVos.isEmpty()) {
            return null;
        }
        Set<String> setCode = getSetCode(meeProductVos);
        List<Sample> samples = new ArrayList<>();

        for(int i = 0; i < meeProductVos.size(); i++) {
            MeeProductVo meeProduct = meeProductVos.get(i);

            Sample sample = Sample.Factory.emptySample();

            sample.put("name", meeProduct.getName());
            Matrix input = Matrix.Factory.linkToArray(new String[] { meeProduct.getName()}).transpose();
            sample.put(Variable.INPUT, input);

            Matrix output = Matrix.Factory.linkToArray(getOutPut(setCode, meeProduct.getCode())).transpose();
            sample.put(Variable.TARGET, output);
            sample.setLabel(meeProduct.getName());

            sample.setId("mee-"+i);

            samples.add(sample);
        }

        return samples;
    }

    public Set<String> getSetCode(List<MeeProductVo> meeProductVos ) {
        if(meeProductVos == null || meeProductVos.isEmpty()) {
            return null;
        }

        Set<String> setCode = new HashSet<>();
        for (MeeProductVo product : meeProductVos) {
            setCode.add(product.getCode());
        }

        return setCode;
    }

    @Override
    public List<ComparePricesVo> getComparePrice(List<InvoiceProduct> invoiceProducts,String bizId) {
        if (invoiceProducts == null || invoiceProducts.size() <= 0)
            return null;

        Map<String,MeeProductVo> mapProduct = getMapMeeProduct(bizId);
        if(mapProduct == null || mapProduct.isEmpty())
            return null;

        List<ComparePricesVo> result = Lists.newArrayList();
        for (InvoiceProduct invoiceProduct : invoiceProducts){
            String sku = invoiceProduct.getSku();
            BigDecimal newPrice = invoiceProduct.getPrice();
            MeeProductVo meeProduct = mapProduct.get(sku);
            if (meeProduct == null)
                continue;

            BigDecimal costPrice = meeProduct.getCostPrice();

            ComparePricesVo compare = new ComparePricesVo();
            compare.setCostPrice(costPrice == null ? BigDecimal.ZERO : costPrice);
            compare.setNewPrice(newPrice);
            compare.setName(meeProduct.getName());
            compare.setSku(sku);
            result.add(compare);
        }
        return result;
    }

    @Override
    public Map<String, MeeProductVo> getMapMeeProduct(String bizId) {
        List<MeeProductVo> allProducts = getMeeProducts(bizId);
        if(allProducts == null || allProducts.size() <= 0)
            return null;

        Map<String,MeeProductVo> mapProducts = new HashMap<>();
        for(MeeProductVo product : allProducts) {
            mapProducts.put(product.getCode(),product);
        }

        return mapProducts;
    }

    private String getMeeUrl(String url,String bizId){
        Long time = DateUtil.getCurrentTime();
        String token = authService.getMeeToken(bizId);
        if(token == null)
            return null;

        String nonce = RandomStringUtils.random(6,true,false);
        String sign = MeeConfig.getMeeSign(bizId,time,token,nonce);

        url = url + "/" + bizId + "/" + time + "/" + nonce + "/" + sign;
        logger.info(url);
        return url;
    }

    private Products getProducts(ProVo proVo){
        Products products = new Products();
        products.setName(proVo.getName());
        products.setBrand(proVo.getBrand());
        products.setCategoryId(proVo.getCategoryId());
        products.setCostPrice(proVo.getCostPrice());
        products.setOverseaCostPrice(proVo.getOverseaCostPrice());
        products.setOverseaName(proVo.getOverseaName());
        products.setOverseaRetailPrice(proVo.getOverseaRetailPrice());
        products.setRetailPrice(proVo.getRetailPrice());
        products.setSku(proVo.getSku());
        products.setWeight(proVo.getWeight());
        products.setCreateTime(new Date());
        products.setUpdateTime(new Date());
        products.setState(0);
        return products;
    }

    private Object[] getOutPut(Set<String> setCodes, String code) {
        List<Double> output = new ArrayList<>();
        for(String setCode : setCodes) {
            Double d = 0.0;
            if(setCode.equals(code)) {
                d = 1.0;
            }
            output.add(d);
        }
        Double[] out = new Double[output.size()];
        return output.toArray(out);
    }

	@Override
	public Map<String, MeeProductVo> getMeeProductsBySku(String bizId, List<String> skus) {
        if(bizId == null || skus == null)
            return null;
        
        List<MeeProductVo> allProducts = getMeeProducts(bizId);
        if(allProducts == null || allProducts.size() <= 0) {
            return null;
        }
        Map<String,MeeProductVo> mapProducts = new HashMap<>();

        allProducts.stream().filter(item -> skus.indexOf(item.getCode()) >= 0).forEach(item -> mapProducts.put(item.getCode(), item));

        return mapProducts;
	}

	@Override
	public List<YiyunTopProduct> getTopProducts(Long bizId) {

		return dataTopService.getTopProduct(bizId);
	}
}
