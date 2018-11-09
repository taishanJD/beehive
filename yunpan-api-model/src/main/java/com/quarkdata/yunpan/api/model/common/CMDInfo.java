package com.quarkdata.yunpan.api.model.common;

/**
 * author:liuda
 * Date:2018/9/11
 * Time:18:10
 */
public class CMDInfo {

    /**
     * 设备唯一标识
     */
    private String udid;
    /**
     * 指令标识
     */
    private String cmd;
    /**
     * 厂商标识
     */
    private String prd;
    /**
     * 华为推送的唯一标识
     */
    private String token;
    /**
     * 小米推送唯一标识
     */
    private String regid;

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getPrd() {
        return prd;
    }

    public void setPrd(String prd) {
        this.prd = prd;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRegid() {
        return regid;
    }

    public void setRegid(String regid) {
        this.regid = regid;
    }
}
