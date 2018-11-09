package com.quarkdata.yunpan.api.model.vo;

import java.util.List;

/**
 * Created by yanyq1129@thundersoft.com on 2018/6/11.
 */
public class CreateOrganizedDirectoryVO {

    private String documentName;

    private Long parentId;

    private List<DocumentPrivilegeVO> permissions;

    private List<DocumentPrivilegeVOfGenerateAccounts> permissionsOfGenerateAccounts;

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<DocumentPrivilegeVO> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<DocumentPrivilegeVO> permissions) {
        this.permissions = permissions;
    }

    public List<DocumentPrivilegeVOfGenerateAccounts> getPermissionsOfGenerateAccounts() {
        return permissionsOfGenerateAccounts;
    }

    public void setPermissionsOfGenerateAccounts(List<DocumentPrivilegeVOfGenerateAccounts> permissionsOfGenerateAccounts) {
        this.permissionsOfGenerateAccounts = permissionsOfGenerateAccounts;
    }

}
