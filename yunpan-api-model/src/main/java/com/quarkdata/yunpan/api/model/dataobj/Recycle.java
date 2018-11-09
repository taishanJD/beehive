package com.quarkdata.yunpan.api.model.dataobj;

import java.io.Serializable;
import java.util.Date;

public class Recycle implements Serializable {
    private Long id;

    private Long incId;

    private Long documentId;

    private Long documentVersionId;

    private Long documentParentId;

    private String documentIdPath;

    private Long createUser;

    private Date createTime;

    private String isDelete;

    private String isVisiable;

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

    public Long getDocumentVersionId() {
        return documentVersionId;
    }

    public void setDocumentVersionId(Long documentVersionId) {
        this.documentVersionId = documentVersionId;
    }

    public Long getDocumentParentId() {
        return documentParentId;
    }

    public void setDocumentParentId(Long documentParentId) {
        this.documentParentId = documentParentId;
    }

    public String getDocumentIdPath() {
        return documentIdPath;
    }

    public void setDocumentIdPath(String documentIdPath) {
        this.documentIdPath = documentIdPath == null ? null : documentIdPath.trim();
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

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete == null ? null : isDelete.trim();
    }

    public String getIsVisiable() {
        return isVisiable;
    }

    public void setIsVisiable(String isVisiable) {
        this.isVisiable = isVisiable == null ? null : isVisiable.trim();
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
        Recycle other = (Recycle) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getIncId() == null ? other.getIncId() == null : this.getIncId().equals(other.getIncId()))
            && (this.getDocumentId() == null ? other.getDocumentId() == null : this.getDocumentId().equals(other.getDocumentId()))
            && (this.getDocumentVersionId() == null ? other.getDocumentVersionId() == null : this.getDocumentVersionId().equals(other.getDocumentVersionId()))
            && (this.getDocumentParentId() == null ? other.getDocumentParentId() == null : this.getDocumentParentId().equals(other.getDocumentParentId()))
            && (this.getDocumentIdPath() == null ? other.getDocumentIdPath() == null : this.getDocumentIdPath().equals(other.getDocumentIdPath()))
            && (this.getCreateUser() == null ? other.getCreateUser() == null : this.getCreateUser().equals(other.getCreateUser()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()))
            && (this.getIsVisiable() == null ? other.getIsVisiable() == null : this.getIsVisiable().equals(other.getIsVisiable()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getIncId() == null) ? 0 : getIncId().hashCode());
        result = prime * result + ((getDocumentId() == null) ? 0 : getDocumentId().hashCode());
        result = prime * result + ((getDocumentVersionId() == null) ? 0 : getDocumentVersionId().hashCode());
        result = prime * result + ((getDocumentParentId() == null) ? 0 : getDocumentParentId().hashCode());
        result = prime * result + ((getDocumentIdPath() == null) ? 0 : getDocumentIdPath().hashCode());
        result = prime * result + ((getCreateUser() == null) ? 0 : getCreateUser().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        result = prime * result + ((getIsVisiable() == null) ? 0 : getIsVisiable().hashCode());
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
        sb.append(", documentVersionId=").append(documentVersionId);
        sb.append(", documentParentId=").append(documentParentId);
        sb.append(", documentIdPath=").append(documentIdPath);
        sb.append(", createUser=").append(createUser);
        sb.append(", createTime=").append(createTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", isVisiable=").append(isVisiable);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}