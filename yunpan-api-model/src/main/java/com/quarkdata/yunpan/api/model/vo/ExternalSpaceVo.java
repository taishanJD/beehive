package com.quarkdata.yunpan.api.model.vo;

import com.quarkdata.quark.share.model.dataobj.Users;

import java.util.List;

public class ExternalSpaceVo {

    private String documentName;

    private List<DocumentPrivilegeVO> permissions;

    private String externalAccount;//外部账号

    private String externalPwd;//外部账号--密码

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public List<DocumentPrivilegeVO> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<DocumentPrivilegeVO> permissions) {
        this.permissions = permissions;
    }

    public String getExternalAccount() {
        return externalAccount;
    }

    public void setExternalAccount(String externalAccount) {
        this.externalAccount = externalAccount;
    }

    public String getExternalPwd() {
        return externalPwd;
    }

    public void setExternalPwd(String externalPwd) {
        this.externalPwd = externalPwd;
    }
}
