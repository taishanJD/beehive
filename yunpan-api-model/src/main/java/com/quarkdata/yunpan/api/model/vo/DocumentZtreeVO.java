package com.quarkdata.yunpan.api.model.vo;

import com.quarkdata.yunpan.api.model.dataobj.Document;

import java.util.List;

/**
 * Created by yanyq1129@thundersoft.com on 2018/3/28.
 */
public class DocumentZtreeVO extends Document {
    private String name;

    private Boolean isParent;

    private List<DocumentZtreeVO> children;

    private String permission;

    private boolean isSetPermission; //管理员是否设置过权限

    public String getPermission() {
        return permission;
    }

    public DocumentZtreeVO setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public boolean isSetPermission() {
        return isSetPermission;
    }

    public DocumentZtreeVO setSetPermission(boolean setPermission) {
        isSetPermission = setPermission;
        return this;
    }

    public String getName() {
        return name;
    }

    public DocumentZtreeVO setName(String name) {
        this.name = name;
        return this;
    }

    public Boolean getIsParent() {
        return true;
    }

    public DocumentZtreeVO setIsParent(Boolean parent) {
        isParent = parent;
        return this;
    }

    public List<DocumentZtreeVO> getChildren() {
        return children;
    }

    public DocumentZtreeVO setChildren(List<DocumentZtreeVO> children) {
        this.children = children;
        return this;
    }
}
