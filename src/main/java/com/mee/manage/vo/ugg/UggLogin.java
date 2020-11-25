package com.mee.manage.vo.ugg;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

@Data
public class UggLogin {
    
    @JSONField(name="Account")
    String Account;

    @JSONField(name = "Password")
    String Password;
}
