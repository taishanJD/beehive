package com.quarkdata.yunpan.api.model.dataobj;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

public class IncConfig implements Serializable {
    private Long id;

    private Long incId;

    private String historyVersionType;

    private Integer historyVersionParam;

    private Integer incRatio;

    private Integer userRatio;

    private Integer perUserQuota;

    private Integer incTotalQuota;

    private Date createTime;

    private String isDelete;

    private String linkMan;

    private String telephone;

    private String email;

    private String cephAccessKey;

    private String cephSecretKey;

    private String cephUrl;

    private byte[] logo;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIncId() {
        return incId;
    }

    public void setIncId(Long incId) {
        this.incId = incId;
    }

    public String getHistoryVersionType() {
        return historyVersionType;
    }

    public void setHistoryVersionType(String historyVersionType) {
        this.historyVersionType = historyVersionType == null ? null : historyVersionType.trim();
    }

    public Integer getHistoryVersionParam() {
        return historyVersionParam;
    }

    public void setHistoryVersionParam(Integer historyVersionParam) {
        this.historyVersionParam = historyVersionParam;
    }

    public Integer getIncRatio() {
        return incRatio;
    }

    public void setIncRatio(Integer incRatio) {
        this.incRatio = incRatio;
    }

    public Integer getUserRatio() {
        return userRatio;
    }

    public void setUserRatio(Integer userRatio) {
        this.userRatio = userRatio;
    }

    public Integer getPerUserQuota() {
        return perUserQuota;
    }

    public void setPerUserQuota(Integer perUserQuota) {
        this.perUserQuota = perUserQuota;
    }

    public Integer getIncTotalQuota() {
        return incTotalQuota;
    }

    public void setIncTotalQuota(Integer incTotalQuota) {
        this.incTotalQuota = incTotalQuota;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete == null ? null : isDelete.trim();
    }

    public String getLinkMan() {
        return linkMan;
    }

    public void setLinkMan(String linkMan) {
        this.linkMan = linkMan == null ? null : linkMan.trim();
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone == null ? null : telephone.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getCephAccessKey() {
        return cephAccessKey;
    }

    public void setCephAccessKey(String cephAccessKey) {
        this.cephAccessKey = cephAccessKey == null ? null : cephAccessKey.trim();
    }

    public String getCephSecretKey() {
        return cephSecretKey;
    }

    public void setCephSecretKey(String cephSecretKey) {
        this.cephSecretKey = cephSecretKey == null ? null : cephSecretKey.trim();
    }

    public String getCephUrl() {
        return cephUrl;
    }

    public void setCephUrl(String cephUrl) {
        this.cephUrl = cephUrl == null ? null : cephUrl.trim();
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        IncConfig other = (IncConfig) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getIncId() == null ? other.getIncId() == null : this.getIncId().equals(other.getIncId()))
            && (this.getHistoryVersionType() == null ? other.getHistoryVersionType() == null : this.getHistoryVersionType().equals(other.getHistoryVersionType()))
            && (this.getHistoryVersionParam() == null ? other.getHistoryVersionParam() == null : this.getHistoryVersionParam().equals(other.getHistoryVersionParam()))
            && (this.getIncRatio() == null ? other.getIncRatio() == null : this.getIncRatio().equals(other.getIncRatio()))
            && (this.getUserRatio() == null ? other.getUserRatio() == null : this.getUserRatio().equals(other.getUserRatio()))
            && (this.getPerUserQuota() == null ? other.getPerUserQuota() == null : this.getPerUserQuota().equals(other.getPerUserQuota()))
            && (this.getIncTotalQuota() == null ? other.getIncTotalQuota() == null : this.getIncTotalQuota().equals(other.getIncTotalQuota()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()))
            && (this.getLinkMan() == null ? other.getLinkMan() == null : this.getLinkMan().equals(other.getLinkMan()))
            && (this.getTelephone() == null ? other.getTelephone() == null : this.getTelephone().equals(other.getTelephone()))
            && (this.getEmail() == null ? other.getEmail() == null : this.getEmail().equals(other.getEmail()))
            && (this.getCephAccessKey() == null ? other.getCephAccessKey() == null : this.getCephAccessKey().equals(other.getCephAccessKey()))
            && (this.getCephSecretKey() == null ? other.getCephSecretKey() == null : this.getCephSecretKey().equals(other.getCephSecretKey()))
            && (this.getCephUrl() == null ? other.getCephUrl() == null : this.getCephUrl().equals(other.getCephUrl()))
            && (Arrays.equals(this.getLogo(), other.getLogo()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getIncId() == null) ? 0 : getIncId().hashCode());
        result = prime * result + ((getHistoryVersionType() == null) ? 0 : getHistoryVersionType().hashCode());
        result = prime * result + ((getHistoryVersionParam() == null) ? 0 : getHistoryVersionParam().hashCode());
        result = prime * result + ((getIncRatio() == null) ? 0 : getIncRatio().hashCode());
        result = prime * result + ((getUserRatio() == null) ? 0 : getUserRatio().hashCode());
        result = prime * result + ((getPerUserQuota() == null) ? 0 : getPerUserQuota().hashCode());
        result = prime * result + ((getIncTotalQuota() == null) ? 0 : getIncTotalQuota().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        result = prime * result + ((getLinkMan() == null) ? 0 : getLinkMan().hashCode());
        result = prime * result + ((getTelephone() == null) ? 0 : getTelephone().hashCode());
        result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
        result = prime * result + ((getCephAccessKey() == null) ? 0 : getCephAccessKey().hashCode());
        result = prime * result + ((getCephSecretKey() == null) ? 0 : getCephSecretKey().hashCode());
        result = prime * result + ((getCephUrl() == null) ? 0 : getCephUrl().hashCode());
        result = prime * result + (Arrays.hashCode(getLogo()));
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", incId=").append(incId);
        sb.append(", historyVersionType=").append(historyVersionType);
        sb.append(", historyVersionParam=").append(historyVersionParam);
        sb.append(", incRatio=").append(incRatio);
        sb.append(", userRatio=").append(userRatio);
        sb.append(", perUserQuota=").append(perUserQuota);
        sb.append(", incTotalQuota=").append(incTotalQuota);
        sb.append(", createTime=").append(createTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", linkMan=").append(linkMan);
        sb.append(", telephone=").append(telephone);
        sb.append(", email=").append(email);
        sb.append(", cephAccessKey=").append(cephAccessKey);
        sb.append(", cephSecretKey=").append(cephSecretKey);
        sb.append(", cephUrl=").append(cephUrl);
        sb.append(", logo=").append(logo);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}