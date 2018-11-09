package com.quarkdata.quark.share.model.dataobj;

import java.io.Serializable;
import java.util.Date;

public class Incorporation implements Serializable {
    private Integer id;

    private String code;

    private String name;

    private String description;

    private Integer adminid;

    private Integer status;

    private String account;

    private String domain;

    private String license;

    private Boolean isDevAutClaf;

    private String token;

    private String codeLicenseMd5;

    private Boolean isBind;

    private String agentDomain;

    private String emailAccount;

    private String emailPassword;

    private String emailHost;

    private String emailPort;

    private Integer emailSsl;

    private String sysName;

    private String licenseUserLimit;

    private Date licenseExpirationTime;

    private String ifBuyEmm;

    private String ifBuyOneshare;

    private String ifBuyIam;

    private String ifBuySms;

    private String disposition;

    private String enterpriseEn;

    private Date freezeTime;

    private String freezingPeriod;

    private String logourl;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Integer getAdminid() {
        return adminid;
    }

    public void setAdminid(Integer adminid) {
        this.adminid = adminid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain == null ? null : domain.trim();
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license == null ? null : license.trim();
    }

    public Boolean getIsDevAutClaf() {
        return isDevAutClaf;
    }

    public void setIsDevAutClaf(Boolean isDevAutClaf) {
        this.isDevAutClaf = isDevAutClaf;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token == null ? null : token.trim();
    }

    public String getCodeLicenseMd5() {
        return codeLicenseMd5;
    }

    public void setCodeLicenseMd5(String codeLicenseMd5) {
        this.codeLicenseMd5 = codeLicenseMd5 == null ? null : codeLicenseMd5.trim();
    }

    public Boolean getIsBind() {
        return isBind;
    }

    public void setIsBind(Boolean isBind) {
        this.isBind = isBind;
    }

    public String getAgentDomain() {
        return agentDomain;
    }

    public void setAgentDomain(String agentDomain) {
        this.agentDomain = agentDomain == null ? null : agentDomain.trim();
    }

    public String getEmailAccount() {
        return emailAccount;
    }

    public void setEmailAccount(String emailAccount) {
        this.emailAccount = emailAccount == null ? null : emailAccount.trim();
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword == null ? null : emailPassword.trim();
    }

    public String getEmailHost() {
        return emailHost;
    }

    public void setEmailHost(String emailHost) {
        this.emailHost = emailHost == null ? null : emailHost.trim();
    }

    public String getEmailPort() {
        return emailPort;
    }

    public void setEmailPort(String emailPort) {
        this.emailPort = emailPort == null ? null : emailPort.trim();
    }

    public Integer getEmailSsl() {
        return emailSsl;
    }

    public void setEmailSsl(Integer emailSsl) {
        this.emailSsl = emailSsl;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName == null ? null : sysName.trim();
    }

    public String getLicenseUserLimit() {
        return licenseUserLimit;
    }

    public void setLicenseUserLimit(String licenseUserLimit) {
        this.licenseUserLimit = licenseUserLimit == null ? null : licenseUserLimit.trim();
    }

    public Date getLicenseExpirationTime() {
        return licenseExpirationTime;
    }

    public void setLicenseExpirationTime(Date licenseExpirationTime) {
        this.licenseExpirationTime = licenseExpirationTime;
    }

    public String getIfBuyEmm() {
        return ifBuyEmm;
    }

    public void setIfBuyEmm(String ifBuyEmm) {
        this.ifBuyEmm = ifBuyEmm == null ? null : ifBuyEmm.trim();
    }

    public String getIfBuyOneshare() {
        return ifBuyOneshare;
    }

    public void setIfBuyOneshare(String ifBuyOneshare) {
        this.ifBuyOneshare = ifBuyOneshare == null ? null : ifBuyOneshare.trim();
    }

    public String getIfBuyIam() {
        return ifBuyIam;
    }

    public void setIfBuyIam(String ifBuyIam) {
        this.ifBuyIam = ifBuyIam == null ? null : ifBuyIam.trim();
    }

    public String getIfBuySms() {
        return ifBuySms;
    }

    public void setIfBuySms(String ifBuySms) {
        this.ifBuySms = ifBuySms == null ? null : ifBuySms.trim();
    }

    public String getDisposition() {
        return disposition;
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition == null ? null : disposition.trim();
    }

    public String getEnterpriseEn() {
        return enterpriseEn;
    }

    public void setEnterpriseEn(String enterpriseEn) {
        this.enterpriseEn = enterpriseEn == null ? null : enterpriseEn.trim();
    }

    public Date getFreezeTime() {
        return freezeTime;
    }

    public void setFreezeTime(Date freezeTime) {
        this.freezeTime = freezeTime;
    }

    public String getFreezingPeriod() {
        return freezingPeriod;
    }

    public void setFreezingPeriod(String freezingPeriod) {
        this.freezingPeriod = freezingPeriod == null ? null : freezingPeriod.trim();
    }

    public String getLogourl() {
        return logourl;
    }

    public void setLogourl(String logourl) {
        this.logourl = logourl == null ? null : logourl.trim();
    }
}