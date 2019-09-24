package com.mee.manage.vo;

import com.mee.manage.util.StatusCode;
import lombok.Data;

@Data
public class PriceUpdateResult {

    boolean success;

    String sku;

    StatusCode statusCode;

}
