package com.mee.manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mee.manage.po.Products;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.BathProVos;
import com.mee.manage.vo.ProVo;

import java.util.List;

public interface IProductsService extends IService<Products> {

    boolean insertProduct(ProVo product);

    boolean insertProducts(BathProVos product);

    boolean updateProductBySku(Products product);

    Products getProductBySku(Long sku);

    List<Products> getProductsBySkus(List<Long> skus);
}
