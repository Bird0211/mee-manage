package com.mee.manage.vo.nineteen;

import java.util.List;

import lombok.Data;

@Data
public class NineTeenUpdatePrice {
    
    String goodCode;

    List<NineTeenUpdateSku> skuInfos;

}