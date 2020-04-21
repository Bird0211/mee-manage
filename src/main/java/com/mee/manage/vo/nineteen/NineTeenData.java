package com.mee.manage.vo.nineteen;

import java.util.List;

import lombok.Data;

/**
 * NineTeenData
 */
@Data
public class NineTeenData {

    Integer current_page;

    Integer last_page;

    Integer per_page;

    Integer total;

    List<NineTeenOrder> data;
    
}