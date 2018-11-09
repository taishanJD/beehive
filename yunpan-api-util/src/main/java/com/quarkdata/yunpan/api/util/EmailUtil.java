package com.quarkdata.yunpan.api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EmailUtil {

    private static Logger logger = LoggerFactory.getLogger(EmailUtil.class);

    private static JavaMailSenderImpl javaMailSender;

    @Autowired
    public void setJavaMailSender(JavaMailSenderImpl javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * 验证输入的邮箱格式是否符合
     * @param email
     * @return 是否合法
     */
    public static boolean emailFormat(String email) {
        boolean tag = true;
        final String pattern1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        final Pattern pattern = Pattern.compile(pattern1);
        final Matcher matcher= pattern.matcher(email);
        if (!matcher.find()) {
            tag = false;
        }
        return tag;
    }

    public static void send(String[] emailAddresses, String bodyText, String subject, String attachmentName, ClassPathResource resource){
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(javaMailSender.getUsername());
            helper.setTo(emailAddresses);
            helper.setSubject(subject);
            helper.setText(bodyText);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.info("发送邮件失败", e);
        }

    }

    public static void sendRichContent(String[] emailAddresses, String bodyText, String subject, String attachmentName, ClassPathResource resource){
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(javaMailSender.getUsername());
            helper.setTo(emailAddresses);
            helper.setSubject(subject);
            helper.setText(bodyText, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.info("发送邮件失败", e);
        }

    }


}
