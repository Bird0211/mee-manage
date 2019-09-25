package com.mee.manage.vo;

import com.mee.manage.po.ExlTitle;
import lombok.Data;

@Data
public class ExlTitleVo {

    public ExlTitleVo(){

    }

    public ExlTitleVo(ExlTitle exlTitle){
        this.name = exlTitle.getName();
        this.orderNo = exlTitle.getOrderNo();
        this.addr = exlTitle.getAddr();
        this.express = exlTitle.getExpress();
        this.idNo = exlTitle.getIdNo();
        this.num = exlTitle.getNum();
        this.phone = exlTitle.getPhone();
        this.productName = exlTitle.getProductName();
        this.sku = exlTitle.getSku();
        this.businessName = exlTitle.getBusinessName();
    }

    //名称
    String name;

    //订单编号
    String orderNo;

    //手机号
    String phone;

    //地址
    String addr;

    //商品名称
    String productName;

    //数量
    String num;

    //物流公司
    String express;

    //身份证号
    String idNo;

    //SKU编号
    String sku;

    String businessName;

    public ExlTitle toExlTitle(){
        ExlTitle exlTitle = new ExlTitle();
        exlTitle.setName(this.name);
        exlTitle.setAddr(this.addr);
        exlTitle.setExpress(this.express);
        exlTitle.setNum(this.num);
        exlTitle.setOrderNo(this.orderNo);
        exlTitle.setPhone(this.phone);
        exlTitle.setProductName(this.productName);
        exlTitle.setSku(this.sku);
        exlTitle.setBusinessName(this.businessName);
        exlTitle.setIdNo(this.idNo);
        return exlTitle;
    }

}
