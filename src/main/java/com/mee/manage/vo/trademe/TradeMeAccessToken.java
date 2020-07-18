package com.mee.manage.vo.trademe;

import lombok.Data;

@Data
public class TradeMeAccessToken {
    String token;

    String tokenVerifier;
}