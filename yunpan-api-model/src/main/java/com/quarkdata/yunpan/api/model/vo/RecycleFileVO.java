package com.quarkdata.yunpan.api.model.vo;

import java.util.Date;

public class RecycleFileVO  {
	
	private long id;
	private String documentName;
	private long size;
	private String documentType;
	private String deleteTime;
	private Date deleteTimeStamp;
	private int remainTime;
	private String type;
	private String idPath;
	private Long parentId;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getDeleteTime() {
		return deleteTime;
	}

	public void setDeleteTime(String deleteTime) {
		this.deleteTime = deleteTime;
	}
	
	public int getRemainTime() {
		return remainTime;
	}

	public void setRemainTime(Integer remainTime) {
		this.remainTime = remainTime;
	}

	public String getIdPath() {
		return idPath;
	}

	public void setIdPath(String idPath) {
		this.idPath = idPath;
	}

	public Date getDeleteTimeStamp() {
		return deleteTimeStamp;
	}

	public void setDeleteTimeStamp(Date deleteTimeStamp) {
		this.deleteTimeStamp = deleteTimeStamp;
	}
}
