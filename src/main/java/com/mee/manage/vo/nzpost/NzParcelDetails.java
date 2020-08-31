package com.mee.manage.vo.nzpost;

import lombok.Data;

@Data
public class NzParcelDetails {
    
    String service_code;

    //Array of ancillary codes. This field is only relevant for COURIERPOST. Valid addons are: CPSR, CPOLRD, CPOLOED, CPOLSED, CPOLSAT, CPOLDG.
    String[] add_ons; 

    //Indications if the label is used for returning items. Values must be OUTBOUND or RETURN. This field is only relevant for COURIERPOST.
    String return_indicator;

    String description;

    NzDimension dimensions;
    

}