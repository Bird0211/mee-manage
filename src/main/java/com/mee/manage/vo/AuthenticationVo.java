package com.mee.manage.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationVo {

    String bizId;

    String time;

    String nonce;

    String sign;

    String userId;

    public AuthenticationVo(){

    }

    public AuthenticationVo(String bizId,String userId,String time,String nonce,String sign){
        this.bizId = bizId;
        this.time = time;
        this.nonce = nonce;
        this.sign = sign;
        this.userId = userId;
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
