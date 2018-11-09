package com.quarkdata.yunpan.api.model.vo;

import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.dataobj.DocumentPermission;
import com.quarkdata.yunpan.api.model.dataobj.Tag;

import java.util.List;

public class CollectionVO extends Document{
	
	private static final long serialVersionUID = 1L;
	
	private DocumentPermission documentPermission;

	private Long documentId;
	
	private List<Tag> tags;

	public DocumentPermission getDocumentPermission() {
		return documentPermission;
	} 
	
	public void setDocumentPermission(DocumentPermission documentPermission) {
		this.documentPermission = documentPermission;
	}
	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}
}
