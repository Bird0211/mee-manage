package com.mee.manage.vo.ugg;

import java.util.Date;

import lombok.Data;

@Data
public class QueryParams {
    
    Long bizId;

    String resource;

    Date start;

    Date end;

    Integer status;

    String extId;

    String batchId;

}
