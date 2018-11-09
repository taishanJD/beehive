package com.quarkdata.yunpan.api.model.vo;

import java.util.List;

/**
 * 创建组织空间
 * @author typ
 * 	2017年12月28日
 */
public class CreateOrganizedSpaceVO {
	
	private String documentName;

	private List<DocumentPrivilegeVO> permissions;

	private List<DocumentPrivilegeVOfGenerateAccounts> permissionsOfGenerateAccounts;

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
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
