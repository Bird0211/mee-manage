package com.mee.manage.service;

import com.mee.manage.vo.ProductsVo;

import java.util.List;

public interface IInvoiceService {


    String getInvoiceDate();

    String getInvoiceNo();

    List<ProductsVo> getProducts();


}
