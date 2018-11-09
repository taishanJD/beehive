package com.quarkdata.quark.share.model.dataobj;

import java.io.Serializable;
import java.util.Date;

public class Users implements Serializable {
    private Integer userId;

    private String userName;

    private String password;

    private String displayName;

    private String email;

    private Integer isEmailValidated;

    private Date createTime;

    private Date lastloginTime;

    private String lastloginIp;

    private Integer incid;

    private String mobile;

    private Integer isMobileValidated;

    private Date updateTime;

    private Integer iamId;

    private String guid;

    private String departmentGuid;

    private String iamSource;

    private String firstName;

    private String lastName;

    private Integer userStatus;

    private String source;

    private Integer bindStatus;

    private String createAdId;

    private String organize;

    private String pinyin;

    private String hashCode;

    private String dn;

    private String otpSecret;

    private Integer createdBy;

    private String isAdmin;

    private String adminPlatform;

    private Date lockedAt;

    private Date passwordSetAt;

    private Integer prevStatus;

    private String userPrincipalName;

    private String immutableId;

    private Integer pluginId;

    private static final long serialVersionUID = 1L;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName == null ? null : displayName.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public Integer getIsEmailValidated() {
        return isEmailValidated;
    }

    public void setIsEmailValidated(Integer isEmailValidated) {
        this.isEmailValidated = isEmailValidated;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastloginTime() {
        return lastloginTime;
    }

    public void setLastloginTime(Date lastloginTime) {
        this.lastloginTime = lastloginTime;
    }

    public String getLastloginIp() {
        return lastloginIp;
    }

    public void setLastloginIp(String lastloginIp) {
        this.lastloginIp = lastloginIp == null ? null : lastloginIp.trim();
    }

    public Integer getIncid() {
        return incid;
    }

    public void setIncid(Integer incid) {
        this.incid = incid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public Integer getIsMobileValidated() {
        return isMobileValidated;
    }

    public void setIsMobileValidated(Integer isMobileValidated) {
        this.isMobileValidated = isMobileValidated;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIamId() {
        return iamId;
    }

    public void setIamId(Integer iamId) {
        this.iamId = iamId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid == null ? null : guid.trim();
    }

    public String getDepartmentGuid() {
        return departmentGuid;
    }

    public void setDepartmentGuid(String departmentGuid) {
        this.departmentGuid = departmentGuid == null ? null : departmentGuid.trim();
    }

    public String getIamSource() {
        return iamSource;
    }

    public void setIamSource(String iamSource) {
        this.iamSource = iamSource == null ? null : iamSource.trim();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName == null ? null : firstName.trim();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName == null ? null : lastName.trim();
    }

    public Integer getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Integer userStatus) {
        this.userStatus = userStatus;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source == null ? null : source.trim();
    }

    public Integer getBindStatus() {
        return bindStatus;
    }

    public void setBindStatus(Integer bindStatus) {
        this.bindStatus = bindStatus;
    }

    public String getCreateAdId() {
        return createAdId;
    }

    public void setCreateAdId(String createAdId) {
        this.createAdId = createAdId == null ? null : createAdId.trim();
    }

    public String getOrganize() {
        return organize;
    }

    public void setOrganize(String organize) {
        this.organize = organize == null ? null : organize.trim();
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin == null ? null : pinyin.trim();
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode == null ? null : hashCode.trim();
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn == null ? null : dn.trim();
    }

    public String getOtpSecret() {
        return otpSecret;
    }

    public void setOtpSecret(String otpSecret) {
        this.otpSecret = otpSecret == null ? null : otpSecret.trim();
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin == null ? null : isAdmin.trim();
    }

    public String getAdminPlatform() {
        return adminPlatform;
    }

    public void setAdminPlatform(String adminPlatform) {
        this.adminPlatform = adminPlatform == null ? null : adminPlatform.trim();
    }

    public Date getLockedAt() {
        return lockedAt;
    }

    public void setLockedAt(Date lockedAt) {
        this.lockedAt = lockedAt;
    }

    public Date getPasswordSetAt() {
        return passwordSetAt;
    }

    public void setPasswordSetAt(Date passwordSetAt) {
        this.passwordSetAt = passwordSetAt;
    }

    public Integer getPrevStatus() {
        return prevStatus;
    }

    public void setPrevStatus(Integer prevStatus) {
        this.prevStatus = prevStatus;
    }

    public String getUserPrincipalName() {
        return userPrincipalName;
    }

    public void setUserPrincipalName(String userPrincipalName) {
        this.userPrincipalName = userPrincipalName == null ? null : userPrincipalName.trim();
    }

    public String getImmutableId() {
        return immutableId;
    }

    public void setImmutableId(String immutableId) {
        this.immutableId = immutableId == null ? null : immutableId.trim();
    }

    public Integer getPluginId() {
        return pluginId;
    }

    public void setPluginId(Integer pluginId) {
        this.pluginId = pluginId;
    }
}