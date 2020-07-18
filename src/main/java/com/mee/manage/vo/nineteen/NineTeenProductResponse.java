package com.mee.manage.vo.nineteen;

import java.util.List;

import lombok.Data;

@Data
public class NineTeenProductResponse {
    int page;
    int pageSize;
    int total;
    List<NineTeenProduct> products;
}