package com.mee.manage.vo.nzpost;

import lombok.Data;

@Data
public class NzPickupAddress {
    
    String address_id;

    String dpid;

    String site_code;

    String street;

    String suburb;

    String city;

    String postcode;

    String country_code;

    String company_name;

}