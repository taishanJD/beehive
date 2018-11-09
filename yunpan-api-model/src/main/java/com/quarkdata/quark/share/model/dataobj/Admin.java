package com.quarkdata.quark.share.model.dataobj;

import java.io.Serializable;

public class Admin implements Serializable {
    private Integer id;

    private String adminname;

    private String password;

    private String email;

    @Deprecated
    private Integer depid;

    private Integer incid;

    private String source;

    private String displayname;

    private Integer role;

    private Integer userId;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAdminname() {
        return adminname;
    }

    public void setAdminname(String adminname) {
        this.adminname = adminname == null ? null : adminname.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    @Deprecated
    public Integer getDepid() {
        return depid;
    }

    @Deprecated
    public void setDepid(Integer depid) {
        this.depid = depid;
    }

    public Integer getIncid() {
        return incid;
    }

    public void setIncid(Integer incid) {
        this.incid = incid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source == null ? null : source.trim();
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname == null ? null : displayname.trim();
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}