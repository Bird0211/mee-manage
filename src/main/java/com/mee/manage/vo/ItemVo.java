package com.mee.manage.vo;

import lombok.Data;

import java.util.List;

@Data
public class ItemVo {

    List<WordsVo> sku;

    List<WordsVo> description;

    List<WordsVo> qty;

    List<WordsVo> unitPrice;
}
