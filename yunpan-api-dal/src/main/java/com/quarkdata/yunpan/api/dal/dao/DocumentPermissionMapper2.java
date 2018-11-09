package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.dataobj.DocumentPermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DocumentPermissionMapper2 {
	List<DocumentPermission> getPermission(@Param("userId") Integer userId, @Param("groupIds") List<Integer> groupIds,
			@Param("departmentId") Integer departmentId, @Param("incId") Integer incId, @Param("docId") Long docId);

	void deleteByDocumentIdList(List<Long> docIdList);

	String getRecentPermission(@Param("docIds") String[] docIds, @Param("userId") Long userId,
			@Param("userGroupIds") List<Long> userGroupIds, @Param("deptId") Long deptId);

	/**
	 * 根据doc_permission表中receiver_id查询文档名称集合
	 * 
	 * @author YYQ
	 * @param incId
	 * @param oldOrgAdminId
	 * @return
	 */
	List<String> selectDocToBeTransferPermission(@Param("incId")Long incId, @Param("oldOrgAdminId")Long oldOrgAdminId);

	/**
	 * 根据接收者id删除权限对象
	 * @param receiveId
	 */
	void deleteByReceiveId(Long receiveId);

	DocumentPermission selectByDidAndReceiveType(@Param("dId") Long dId,@Param("type") String type);

	/**
	 * 获取文档权限
	 * @param documentId
	 * @param receiverId
	 * @return
	 */
	List<String> getPermissionType(@Param("documentId") Long documentId, @Param("receiverId") Long receiverId);

	/**
	 * 根据接收者id和接收者类型查询文档权限
	 * @param receiveId
	 * @param type
	 * @return
	 */
	DocumentPermission selectByRidAndReceiveType(@Param("receiveId") Long receiveId,@Param("type") String type);

	List<DocumentPermission> getDocumentMaxPermission(@Param("docIds") String[] docIds, @Param("receiverId") Long receiverId,
													  @Param("receiver_type") String receiver_type);

	/**
	 * 给子文件赋权限(非单独设置过权限的)
	 * @param incId
	 * @param documentId
	 * @param idPath
	 * @param receiverType
	 * @param receiverId
	 * @param permission
	 */
    void setChildPermission(@Param("incId")Long incId,
							@Param("documentId")Long documentId,
							@Param("idPath")String idPath,
							@Param("receiverType")String receiverType,
							@Param("receiverId")Long receiverId,
							@Param("permission")String permission);

	/**
	 * 设置权限前删除所有非单独设置的权限数据
     * @param incId
     * @param documentId
     * @param idPath
     * @param creatorPermission
     */
	void deleteChildPermission(@Param("incId") Long incId,
                               @Param("documentId") Long documentId,
                               @Param("idPath") String idPath,
                               @Param("associatedVisiblePermission") String associatedVisiblePermission,
							   @Param("creatorPermission") String creatorPermission);

	/**
	 * 给父文件夹设置关联可见权限
	 * @param incId
	 * @param groupIds
	 * @param deptId
	 * @param documentId
	 * @param receiverType
	 * @param receiverId
	 * @param associatedVisiblePermission
	 */
	void setFatherPermission(@Param("incId")Long incId,
							 @Param("groupIds")List<Long> groupIds,
							 @Param("deptId")Long deptId,
							 @Param("documentId")long documentId,
							 @Param("receiverType")String receiverType,
							 @Param("receiverId")Long receiverId,
							 @Param("associatedVisiblePermission")String associatedVisiblePermission);

	/**
	 * 查找是否拥有指定权限
	 * @param incId
	 * @param userId
	 * @param groupIds
	 * @param deptId
	 * @param docId
	 * @param permissionIndex 权限索引位置
	 * @return
	 */
    String hasPermission(@Param("incId") Long incId,
						 @Param("userId") Long userId,
						 @Param("groupIds") List<Long> groupIds,
						 @Param("deptId") Long deptId,
						 @Param("documentId") Long docId,
						 @Param("permissionIndex") String permissionIndex);

	int insert(DocumentPermission record);

	/**
	 * 根据文档id获取用户拥有的权限
	 * @param docId
	 * @param userId
	 * @param userGroupIds
	 * @param deptId
	 * @return
	 */
    String getPermissionByDocumentId(@Param("docId") Long docId,
									 @Param("incId") Long incId,
									 @Param("userId") Long userId,
									 @Param("groupIds") List<Long> userGroupIds,
									 @Param("deptId") Long deptId);
}