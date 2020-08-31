package com.mee.manage.vo.nzpost;

import lombok.Data;

@Data
public class CreateLabelRep {
    
    String consignment_id;

    String message_id;

    boolean success;
}