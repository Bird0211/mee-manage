package com.mee.manage.vo.nzpost;

import lombok.Data;

@Data
public class NzPostSendDetails {
    String name;
    String phone;
    Integer site_code;   //Site code assigned to the address where the parcel will be picked up by the courier. This address is also used for the NZ Post manifest process.
    String company_name;
    String email;
    String freepost_number;
}