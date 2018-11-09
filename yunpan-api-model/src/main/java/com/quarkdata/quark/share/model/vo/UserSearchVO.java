package com.quarkdata.quark.share.model.vo;

import com.quarkdata.quark.share.model.dataobj.Users;

import java.util.List;

/**
 * Created by yanyq1129@thundersoft.com on 2018/6/27.
 */
public class UserSearchVO extends Users {

    private String deptNamePath;
    private List<String> groupNames;
    private Long id; // 用户id
    private String _type;
    private Long roleId;

    public List<String> getGroupNames() {
        return groupNames;
    }

    public void setGroupNames(List<String> groupNames) {
        this.groupNames = groupNames;
    }


    public String getDeptNamePath() {
        return deptNamePath;
    }

    public void setDeptNamePath(String deptNamePath) {
        this.deptNamePath = deptNamePath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String get_type() {
        return _type;
    }

    public void set_type(String _type) {
        this._type = _type;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
