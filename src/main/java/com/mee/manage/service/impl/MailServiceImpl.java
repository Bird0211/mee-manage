package com.mee.manage.service.impl;

import com.mee.manage.service.IMailService;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class MailServiceImpl implements IMailService {


    public static final Logger logger = LoggerFactory.getLogger(IMailService.class);


    @Autowired
    private JavaMailSender mailSender;

    //发送邮件的模板引擎
    @Autowired
    private FreeMarkerConfigurer configurer;


    /**

     * 文本

     * @param from

     * @param to

     * @param subject

     * @param content

     */

    @Override

    public void sendSimpleMail(String from, String to, String subject, String content) {



        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);

        message.setTo(to);

        message.setSubject(subject);

        message.setText(content);

        try {

            mailSender.send(message);

            logger.info("simple mail had send。");

        } catch (Exception e) {

            logger.error("send mail error", e);

        }

    }



    /**

     * @param from

     * @param to

     * @param subject

     * @param model

     * @param templateName

     */

    public void sendTemplateMail(String from,
                                 String to,
                                 String subject,
                                 Map<String, Object> model,
                                 String templateName) throws MessagingException, IOException, TemplateException {

        MimeMessage message = mailSender.createMimeMessage();


            //true表示需要创建一个multipart message

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from);

            helper.setTo(to);

            helper.setSubject(subject);

            Template template = configurer.getConfiguration().getTemplate(templateName+".ftl");
            String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            helper.setText(text, true);

            mailSender.send(message);

            logger.info("send template success");


    }



    /**

     * 附件

     *

     * @param from

     * @param to

     * @param subject

     * @param content

     * @param filePath

     */

    public void sendAttachmentsMail(String from, String to, String subject, String content, String filePath){

        MimeMessage message = mailSender.createMimeMessage();

        try {

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from);

            helper.setTo(to);

            helper.setSubject(subject);

            helper.setText(content, true);


            FileSystemResource file = new FileSystemResource(new File(filePath));

            String fileName = filePath.substring(filePath.lastIndexOf(File.separator));

            helper.addAttachment(fileName, file);


            mailSender.send(message);

            logger.info("send mail with attach success。");

        } catch (Exception e) {

            logger.error("send mail with attach success", e);

        }

    }



    /**

     * 发送内嵌图片

     *

     * @param from

     * @param to

     * @param subject

     * @param content

     * @param imgPath

     * @param imgId

     */
    public void sendInlineResourceMail(String from, String to, String subject, String content,

                                       String imgPath, String imgId){

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from);

            helper.setTo(to);

            helper.setSubject(subject);

            helper.setText(content, true);

            FileSystemResource res = new FileSystemResource(new File(imgPath));

            helper.addInline(imgId, res);


            mailSender.send(message);

            logger.info("send inner resources success。");

        } catch (Exception e) {
            logger.error("send inner resources fail", e);
        }
    }

}
