package com.quarkdata.yunpan.api.model.vo;

import java.util.Date;

/**
 * 
 * @author typ
 * 	2017年12月27日
 */
public class DocumentVersionVO {
	private Long id;
	private Integer version;
	private Date updateTime;
	private String updateUsername;
	private Long size;
	private String documentName;
	private Long documentId;
	
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getUpdateUsername() {
		return updateUsername;
	}
	public void setUpdateUsername(String updateUsername) {
		this.updateUsername = updateUsername;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDocumentName() { return this.documentName; }
	public void  setDocumentName(String documentName) { this.documentName = documentName; }
	public  Long getDocumentId(){ return  this.documentId; }
	public  void setDocumentId(Long documentId) { this.documentId = documentId; }
}
