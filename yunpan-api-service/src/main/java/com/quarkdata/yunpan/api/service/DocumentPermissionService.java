package com.quarkdata.yunpan.api.service;

import com.quarkdata.yunpan.api.model.dataobj.DocumentPermission;

import java.util.List;

/**
 * 
 * @author YYQ 文档权限Service
 *
 */
public interface DocumentPermissionService {

	/**
	 * 获取用户拥有的文档权限
	 * 注1：如果文档没有单独设置过权限，则继承父级的权限
	 * 注2：如果文档的某个父级单独设置了权限且用户没有该父级的权限，则视为用户对文档没权限
	 * @param idPath
	 * @param userId
	 * @param userGroupIds
	 * @param deptId
	 * @return
	 */
	String getPermission(String idPath, Long userId, List<Long> userGroupIds, Long deptId);

	/**
	 * 保存
	 * @param documentPermission
	 */
	void save(DocumentPermission documentPermission);

	/**
	 * 删除
	 * @param receiveId
	 */
	void deleteByReceiveId(Long receiveId);

	DocumentPermission getDocPermissionByRidAndType(Long rId,String type);

	/**
	 * 查询是否有直接权限
	 * @param incId
	 * @param receiverId
	 * @param dpGroupIds
	 * @param deptId
	 * @param docId
	 * @return
	 */
    List<DocumentPermission> getDirectPermission(Long incId, Long receiverId, List<Long> dpGroupIds, Long deptId, Long docId);

	/**
	 * 检查指定权限
	 *
	 * @param docId
	 * @param permission
	 * @return
	 */
    Boolean hasPermission(Long incId, Long userId, List<Long> groupIds, Long deptId, Long docId, String permission);

	/**
	 * 根据文档id查询用户所拥有的权限
	 * @param docId
	 * @param userId
	 * @param userGroupIds
	 * @param deptId
	 * @param systemAdmin
	 * @return
	 */
	String getPermissionByDocumentId(Long docId, Long incId, Long userId, List<Long> userGroupIds, Long deptId, boolean systemAdmin);
}
