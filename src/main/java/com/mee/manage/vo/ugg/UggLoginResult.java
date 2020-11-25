package com.mee.manage.vo.ugg;

import lombok.Data;

@Data
public class UggLoginResult<T> {
    
    Integer Status;

    String ErrorMsg;

    T Data;
}
