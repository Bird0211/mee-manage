package com.mee.manage.vo;

import lombok.Data;

@Data
public class OAuth2Vo {
    String prefix;

    String token;

    public String toString() {
        return this.prefix + " " + this.token;
    }
}