package com.quarkdata.yunpan.api.model.vo;

import com.quarkdata.quark.share.model.vo.DepartmentSearchVO;
import com.quarkdata.quark.share.model.vo.GroupSearchVO;
import com.quarkdata.quark.share.model.vo.UserSearchVO;

import java.io.Serializable;
import java.util.List;

public class MemberInfoVO implements Serializable{
    List<UserSearchVO> users;
    List<GroupSearchVO> groups;
    List<DepartmentSearchVO> departments;

    public MemberInfoVO(List<UserSearchVO> users, List<GroupSearchVO> groups, List<DepartmentSearchVO> departments){
        this.users = users;
        this.groups = groups;
        this.departments = departments;
    }

    public List<UserSearchVO> getUsers() {
        return users;
    }

    public void setUsers(List<UserSearchVO> users) {
        this.users = users;
    }

    public List<GroupSearchVO> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupSearchVO> groups) {
        this.groups = groups;
    }

    public List<DepartmentSearchVO> getDepartments() {
        return departments;
    }

    public void setDepartments(List<DepartmentSearchVO> departments) {
        this.departments = departments;
    }
}
