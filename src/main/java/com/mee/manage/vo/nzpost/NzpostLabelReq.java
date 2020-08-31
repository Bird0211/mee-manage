package com.mee.manage.vo.nzpost;

import java.util.List;

import lombok.Data;

@Data
public class NzpostLabelReq {
    String carrier;     //Carrier of the parcel. Value must be one of COURIERPOST OR PACE

    String orientation; //Print orientation of the label. Value must be PORTRAIT or LANDSCAPE. Default value is LANDSCAPE.

    String sender_reference_1;

    String sender_reference_2;

    NzPostSendDetails sender_details;

    NzReceiverDetails receiver_details;

    NzPickupAddress pickup_address;	

    NzDeliveryAddress delivery_address;

    List<NzParcelDetails> parcel_details;
}