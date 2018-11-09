package com.quarkdata.yunpan.api.model.vo;

import java.util.Date;

/**
 * Created by yanyq1129@thundersoft.com on 2018/5/31.
 */
public class DocumentPrivilegeVOfGenerateAccounts {

    private String receiverName;

    private String receiverPassword;

    private String permission;

    private Date expiryDate;

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPassword() {
        return receiverPassword;
    }

    public void setReceiverPassword(String receiverPassword) {
        this.receiverPassword = receiverPassword;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

}
