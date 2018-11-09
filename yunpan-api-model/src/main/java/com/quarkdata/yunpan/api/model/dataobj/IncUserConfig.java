package com.quarkdata.yunpan.api.model.dataobj;

import java.io.Serializable;

public class IncUserConfig implements Serializable {
    private Long id;

    private Long incId;

    private Long userId;

    private String isReceiveMessage;

    private String isReceiveEmailMessage;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getIsReceiveMessage() {
        return isReceiveMessage;
    }

    public void setIsReceiveMessage(String isReceiveMessage) {
        this.isReceiveMessage = isReceiveMessage == null ? null : isReceiveMessage.trim();
    }

    public String getIsReceiveEmailMessage() {
        return isReceiveEmailMessage;
    }

    public void setIsReceiveEmailMessage(String isReceiveEmailMessage) {
        this.isReceiveEmailMessage = isReceiveEmailMessage == null ? null : isReceiveEmailMessage.trim();
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
        IncUserConfig other = (IncUserConfig) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getIncId() == null ? other.getIncId() == null : this.getIncId().equals(other.getIncId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getIsReceiveMessage() == null ? other.getIsReceiveMessage() == null : this.getIsReceiveMessage().equals(other.getIsReceiveMessage()))
            && (this.getIsReceiveEmailMessage() == null ? other.getIsReceiveEmailMessage() == null : this.getIsReceiveEmailMessage().equals(other.getIsReceiveEmailMessage()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getIncId() == null) ? 0 : getIncId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getIsReceiveMessage() == null) ? 0 : getIsReceiveMessage().hashCode());
        result = prime * result + ((getIsReceiveEmailMessage() == null) ? 0 : getIsReceiveEmailMessage().hashCode());
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
        sb.append(", userId=").append(userId);
        sb.append(", isReceiveMessage=").append(isReceiveMessage);
        sb.append(", isReceiveEmailMessage=").append(isReceiveEmailMessage);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}