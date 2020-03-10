package com.mee.manage.vo.weimob;

import lombok.Data;

@Data
public class WeimobApplyTokenVo {

    String code;
    String grant_type;
    String client_id;
    String client_secret;
    String redirect_uri;

}
