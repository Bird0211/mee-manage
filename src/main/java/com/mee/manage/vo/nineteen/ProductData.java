package com.mee.manage.vo.nineteen;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ProductData {
    Date create_time;

    int distribution_set;

    int distribution_type;

    String first_commission;

    String second_commisstion;

    String good_type;

    String good_describe;

    String good_name;

    int code;

    String name_ch;

    String name_eh;

    int spt1;

    String description;

    List<String> spec_name;

    List<String[]> spec_son_name;

    List<SkuInfo> sku_info;

    List<String> good_group;
}