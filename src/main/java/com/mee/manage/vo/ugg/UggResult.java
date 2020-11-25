package com.mee.manage.vo.ugg;

import lombok.Data;

@Data
public class UggResult<T> {
    
    Integer code;

    String msg;

    T result; 

}
