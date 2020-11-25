package com.mee.manage.vo.ugg;

import java.util.List;

import lombok.Data;

@Data
public class ProductDetail {
    String Upper;

    String Lining;

    String Sole;

    String Insole;

    List<String> Feature;

    List<String> Other;
}
