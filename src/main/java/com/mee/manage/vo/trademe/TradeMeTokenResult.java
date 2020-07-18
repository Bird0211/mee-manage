package com.mee.manage.vo.trademe;

import lombok.Data;

@Data
public class TradeMeTokenResult {

    String oauth_token;

    String oauth_token_secret;

    String oauth_callback_confirmed;
    
}