package com.mee.manage.vo;

import lombok.Data;

import java.util.Map;

@Data
public class MailParam {

    String to;

    String subject;

    String templateName;

    Map<String,Object> model;

}
