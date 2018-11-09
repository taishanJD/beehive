package com.quarkdata.yunpan.api.model.vo;

import java.util.List;

public class SetDocumentPrivilegeVO {
	
	private Long documentId;

	private List<DocumentPrivilegeVO> permissions;

	private List<DocumentPrivilegeVOfGenerateAccounts> permissionsOfGenerateAccounts;

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public List<DocumentPrivilegeVO> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<DocumentPrivilegeVO> permissions) {
		this.permissions = permissions;
	}

	public List<DocumentPrivilegeVOfGenerateAccounts> getPermissionsOfGenerateAccounts() {
		return permissionsOfGenerateAccounts;
	}

	public void setPermissionsOfGenerateAccounts(List<DocumentPrivilegeVOfGenerateAccounts> permissionsOfGenerateAccounts) {
		this.permissionsOfGenerateAccounts = permissionsOfGenerateAccounts;
	}
}
