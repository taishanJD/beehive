package com.quarkdata.yunpan.api.model.common;

import java.util.Map;

/**
 * author:liuda
 * Date:2018/9/13
 * Time:17:34
 */
public class AndroidPush {

    /**
     * 设备标识
     */
    private String udid;

    /**
     * 指令标识
     */
    private String cmd;

    /**
     * 额外数据
     */
    private Map<String, String> params;

    private String cre;

    private String upd;

    @Override
    public String toString() {
        return "AndroidPush{" +
                "udid='" + udid + '\'' +
                ", cmd='" + cmd + '\'' +
                ", params=" + params +
                ", cre='" + cre + '\'' +
                ", upd='" + upd + '\'' +
                '}';
    }

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

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getCre() {
        return cre;
    }

    public void setCre(String cre) {
        this.cre = cre;
    }

    public String getUpd() {
        return upd;
    }

    public void setUpd(String upd) {
        this.upd = upd;
    }

    public AndroidPush() {
    }

    public AndroidPush(String udid, String cmd, Map<String, String> params, String cre, String upd) {
        this.udid = udid;
        this.cmd = cmd;
        this.params = params;
        this.cre = cre;
        this.upd = upd;
    }
}
