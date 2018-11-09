package com.quarkdata.yunpan.api.model.vo;

import java.util.List;

import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.dataobj.Tag;

/**
 * Document对象扩展
 * 	携带收藏与标签信息
 * 
 * @author typ
 * 	2017年12月12日
 */
public class DocumentExtend extends Document{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean isCol;//是否收藏

	private Long documentId;
	
	private List<Tag> tags;
	
	private String permission;
	
	private boolean isSetPermission; //管理员是否设置过权限

	public boolean isCol() {
		return isCol;
	}

	public void setCol(boolean isCol) {
		this.isCol = isCol;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public boolean isSetPermission() {
		return isSetPermission;
	}

	public void setSetPermission(boolean isSetPermission) {
		this.isSetPermission = isSetPermission;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}
}
