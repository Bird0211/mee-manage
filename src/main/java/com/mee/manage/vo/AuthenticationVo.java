package com.mee.manage.vo;

import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class AuthenticationVo {

    String bizId;

    String time;

    String nonce;

    String sign;

    public AuthenticationVo(){

    }

    public AuthenticationVo(String bizId,String time,String nonce,String sign){
        this.bizId = bizId;
        this.time = time;
        this.nonce = nonce;
        this.sign = sign;
    }

    public boolean isEmpty(){
        if(StringUtils.isEmpty(this.getBizId()) ||
                StringUtils.isEmpty(this.getNonce()) ||
                StringUtils.isEmpty(this.getSign()) ||
                StringUtils.isEmpty(this.getTime())
        )
            return true;
        else
            return false;
    }

}
