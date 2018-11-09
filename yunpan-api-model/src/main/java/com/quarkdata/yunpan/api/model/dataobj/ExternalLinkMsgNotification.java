package com.quarkdata.yunpan.api.model.dataobj;

import java.io.Serializable;

public class ExternalLinkMsgNotification implements Serializable {
    private Long id;

    private Long linkId;

    private Long incId;

    private String receiverType;

    private String receiverDetail;

    private String isSent;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLinkId() {
        return linkId;
    }

    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }

    public Long getIncId() {
        return incId;
    }

    public void setIncId(Long incId) {
        this.incId = incId;
    }

    public String getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(String receiverType) {
        this.receiverType = receiverType == null ? null : receiverType.trim();
    }

    public String getReceiverDetail() {
        return receiverDetail;
    }

    public void setReceiverDetail(String receiverDetail) {
        this.receiverDetail = receiverDetail == null ? null : receiverDetail.trim();
    }

    public String getIsSent() {
        return isSent;
    }

    public void setIsSent(String isSent) {
        this.isSent = isSent == null ? null : isSent.trim();
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
        ExternalLinkMsgNotification other = (ExternalLinkMsgNotification) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getLinkId() == null ? other.getLinkId() == null : this.getLinkId().equals(other.getLinkId()))
            && (this.getIncId() == null ? other.getIncId() == null : this.getIncId().equals(other.getIncId()))
            && (this.getReceiverType() == null ? other.getReceiverType() == null : this.getReceiverType().equals(other.getReceiverType()))
            && (this.getReceiverDetail() == null ? other.getReceiverDetail() == null : this.getReceiverDetail().equals(other.getReceiverDetail()))
            && (this.getIsSent() == null ? other.getIsSent() == null : this.getIsSent().equals(other.getIsSent()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getLinkId() == null) ? 0 : getLinkId().hashCode());
        result = prime * result + ((getIncId() == null) ? 0 : getIncId().hashCode());
        result = prime * result + ((getReceiverType() == null) ? 0 : getReceiverType().hashCode());
        result = prime * result + ((getReceiverDetail() == null) ? 0 : getReceiverDetail().hashCode());
        result = prime * result + ((getIsSent() == null) ? 0 : getIsSent().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", linkId=").append(linkId);
        sb.append(", incId=").append(incId);
        sb.append(", receiverType=").append(receiverType);
        sb.append(", receiverDetail=").append(receiverDetail);
        sb.append(", isSent=").append(isSent);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}