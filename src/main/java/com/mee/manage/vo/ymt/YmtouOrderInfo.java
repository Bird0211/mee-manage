package com.mee.manage.vo.ymt;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class YmtouOrderInfo {

            String payment_order_no;
            Double m_adjust_discount;
            String receiver_email;
            String order_time;
            String buyer_id;
            Boolean domestic_delivered;
            String receiver_state;
            BigDecimal m_coupon_discount;
            Integer order_status;
            String trade_id;
            String receiver_zip;
            String receiver_name;
            String pay_type;
            BigDecimal payment;
            BigDecimal p_coupon_discount;
            String accept_time;
            String shipping_time;
            String seller_id;
            String cancel_time;
            BigDecimal amount;
            String seller_memo;
            String paid_time;
            String receiver_mobile;
            BigDecimal shipping_fee;
            String buyer_remark;

            String id_cards;
            String receiver_address;
            String receiver_phone;
            Long order_id;
            BigDecimal m_promotion_discount;
            Boolean pre_sale;

            List<YmtouOrderItem> order_items_info;
            List<YmtouDeliveryInfo> delivery_info;

}
