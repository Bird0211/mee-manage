package com.mee.manage.vo.ugg;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class UggProductDetail {
    Long Barcode;

    String ProductCode;

    String ProductName;

    String ProductNameCn;

    Integer ColorCode;

    String ColorName;

    String ColorNameCn;

    String Size;

    String Brand;

    BigDecimal RetailPrice;

    BigDecimal Price;

    List<String> ProductImage;
    
    String ProductImageDesCn;

    String ProductImageDesEn;

    ProductDetail ProductDetails;

    ProductDetail ProductDetailsCn;

    Double ProductWeight;
}
