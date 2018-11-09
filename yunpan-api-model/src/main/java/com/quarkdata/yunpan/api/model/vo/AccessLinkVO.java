package com.quarkdata.yunpan.api.model.vo;

import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.dataobj.Tag;

import java.util.Date;
import java.util.List;

/**
 * Document对象扩展
 * 	携带过期

 */
public class AccessLinkVO extends Document{

	public String getDisplayTime() {
		return displayTime;
	}

	public void setDisplayTime(String displayTime) {
		this.displayTime = displayTime;
	}

	@Override
	public Date getCreateTime() {
		return createTime;
	}

	@Override
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	private String  displayTime;
	private Date createTime;

	public String getLinkType() {
		return linkType;
	}

	public AccessLinkVO setLinkType(String linkType) {
		this.linkType = linkType;
		return this;
	}

	private String linkType;

	public String getLinkCode() {
		return linkCode;
	}

	public AccessLinkVO setLinkCode(String linkCode) {
		this.linkCode = linkCode;
		return this;
	}

	private String linkCode;
	public AccessLinkVO(Document document) {
		super.setId(document.getId());
		super.setIncId(document.getIncId());
		super.setSize(document.getSize());
		super.setDocumentName(document.getDocumentName());
		super.setDocumentType(document.getDocumentType());
		super.setCreateUsername(document.getCreateUsername());
		super.setIdPath(document.getIdPath());
		super.setType(document.getType());
		super.setDocumentVersionId(document.getDocumentVersionId());
	}
}
