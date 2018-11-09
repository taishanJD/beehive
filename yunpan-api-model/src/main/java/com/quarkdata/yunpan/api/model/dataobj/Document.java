package com.quarkdata.yunpan.api.model.dataobj;

import java.io.Serializable;
import java.util.Date;

public class Document implements Serializable {
    private Long id;

    private Long incId;

    private Long parentId;

    private Long documentVersionId;

    private String documentName;

    private String type;

    private String documentType;

    private String idPath;

    private Long size;

    private Date createTime;

    private Long createUser;

    private String createUsername;

    private Date updateTime;

    private Long updateUser;

    private String updateUsername;

    private String isShare;

    private String description;

    private String isDelete;

    private String isLock;

    private Date lockTime;

    private Long lockUser;

    private String lockUsername;

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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getDocumentVersionId() {
        return documentVersionId;
    }

    public void setDocumentVersionId(Long documentVersionId) {
        this.documentVersionId = documentVersionId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName == null ? null : documentName.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType == null ? null : documentType.trim();
    }

    public String getIdPath() {
        return idPath;
    }

    public void setIdPath(String idPath) {
        this.idPath = idPath == null ? null : idPath.trim();
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Long createUser) {
        this.createUser = createUser;
    }

    public String getCreateUsername() {
        return createUsername;
    }

    public void setCreateUsername(String createUsername) {
        this.createUsername = createUsername == null ? null : createUsername.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Long updateUser) {
        this.updateUser = updateUser;
    }

    public String getUpdateUsername() {
        return updateUsername;
    }

    public void setUpdateUsername(String updateUsername) {
        this.updateUsername = updateUsername == null ? null : updateUsername.trim();
    }

    public String getIsShare() {
        return isShare;
    }

    public void setIsShare(String isShare) {
        this.isShare = isShare == null ? null : isShare.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete == null ? null : isDelete.trim();
    }

    public String getIsLock() {
        return isLock;
    }

    public void setIsLock(String isLock) {
        this.isLock = isLock == null ? null : isLock.trim();
    }

    public Date getLockTime() {
        return lockTime;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }

    public Long getLockUser() {
        return lockUser;
    }

    public void setLockUser(Long lockUser) {
        this.lockUser = lockUser;
    }

    public String getLockUsername() {
        return lockUsername;
    }

    public void setLockUsername(String lockUsername) {
        this.lockUsername = lockUsername == null ? null : lockUsername.trim();
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
        Document other = (Document) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getIncId() == null ? other.getIncId() == null : this.getIncId().equals(other.getIncId()))
            && (this.getParentId() == null ? other.getParentId() == null : this.getParentId().equals(other.getParentId()))
            && (this.getDocumentVersionId() == null ? other.getDocumentVersionId() == null : this.getDocumentVersionId().equals(other.getDocumentVersionId()))
            && (this.getDocumentName() == null ? other.getDocumentName() == null : this.getDocumentName().equals(other.getDocumentName()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getDocumentType() == null ? other.getDocumentType() == null : this.getDocumentType().equals(other.getDocumentType()))
            && (this.getIdPath() == null ? other.getIdPath() == null : this.getIdPath().equals(other.getIdPath()))
            && (this.getSize() == null ? other.getSize() == null : this.getSize().equals(other.getSize()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getCreateUser() == null ? other.getCreateUser() == null : this.getCreateUser().equals(other.getCreateUser()))
            && (this.getCreateUsername() == null ? other.getCreateUsername() == null : this.getCreateUsername().equals(other.getCreateUsername()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getUpdateUser() == null ? other.getUpdateUser() == null : this.getUpdateUser().equals(other.getUpdateUser()))
            && (this.getUpdateUsername() == null ? other.getUpdateUsername() == null : this.getUpdateUsername().equals(other.getUpdateUsername()))
            && (this.getIsShare() == null ? other.getIsShare() == null : this.getIsShare().equals(other.getIsShare()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()))
            && (this.getIsLock() == null ? other.getIsLock() == null : this.getIsLock().equals(other.getIsLock()))
            && (this.getLockTime() == null ? other.getLockTime() == null : this.getLockTime().equals(other.getLockTime()))
            && (this.getLockUser() == null ? other.getLockUser() == null : this.getLockUser().equals(other.getLockUser()))
            && (this.getLockUsername() == null ? other.getLockUsername() == null : this.getLockUsername().equals(other.getLockUsername()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getIncId() == null) ? 0 : getIncId().hashCode());
        result = prime * result + ((getParentId() == null) ? 0 : getParentId().hashCode());
        result = prime * result + ((getDocumentVersionId() == null) ? 0 : getDocumentVersionId().hashCode());
        result = prime * result + ((getDocumentName() == null) ? 0 : getDocumentName().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getDocumentType() == null) ? 0 : getDocumentType().hashCode());
        result = prime * result + ((getIdPath() == null) ? 0 : getIdPath().hashCode());
        result = prime * result + ((getSize() == null) ? 0 : getSize().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getCreateUser() == null) ? 0 : getCreateUser().hashCode());
        result = prime * result + ((getCreateUsername() == null) ? 0 : getCreateUsername().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getUpdateUser() == null) ? 0 : getUpdateUser().hashCode());
        result = prime * result + ((getUpdateUsername() == null) ? 0 : getUpdateUsername().hashCode());
        result = prime * result + ((getIsShare() == null) ? 0 : getIsShare().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        result = prime * result + ((getIsLock() == null) ? 0 : getIsLock().hashCode());
        result = prime * result + ((getLockTime() == null) ? 0 : getLockTime().hashCode());
        result = prime * result + ((getLockUser() == null) ? 0 : getLockUser().hashCode());
        result = prime * result + ((getLockUsername() == null) ? 0 : getLockUsername().hashCode());
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
        sb.append(", parentId=").append(parentId);
        sb.append(", documentVersionId=").append(documentVersionId);
        sb.append(", documentName=").append(documentName);
        sb.append(", type=").append(type);
        sb.append(", documentType=").append(documentType);
        sb.append(", idPath=").append(idPath);
        sb.append(", size=").append(size);
        sb.append(", createTime=").append(createTime);
        sb.append(", createUser=").append(createUser);
        sb.append(", createUsername=").append(createUsername);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", updateUser=").append(updateUser);
        sb.append(", updateUsername=").append(updateUsername);
        sb.append(", isShare=").append(isShare);
        sb.append(", description=").append(description);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", isLock=").append(isLock);
        sb.append(", lockTime=").append(lockTime);
        sb.append(", lockUser=").append(lockUser);
        sb.append(", lockUsername=").append(lockUsername);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}