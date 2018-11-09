package com.quarkdata.yunpan.api.model.common;

import java.util.Map;

/**
 * author:liuda
 * Date:2018/9/18
 * Time:14:36
 */
public class IosPush {
    /**
     * ios推送token
     */
    private String token;

    /**
     * 弹送的消息标题
     */
    private String alertTitle;

    /**
     * 弹出的消息内容
     */
    private String alertBody;

    /**
     * 显示图标
     */
    private String badge;

    /**
     * 声音提示
     */
    private String sound;

    /**
     * 附带数据
     */
    private Map<String,String> message;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAlertTitle() {
        return alertTitle;
    }

    public void setAlertTitle(String alertTitle) {
        this.alertTitle = alertTitle;
    }

    public String getAlertBody() {
        return alertBody;
    }

    public void setAlertBody(String alertBody) {
        this.alertBody = alertBody;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public Map<String, String> getMessage() {
        return message;
    }

    public void setMessage(Map<String, String> message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "IosPush{" +
                "token='" + token + '\'' +
                ", alertTitle='" + alertTitle + '\'' +
                ", alertBody='" + alertBody + '\'' +
                ", badge='" + badge + '\'' +
                ", sound='" + sound + '\'' +
                ", message=" + message +
                '}';
    }

    public IosPush(String token, String alertTitle, String alertBody, String badge, String sound, Map<String, String> message) {
        this.token = token;
        this.alertTitle = alertTitle;
        this.alertBody = alertBody;
        this.badge = badge;
        this.sound = sound;
        this.message = message;
    }

    public IosPush() {
    }
}
