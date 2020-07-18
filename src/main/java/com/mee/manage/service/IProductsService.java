package com.mee.manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.Products;
import com.mee.manage.vo.*;
import com.mee.manage.vo.Yiyun.YiyunTopProduct;

import org.jdmp.core.sample.Sample;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IProductsService extends IService<Products> {

    boolean insertProduct(ProVo product);

    boolean insertProducts(BathProVos product);

    boolean updateProductBySku(Products product);

    Products getProductBySku(Long sku);

    List<Products> getProductsBySkus(List<Long> skus);

    List<MeeProductVo> getProductsBySkus(List<MeeProductVo> products, List<String> skus);

    List<Sample> getAllProducts();

    List<MeeProductVo> getMeeProducts(String bizId);

    List<MeeProductVo> getMeeProductsByUrl(String bizId);

    Map<String, MeeProductVo> getMeeProductsBySku(String bizId, List<String> skus);

    List<SuppliersVo> getSuppliers(String bizId);

    List<Sample> getSampleProducts(List<MeeProductVo> meeProductVos);

    Map<String,MeeProductVo> getMapMeeProduct(String bizId);

    Set<String> getSetCode(List<MeeProductVo> meeProductVos);

    List<ComparePricesVo> getComparePrice(List<InvoiceProduct> invoiceProducts,String bizId);

    List<YiyunTopProduct> getTopProducts(String bizId);


}
