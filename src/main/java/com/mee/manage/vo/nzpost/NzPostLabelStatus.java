package com.mee.manage.vo.nzpost;

import lombok.Data;

@Data
public class NzPostLabelStatus {
    
    String consignment_id;

    String consignment_status;

    String consignment_url;

    LabelError[] errors;

    String expiry_date_utc;

    Labels[] labels;

    String message_id;

    String[] page_urls;

    Boolean success;

}