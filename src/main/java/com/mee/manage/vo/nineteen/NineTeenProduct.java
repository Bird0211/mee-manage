package com.mee.manage.vo.nineteen;

import java.util.List;

import lombok.Data;

@Data
public class NineTeenProduct {
    
    String productName;

    Integer goodCode;

    String nameCh;

    String nameEn;

    List<NineTeenSku> skuInfo;

}