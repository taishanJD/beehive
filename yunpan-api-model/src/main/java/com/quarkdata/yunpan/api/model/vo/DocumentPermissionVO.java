package com.quarkdata.yunpan.api.model.vo;

import java.util.Date;

public class DocumentPermissionVO {

    private Long id; //用户，用户组，部门
    private String _type; //0用户、1用户组、2部门
    private String permission;// 权限：0只读、1读写、2管理
    private String name;//显示的名称
    private String displayName;//真实姓名
    private String email;//邮箱
    private String mobile;//手机号
    private String source;// 权限来源: father, self
    private Date expiryDate; // 如果是系统生成账号,显示该账号的有效期
    private String password; // 如果是系统生成账号,显示其密码

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

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this._type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
