package com.quarkdata.quark.share.model.vo;

import com.quarkdata.quark.share.model.dataobj.*;

import java.io.Serializable;
import java.util.List;

/**
 * 当前登录用户相关信息
 */
public class UserInfoVO implements Serializable{

    /**
     * 用户信息
     */
    private Users user;
    /**
     * 用户所在组
     */
    private List<Group> groupsList;
    /**
     * 用户所在部门
     */
    private Department department;
    /**
     * 用户所拥有角色
     */
    private OneshareRole role;
    /**
     * 超级管理员
     */
    private Admin admin;



    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public List<Group> getGroupsList() {
        return groupsList;
    }

    public void setGroupsList(List<Group> groupsList) {
        this.groupsList = groupsList;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public OneshareRole getRole() {
        return role;
    }

    public void setRole(OneshareRole role) {
        this.role = role;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }
}
