package com.mee.manage.vo;

import lombok.Data;

import java.util.List;

@Data
public class MeeProductResponse {

    String result;

    String error;

    List<MeeProductVo> products;

}
