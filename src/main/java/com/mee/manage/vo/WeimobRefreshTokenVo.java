package com.mee.manage.vo;

import lombok.Data;

@Data
public class WeimobRefreshTokenVo {
    String refresh_token;
    String grant_type;
    String client_id;
    String client_secret;
    String redirect_uri;
}
