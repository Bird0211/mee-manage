package com.mee.manage.vo;

import lombok.Data;

@Data
public class WeimobTokenResponse {
    String access_token;
    String token_type;
    int expires_in;
    String refresh_token;
    int refresh_token_expires_in;
    String scope;
    String business_id;
    String public_account_id;
}
