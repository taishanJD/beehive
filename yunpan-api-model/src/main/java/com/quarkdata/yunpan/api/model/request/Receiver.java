package com.quarkdata.yunpan.api.model.request;

/**
 * @author maorl
 * @date 12/11/17.
 */
public class Receiver {
    private String recId;
    private String recType;
    private String permission;

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setRecId(String recId) {
        this.recId = recId;
    }

    public String getRecId() {
        return recId;
    }

    public void setRecType(String recType) {
        this.recType = recType;
    }

    public String getRecType() {
        return recType;
    }
}
