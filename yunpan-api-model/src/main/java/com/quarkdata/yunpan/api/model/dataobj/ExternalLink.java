package com.quarkdata.yunpan.api.model.dataobj;

import java.io.Serializable;
import java.util.Date;

public class ExternalLink implements Serializable {
    private Long id;

    private Long incId;

    private Long documentId;

    private String isEnableExternalLink;

    private String externalLinkType;

    private String externalLinkCode;

    private Date externalLinkExpireTime;

    private Integer viewCount;

    private Integer downloadCount;

    private Long createUser;

    private Date createTime;

    private Long updateUser;

    private Date updateTime;

    private String createUsername;

    private String allowPreview;

    private String allowDownload;

    private String isSecret;

    private String fetchCode;

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

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getIsEnableExternalLink() {
        return isEnableExternalLink;
    }

    public void setIsEnableExternalLink(String isEnableExternalLink) {
        this.isEnableExternalLink = isEnableExternalLink == null ? null : isEnableExternalLink.trim();
    }

    public String getExternalLinkType() {
        return externalLinkType;
    }

    public void setExternalLinkType(String externalLinkType) {
        this.externalLinkType = externalLinkType == null ? null : externalLinkType.trim();
    }

    public String getExternalLinkCode() {
        return externalLinkCode;
    }

    public void setExternalLinkCode(String externalLinkCode) {
        this.externalLinkCode = externalLinkCode == null ? null : externalLinkCode.trim();
    }

    public Date getExternalLinkExpireTime() {
        return externalLinkExpireTime;
    }

    public void setExternalLinkExpireTime(Date externalLinkExpireTime) {
        this.externalLinkExpireTime = externalLinkExpireTime;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Long getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Long createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Long updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateUsername() {
        return createUsername;
    }

    public void setCreateUsername(String createUsername) {
        this.createUsername = createUsername == null ? null : createUsername.trim();
    }

    public String getAllowPreview() {
        return allowPreview;
    }

    public void setAllowPreview(String allowPreview) {
        this.allowPreview = allowPreview == null ? null : allowPreview.trim();
    }

    public String getAllowDownload() {
        return allowDownload;
    }

    public void setAllowDownload(String allowDownload) {
        this.allowDownload = allowDownload == null ? null : allowDownload.trim();
    }

    public String getIsSecret() {
        return isSecret;
    }

    public void setIsSecret(String isSecret) {
        this.isSecret = isSecret == null ? null : isSecret.trim();
    }

    public String getFetchCode() {
        return fetchCode;
    }

    public void setFetchCode(String fetchCode) {
        this.fetchCode = fetchCode == null ? null : fetchCode.trim();
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
        ExternalLink other = (ExternalLink) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getIncId() == null ? other.getIncId() == null : this.getIncId().equals(other.getIncId()))
            && (this.getDocumentId() == null ? other.getDocumentId() == null : this.getDocumentId().equals(other.getDocumentId()))
            && (this.getIsEnableExternalLink() == null ? other.getIsEnableExternalLink() == null : this.getIsEnableExternalLink().equals(other.getIsEnableExternalLink()))
            && (this.getExternalLinkType() == null ? other.getExternalLinkType() == null : this.getExternalLinkType().equals(other.getExternalLinkType()))
            && (this.getExternalLinkCode() == null ? other.getExternalLinkCode() == null : this.getExternalLinkCode().equals(other.getExternalLinkCode()))
            && (this.getExternalLinkExpireTime() == null ? other.getExternalLinkExpireTime() == null : this.getExternalLinkExpireTime().equals(other.getExternalLinkExpireTime()))
            && (this.getViewCount() == null ? other.getViewCount() == null : this.getViewCount().equals(other.getViewCount()))
            && (this.getDownloadCount() == null ? other.getDownloadCount() == null : this.getDownloadCount().equals(other.getDownloadCount()))
            && (this.getCreateUser() == null ? other.getCreateUser() == null : this.getCreateUser().equals(other.getCreateUser()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateUser() == null ? other.getUpdateUser() == null : this.getUpdateUser().equals(other.getUpdateUser()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getCreateUsername() == null ? other.getCreateUsername() == null : this.getCreateUsername().equals(other.getCreateUsername()))
            && (this.getAllowPreview() == null ? other.getAllowPreview() == null : this.getAllowPreview().equals(other.getAllowPreview()))
            && (this.getAllowDownload() == null ? other.getAllowDownload() == null : this.getAllowDownload().equals(other.getAllowDownload()))
            && (this.getIsSecret() == null ? other.getIsSecret() == null : this.getIsSecret().equals(other.getIsSecret()))
            && (this.getFetchCode() == null ? other.getFetchCode() == null : this.getFetchCode().equals(other.getFetchCode()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getIncId() == null) ? 0 : getIncId().hashCode());
        result = prime * result + ((getDocumentId() == null) ? 0 : getDocumentId().hashCode());
        result = prime * result + ((getIsEnableExternalLink() == null) ? 0 : getIsEnableExternalLink().hashCode());
        result = prime * result + ((getExternalLinkType() == null) ? 0 : getExternalLinkType().hashCode());
        result = prime * result + ((getExternalLinkCode() == null) ? 0 : getExternalLinkCode().hashCode());
        result = prime * result + ((getExternalLinkExpireTime() == null) ? 0 : getExternalLinkExpireTime().hashCode());
        result = prime * result + ((getViewCount() == null) ? 0 : getViewCount().hashCode());
        result = prime * result + ((getDownloadCount() == null) ? 0 : getDownloadCount().hashCode());
        result = prime * result + ((getCreateUser() == null) ? 0 : getCreateUser().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateUser() == null) ? 0 : getUpdateUser().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getCreateUsername() == null) ? 0 : getCreateUsername().hashCode());
        result = prime * result + ((getAllowPreview() == null) ? 0 : getAllowPreview().hashCode());
        result = prime * result + ((getAllowDownload() == null) ? 0 : getAllowDownload().hashCode());
        result = prime * result + ((getIsSecret() == null) ? 0 : getIsSecret().hashCode());
        result = prime * result + ((getFetchCode() == null) ? 0 : getFetchCode().hashCode());
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
        sb.append(", documentId=").append(documentId);
        sb.append(", isEnableExternalLink=").append(isEnableExternalLink);
        sb.append(", externalLinkType=").append(externalLinkType);
        sb.append(", externalLinkCode=").append(externalLinkCode);
        sb.append(", externalLinkExpireTime=").append(externalLinkExpireTime);
        sb.append(", viewCount=").append(viewCount);
        sb.append(", downloadCount=").append(downloadCount);
        sb.append(", createUser=").append(createUser);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateUser=").append(updateUser);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", createUsername=").append(createUsername);
        sb.append(", allowPreview=").append(allowPreview);
        sb.append(", allowDownload=").append(allowDownload);
        sb.append(", isSecret=").append(isSecret);
        sb.append(", fetchCode=").append(fetchCode);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}