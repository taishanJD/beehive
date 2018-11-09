package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.Message;
import com.quarkdata.yunpan.api.util.SignatureUtil;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class EmailSender {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public ResultCode<String> htmlMail(Map<String, String> mailConfMap, String[] receiver, String subject,
                                       String html, Integer incId) {

        ResultCode<String> result=new ResultCode<>();
        String username = mailConfMap.get("mailUsername");
        String sender = username;
        String password = mailConfMap.get("mailPwd");
        String host = mailConfMap.get("mailHost");
        /*Integer port = Integer.valueOf(mailConfMap.get("port"));
        String ssl = mailConfMap.get("ssl")*/;

        logger.info("邮件服务器配置信息==");
        logger.info("邮件服务器-账户=" + username);
        logger.info("邮件服务器-密码=" + password);
        logger.info("邮件服务器-host=" + host);

        boolean enableSSL = true;//使用ssl
       /* if (StringUtils.equals(ssl, "1")) {
            enableSSL = true;
        }*/

        MyJavaMailSender javaMailSender=this.initMailSender(host, username, password, enableSSL);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper;
        try {
            messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            messageHelper.setFrom(sender);
            messageHelper.setTo(receiver);
            messageHelper.setSubject(subject);

            // true 表示启动HTML格式的邮件
            messageHelper.setText(html, true);
            javaMailSender.send(mimeMessage);
            result.setCode(Messages.SUCCESS_CODE);
            result.setMsg(Messages.SUCCESS_MSG);
            return result;
        } catch (MessagingException e) {
            e.printStackTrace();
            result.setCode(Messages.SERVICE_INTERNAL_EXCEPTION_CODE);
            result.setMsg(Messages.SERVICE_INTERNAL_EXCEPTION_MSG);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof MailSendException) {
                logger.error("Invalid Addresses-->" + StringUtils.join(receiver, ","));
                result.setCode(Messages.EMAIL_INVALID_CODE);
                result.setMsg(Messages.EMAIL_INVALID_MSG);
                return result;
            } else {
                logger.error("unkonw exception");
                result.setCode(Messages.SERVICE_INTERNAL_EXCEPTION_CODE);
                result.setMsg(Messages.SERVICE_INTERNAL_EXCEPTION_MSG);
                return result;
            }
        }
    }

    /**
     *
     * @Author skh
     * @Date 2017年11月6日
     *
     * @param host 邮件服务器地址
     * @param username 邮件用户名
     * @param password 邮件密码
     * @param enableSSL 开启SSL加密
     * @return
     */
    private MyJavaMailSender initMailSender(String host, String username,
                                                                      String password, boolean enableSSL) {

        MyJavaMailSender mailSender = MyJavaMailSender.getInstance();

        mailSender.setHost(host);
        //mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.debug", "true");
        // properties.setProperty("mail.debug", "false");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.ssl.enable", String.valueOf(enableSSL));

        mailSender.setJavaMailProperties(properties);

        return mailSender;
    }

    /**
     * 填充邮件模板，获得邮件内容字符串
     *
     * @Author skh
     * @Date 2017年11月8日
     *
     * @param map
     * @param templateName 模板名称
     * @return
     */
    public String getMailText(Map<String, String> map, String templateName) {

        String html = "";
        try {
           // FreeMarkerConfigurer freeMarkerConfigure=new FreeMarkerConfigurer();
            Template template = freeMarkerConfigurer.getConfiguration().getTemplate(templateName);
            html = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);

        } catch (IOException e) {
            e.printStackTrace();
            logger.info("====>get email tempate fail! " + templateName);
        } catch (TemplateException e) {
            e.printStackTrace();
            logger.info("====>get email tempate fail! " + templateName);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof MailSendException) {
                logger.error("Invalid Addresses");
            } else {
                logger.error("unkonw exception");
            }
        }
        return html;
    }

    /**
     * 发送忘记密码邮件
     * @param mailConfMap
     * @param username
     * @param receiver
     * @param validTime
     * @param passwordRule
     * @param incId
     * @param resetType
     * @return
     */
    //@Override
    public ResultCode<String> sendForgotPwdMail(Map<String, String> mailConfMap, String username,
                                                String[] receiver, String validTime, String passwordRule,
                                                Integer incId, String resetType) {
        ResultCode<String> result=new ResultCode<>();
        // 发送激活邮件，不修改用户的状态，
        // 等到用户主动点击激活邮件中的链接后再变更用户的状态，并且状态只能从未激活到已激活

        // 生成重置密码url，链接需要做签名验证，需要有有效期
        Long timestamp = SignatureUtil.getCurrentTimestampMs();
        String privateKey = SignatureUtil.PRIVATE_KEY;
        SignatureUtil.SignType signType = SignatureUtil.SignType.MD5;

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("username", username);
        paramMap.put("incId", String.valueOf(incId));
        paramMap.put("timestamp", timestamp.toString());
        paramMap.put("passwordRule", passwordRule);
        paramMap.put("resetType", resetType);

        String sign = "";
        try {
            sign = SignatureUtil.generateSignature(paramMap, privateKey, signType);
        } catch (Exception e) {
            logger.info("====>username:" + username + " , sign invalid!");
            e.printStackTrace();
        }

        /* String projectName = ServerCfg.getInstance().getProjectName(); */
        //String webUrl = ServerCfg.getInstance().getWebUrl();
        String serverUrl=null;
        try {
            serverUrl = InetAddress.getLocalHost().getHostAddress();
            logger.debug("server address:"+serverUrl);

        }catch (Exception ex){
            logger.debug("get server address exception");
            result.setCode(Messages.SERVICE_INTERNAL_EXCEPTION_CODE);
            result.setMsg(Messages.SERVICE_INTERNAL_EXCEPTION_MSG);
            return result;
        }
        // 示例：http://192.168.7.58:4200/#/login/resetPassword/asdgafbvadbvadfadvg9au0sdfvajn/fuyb@qq.com/149000000/1001/1/8_1_1_1_0
        // 签名/用户名/时间戳/incId/resetType/passwordRule
        String url = "http://" + serverUrl + "/login/resetPassword/" + sign
                + "/" + username + "/" + timestamp + "/" + incId + "/" + resetType + "/"
                + passwordRule;

        logger.info("====> reset password URL：" + url);
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("url", url);
        map.put("validTime", validTime);

        String html = "";
        // 默认中文
        String subject = "密码重置";
        // 邮件模板
        String templateName = "forgotPwd.ftl";

        html = this.getMailText(map, templateName);

        result = this.htmlMail(mailConfMap, receiver, subject, html, incId);
        return result;
    }
}
