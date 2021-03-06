package com.mee.manage.vo;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class InvoiceVo {

    private Date invoiceDate;

    private String invoiceNo;

    private List<ProductsVo> products;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
