package com.mee.manage.service;

import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Map;

public interface IMailService {

    void sendSimpleMail(String from, String to, String subject, String content);

    void sendTemplateMail(String from,
                          String to,
                          String subject,
                          Map<String, Object> model,
                          String templateName) throws MessagingException, IOException, TemplateException;

    void sendAttachmentsMail(String from, String to, String subject, String content, String filePath);

    void sendInlineResourceMail(String from, String to, String subject, String content,

                                String imgPath, String imgId);

    }
