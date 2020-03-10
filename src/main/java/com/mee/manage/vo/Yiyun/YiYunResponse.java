package com.mee.manage.vo.Yiyun;


import lombok.Data;

@Data
public class YiYunResponse<T> {

    String result;

    String error;

    T data;
}
