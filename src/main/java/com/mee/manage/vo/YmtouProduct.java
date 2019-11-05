package com.mee.manage.vo;

import lombok.Data;

import java.util.List;

@Data
public class YmtouProduct {

    String product_id;

    String product_name;

    Integer category_id;

    String category_name;

    int brand_id;

    String brand_name;

    String[] product_images;

    String product_url;

    String create_time;

    String update_time;

    String listing_start_time;

    String listing_end_time;

    Integer delivery_type;

    Boolean fbx;

    String remark;

    List<YmtouSkuInfo> skus;
}
