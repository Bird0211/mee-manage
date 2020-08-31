package com.mee.manage.vo.Yiyun;

import lombok.Data;

@Data
public class YiyunOrderDetail {

    YiyunOrderDetail() {

    }

    YiyunOrderDetail(String details) {
        if(details != null) {
            String[] info = details.split("\\|");
            if(info != null && info.length == 7) {
                this.id = info[0].trim();
                this.sku = info[1].trim();
                this.name = info[2];
                this.weight = Integer.parseInt(info[3].trim());
                this.price = info[4];
                this.number = Double.parseDouble(info[5].trim());
                this.totalPrice = info[6];
            }
        }
    }
    
    String id;

    String sku;

    String name;

    Integer weight;

    String price;

    Double number;

    String totalPrice;

}