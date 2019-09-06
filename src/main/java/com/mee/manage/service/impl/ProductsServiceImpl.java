package com.mee.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.IPage;
import com.mee.manage.mapper.IProductsMapper;
import com.mee.manage.po.Products;
import com.mee.manage.service.IProductsService;
import com.mee.manage.util.*;
import com.mee.manage.vo.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.jdmp.core.dataset.DataSet;
import org.jdmp.core.dataset.DataSetFactory;
import org.jdmp.core.sample.DefaultSample;
import org.jdmp.core.sample.DefaultSampleFactory;
import org.jdmp.core.sample.Sample;
import org.jdmp.core.sample.SampleFactory;
import org.jdmp.core.variable.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ujmp.core.DenseMatrix;
import org.ujmp.core.Matrix;
import org.ujmp.core.mapmatrix.DefaultMapMatrix;
import org.ujmp.core.util.MathUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

@Service
public class ProductsServiceImpl extends ServiceImpl<IProductsMapper, Products>
        implements IProductsService {

    private static final Logger logger = LoggerFactory.getLogger(IProductsService.class);


    @Autowired
    Config config;

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
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.set("sku",product.getSku());
        return update(product,updateWrapper);
    }

    @Override
    public Products getProductBySku(Long sku) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("sku",sku);
        return getOne(queryWrapper);
    }

    @Override
    public List<Products> getProductsBySkus(List<Long> skus) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.in("sku",skus);
        return list(queryWrapper);
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
    public List<MeeProductVo> getMeeProducts() {
        String url = getMeeUrl(config.getAllProductUrl());
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
    public List<SuppliersVo> getSuppliers() {
        String url = getMeeUrl(config.getAllSupplieUrl());
        String result = JoddHttpUtils.getData(url);
        logger.info(result);
        List<SuppliersVo> suppliers = null;
        List<MeeSuppliersResponse> suppliersResponse = JSON.parseArray(result,MeeSuppliersResponse.class);
        if( suppliersResponse != null && suppliersResponse.size() > 0) {
            MeeSuppliersResponse response = suppliersResponse.get(0);
            if(response != null && response.getResult().equals("SUCCESS")) {
                suppliers = response.getSuppliers();
            }
        }

        return suppliers;
    }

    @Override
    public List<Sample> getSampleProducts() {
        List<MeeProductVo> meeProductVos = getMeeProducts();
        if(meeProductVos == null || meeProductVos.isEmpty()) {
            return null;
        }

        List<Sample> samples = new ArrayList<>();

        for(int i = 0; i < meeProductVos.size(); i++) {
            MeeProductVo meeProduct = meeProductVos.get(i);

            Sample sample = Sample.Factory.emptySample();

            sample.put("name", meeProduct.getName());
            Matrix input = Matrix.Factory.linkToArray(new String[] { meeProduct.getName() }).transpose();
            sample.put(Variable.INPUT, input);

            Matrix output = Matrix.Factory.linkToArray(meeProduct.getCode()).transpose();
            sample.put(Variable.TARGET, output);

            sample.setLabel(meeProduct.getCode());

            sample.setId("mee-"+i);

            samples.add(sample);
        }

        return samples;
    }

    @Override
    public Map<String, MeeProductVo> getMapMeeProduct() {
        List<MeeProductVo> allProducts = getMeeProducts();
        if(allProducts == null || allProducts.size() <= 0)
            return null;

        Map<String,MeeProductVo> mapProducts = new HashMap<>();
        for(MeeProductVo product : allProducts) {
            mapProducts.put(product.getCode(),product);
        }

        return mapProducts;
    }

    private String getMeeUrl(String url){
        String bizId = config.getBizId();
        Long time = DateUtil.getCurrentTime();
        String token = config.getToken();
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
}