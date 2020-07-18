package com.mee.manage.vo.nineteen;

import lombok.Data;

/**
 * NineTeenResponse
 */
@Data
public class NineTeenResponse<T> {

    Integer code;

    String msg;

    T data;
    
}