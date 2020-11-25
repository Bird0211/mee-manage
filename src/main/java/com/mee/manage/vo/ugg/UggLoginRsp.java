package com.mee.manage.vo.ugg;

import lombok.Data;

@Data
public class UggLoginRsp {
    String Account;
    String AgentAlias;
    Boolean AllowSubmitWhenNonStock;
    String ChannelCode;
    String ChannelId;
    String ChannelName;
    Boolean EnableDropShipping;
    Boolean EnablePay;
    Boolean EnabledAgent;
    Boolean FirstLogin;
    String Id;
    String ImageBaseUrl;
    String Name;
    String StructId;
    String Token;
    Integer Type;
}
