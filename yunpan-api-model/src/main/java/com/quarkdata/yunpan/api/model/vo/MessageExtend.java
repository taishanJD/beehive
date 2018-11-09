package com.quarkdata.yunpan.api.model.vo;

import java.util.Date;

public class MessageExtend {
    private Long id;

    private Long incId;

    private String message;

    private String createUsername;

    private Date createTime;

    private Long documentId;

    private String documentName;

    private Integer messageType;

    private String isRead;

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreateUsername() {
        return createUsername;
    }

    public void setCreateUsername(String createUsername) {
        this.createUsername = createUsername;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

	/**
	 * @return the isRead
	 */
	public String getIsRead() {
		return isRead;
	}

	/**
	 * @param isRead the isRead to set
	 */
	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}

	/**
	 * @return the messageType
	 */
	public Integer getMessageType() {
		return messageType;
	}

	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MessageExtend [id=");
		builder.append(id);
		builder.append(", incId=");
		builder.append(incId);
		builder.append(", message=");
		builder.append(message);
		builder.append(", createUsername=");
		builder.append(createUsername);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", documentId=");
		builder.append(documentId);
		builder.append(", documentName=");
		builder.append(documentName);
		builder.append(", messageType=");
		builder.append(messageType);
		builder.append(", isRead=");
		builder.append(isRead);
		builder.append("]");
		return builder.toString();
	}

    
    
//    @Override
//    public String toString() {
//        return "MessageExtend{" +
//                "id=" + id +
//                ", incId=" + incId +
//                ", message='" + message + '\'' +
//                ", createUsername='" + createUsername + '\'' +
//                ", createTime='" + createTime + '\'' +
//                ", documentId=" + documentId +
//                ", documentName=" + documentName +
//                '}';
//    }
}
