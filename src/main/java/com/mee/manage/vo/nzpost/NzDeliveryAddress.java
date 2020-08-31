package com.mee.manage.vo.nzpost;

import lombok.Data;

@Data
public class NzDeliveryAddress {

    String street;

    String suburb;

    String city;

    String postcode;

    String country_code;

}