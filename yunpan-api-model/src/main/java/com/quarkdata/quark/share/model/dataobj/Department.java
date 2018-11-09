package com.quarkdata.quark.share.model.dataobj;

import java.io.Serializable;
import java.util.Date;

public class Department implements Serializable {
    private Integer id;

    private String name;

    private String des;

    private Integer fatherid;

    private Date createTime;

    private String code;

    private Integer incid;

    private String absCode;

    private Date updateTime;

    private String guid;

    private Integer status;

    private String createAdId;

    private String organize;

    private Integer layer;

    private String hashCode;

    private String dn;

    private String source;

    private String deptNamePath;

    private Integer ruleId;

    private Integer policyId;

    private String fatherGuid;

    private Date lastupdate;

    private String sfFatherid;

    private String sfId;

    private Integer level;

    private String parentrulestatus;

    private String parentstatus;

    private Integer pluginId;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des == null ? null : des.trim();
    }

    public Integer getFatherid() {
        return fatherid;
    }

    public void setFatherid(Integer fatherid) {
        this.fatherid = fatherid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Integer getIncid() {
        return incid;
    }

    public void setIncid(Integer incid) {
        this.incid = incid;
    }

    public String getAbsCode() {
        return absCode;
    }

    public void setAbsCode(String absCode) {
        this.absCode = absCode == null ? null : absCode.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid == null ? null : guid.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Integer getLayer() {
        return layer;
    }

    public void setLayer(Integer layer) {
        this.layer = layer;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source == null ? null : source.trim();
    }

    public String getDeptNamePath() {
        return deptNamePath;
    }

    public void setDeptNamePath(String deptNamePath) {
        this.deptNamePath = deptNamePath == null ? null : deptNamePath.trim();
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Integer policyId) {
        this.policyId = policyId;
    }

    public String getFatherGuid() {
        return fatherGuid;
    }

    public void setFatherGuid(String fatherGuid) {
        this.fatherGuid = fatherGuid == null ? null : fatherGuid.trim();
    }

    public Date getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(Date lastupdate) {
        this.lastupdate = lastupdate;
    }

    public String getSfFatherid() {
        return sfFatherid;
    }

    public void setSfFatherid(String sfFatherid) {
        this.sfFatherid = sfFatherid == null ? null : sfFatherid.trim();
    }

    public String getSfId() {
        return sfId;
    }

    public void setSfId(String sfId) {
        this.sfId = sfId == null ? null : sfId.trim();
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getParentrulestatus() {
        return parentrulestatus;
    }

    public void setParentrulestatus(String parentrulestatus) {
        this.parentrulestatus = parentrulestatus == null ? null : parentrulestatus.trim();
    }

    public String getParentstatus() {
        return parentstatus;
    }

    public void setParentstatus(String parentstatus) {
        this.parentstatus = parentstatus == null ? null : parentstatus.trim();
    }

    public Integer getPluginId() {
        return pluginId;
    }

    public void setPluginId(Integer pluginId) {
        this.pluginId = pluginId;
    }
}