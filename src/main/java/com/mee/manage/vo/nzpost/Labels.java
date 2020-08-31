package com.mee.manage.vo.nzpost;

import lombok.Data;

@Data
public class Labels {
    String label_id;
    String tracking_reference;
    String label_generation_status;
    LabelError[] errors;
}