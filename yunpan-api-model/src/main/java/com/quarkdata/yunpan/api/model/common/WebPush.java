package com.quarkdata.yunpan.api.model.common;

import java.util.Map;

/**
 * author:liuda
 * Date:2018/9/19
 * Time:16:22
 */
public class WebPush {
    /**
     * 浏览器唯一标识
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

    @Override
    public String toString() {
        return "WebPush{" +
                "udid='" + udid + '\'' +
                ", cmd='" + cmd + '\'' +
                ", params=" + params +
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

    public WebPush(String udid, String cmd, Map<String, String> params) {
        this.udid = udid;
        this.cmd = cmd;
        this.params = params;
    }

    public WebPush() {
    }
}
