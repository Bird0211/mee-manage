package com.mee.manage.vo.nzpost;

import lombok.Data;

@Data
public class NzpostTokenRsp {
    
    String access_token;

    String token_type;

    String expires_in;

}