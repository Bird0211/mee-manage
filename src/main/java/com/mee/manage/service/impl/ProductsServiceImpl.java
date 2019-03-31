package com.mee.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mee.manage.mapper.IProductsMapper;
import com.mee.manage.po.Products;
import com.mee.manage.service.IProductsService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.BathProVos;
import com.mee.manage.vo.ProVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class ProductsServiceImpl extends ServiceImpl<IProductsMapper, Products>
        implements IProductsService {


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
