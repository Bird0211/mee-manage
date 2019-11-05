package com.mee.manage.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class YmtouOrderItem {

    String outer_sku_id;
    BigDecimal m_adjust_discount;
    String refund_status;
    Integer num;
    String sku_id;
    String sku_properties_name;
    String refund_id;
    String product_title;
    String order_item_id;
    BigDecimal shipping_fee;
    BigDecimal m_coupon_discount;
    Integer delivery_type;
    BigDecimal price;
    String product_id;
    Integer refund_num;
    BigDecimal payment;
    BigDecimal p_coupon_discount;
    Long order_id;
    BigDecimal m_promotion_discount;
}
