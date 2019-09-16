package com.mee.manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.Products;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.BathProVos;
import com.mee.manage.vo.MeeProductVo;
import com.mee.manage.vo.ProVo;
import com.mee.manage.vo.SuppliersVo;
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

    List<Sample> getAllProducts();

    List<MeeProductVo> getMeeProducts();

    List<SuppliersVo> getSuppliers();

    List<Sample> getSampleProducts(List<MeeProductVo> meeProductVos);

    Map<String,MeeProductVo> getMapMeeProduct();

    public Set<String> getSetCode(List<MeeProductVo> meeProductVos);
}
