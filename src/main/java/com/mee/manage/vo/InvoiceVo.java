package com.mee.manage.vo;

import lombok.Data;

import java.util.List;

@Data
public class InvoiceVo {

    private String invoiceDate;

    private String invoiceNo;

    private List<ProductsVo> products;

}
