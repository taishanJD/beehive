package com.quarkdata.yunpan.api.service;

import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.common.ResultTwoDataCode;
import com.quarkdata.yunpan.api.model.common.YunPanApiException;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.vo.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * 组织文件
 * 
 * @author typ
 * 	2017年12月12日
 */
public interface OrganizedFileService extends BaseLogService {
	
	/**
	 * 获取组织文件列表
	 * @param incId
	 * @param userId
	 * @param userGroupId
	 * @param deptId
	 * @param parentId
	 * @return
	 */
	List<Document> getOrganizedFiles(Long incId, Long userId, Long userGroupId, Long deptId, Long parentId);
	
	/**
	 * 查询组织文件
	 * 	携带收藏和标签信息
	 * @param incId
	 * @param userId
	 * @param userGroupIds
	 * @param deptId
	 * @param parentId
	 * @param isOrganizedAdmin
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	ResultCode<List<DocumentExtend>> getOrganizedFilesCarryCol_tags(Long incId, Long userId, List<Long> userGroupIds,
																	Long deptId, Long parentId, String documentName, Long isExact, boolean isOrganizedAdmin, Integer pageNum, Integer pageSize);
	
	/**
	 * 
	 *
	 * @param request
	 * @param file
	 * @param fileName    如果不为空，则以此文件名为准，如果为空，则以file对象中的文件名为准
	 * @param isOverwrite    是否覆盖，1-是：版本+1,0-否：文件名重复则返回提示信息
	 * @param parentId
	 * @param userId
	 * @param userName
	 * @param incId
	 * @param deptId
	 * @param groupIds
	 * @param isOrganizedAdmin
	 * @return
	 */
	ResultCode<DocumentExtend> uploadFile(HttpServletRequest request, MultipartFile file, String fileName, int isOverwrite, Long parentId, Long userId, String userName, Long incId, Integer deptId, List<Long> groupIds, boolean isOrganizedAdmin)throws IOException,YunPanApiException;

	/**
	 * App上传组织文件
	 * @param request
	 * @param file
	 * @param fileName    如果不为空，则以此文件名为准，如果为空，则以file对象中的文件名为准
	 * @param isOverwrite    是否覆盖，1-是：版本+1,0-否：文件名重复则返回提示信息
	 * @param parentId
	 * @param userId
	 * @param userName
	 * @param incId
	 * @param deptId
	 * @param groupIds
	 * @param isOrganizedAdmin
	 * @return
	 */
	ResultCode<DocumentExtend> uploadFileApp(HttpServletRequest request, MultipartFile file, String fileName, int isOverwrite, Long parentId, Long userId, String userName, Long incId, Integer deptId, List<Long> groupIds, boolean isOrganizedAdmin)throws IOException,YunPanApiException;

	/**
	 * 创建文件夹
	 *
     * @param request
     * @param documentName
     * @param parentId
     * @param userId
     * @param userName
     * @param incId
     * @param deptId
     * @param groupIds
     * @param isOrganizedAdmin
     * @return
	 */
	ResultCode<DocumentExtend> createDir(HttpServletRequest request, String documentName, Long parentId, Long userId, String userName, Long incId, Integer deptId, List<Long> groupIds,
										 boolean isOrganizedAdmin, List<DocumentPrivilegeVO> permissions, List<DocumentPrivilegeVOfGenerateAccounts> permissionsOfGenerateAccounts)throws YunPanApiException;

	/**
	 * 设置文档权限
	 *
     * @param request
     * @param documentId
     * @param permissions
     * @param permissionsOfGenerateAccounts
	 * @param incId
     * @param isOrganizedAdmin
     * @param userId
     * @param groupIds
     * @param deptId
     * @return
	 * @throws YunPanApiException
	 */
	ResultCode<String> setDocumentPrivilege(HttpServletRequest request, Long documentId, List<DocumentPrivilegeVO> permissions, List<DocumentPrivilegeVOfGenerateAccounts> permissionsOfGenerateAccounts, Long incId, boolean isOrganizedAdmin, Long userId, List<Long> groupIds, Long deptId)throws YunPanApiException;
	
	/**
	 * 创建组织空间
	 *
     * @param request
     * @param documentName
     * @param permissions
     * @param permissionsOfGenerateAccounts
	 * @param userId
     * @param userName
     * @param incId
     * @param isAdmin
     * @return
	 */
	ResultCode<DocumentExtend> createOrganizedSpace(HttpServletRequest request, String documentName, List<DocumentPrivilegeVO> permissions, List<DocumentPrivilegeVOfGenerateAccounts> permissionsOfGenerateAccounts, Long userId, String userName, Long incId, boolean isAdmin)throws YunPanApiException;
	/**
	 * 组织管理员文档列表
	 * @param incId
	 * @param parentId
	 * @param userId
	 * @param documentName
	 * @param pageNum
     * @param pageSize
     * @return
	 */
	ResultCode<List<DocumentExtend>> adminFileList(Long incId, Long parentId, Long userId, String documentName, Long isExact, Integer pageNum, Integer pageSize);
	/**
	 * 获取文档权限
	 * @param docId
	 * @param incId 
	 * @return
	 */
	ResultCode<List<DocumentPermissionVO>> getDocDocumentPermissions(Long docId, Long incId);


	/**
	 * 获取我拥有的文档权限
	 * @param docId
	 * @param userId
	 * @param userGroupIds
	 * @param deptId
	 * @param isAdmin
     * @return
	 */
	ResultCode<Integer> getDocumentPermissionOfMine(Long docId, Long userId, List<Long> userGroupIds, Long deptId, boolean isAdmin);

	/**
	 * 根据parentId获取具有读写权限的组织文件子文件夹目录
	 * @param incId
	 * @param userId
	 * @param userGroupIds
	 * @param deptId
	 * @param parentId
	 * @param documentName
	 * @param isOrganizedAdmin
     * @return
	 */
	ResultCode<List<DocumentZtreeVO>> getGreaterThanWritePrivilegeIncDirectoryByParentId(Long incId, Long userId, List<Long> userGroupIds, Long deptId, Long parentId, String documentName, boolean isOrganizedAdmin);

	ResultCode<String> getFileCount(Long incId, Long userId, List<Long> userGroupIds, Long deptId);

	ResultCode<List<DocumentExtend>> getAllChildren(Long incId, Long userId, List<Long> userGroupIds, Long deptId, Long parentId);

	/**
	 * 根据文档id批量删除组织文件
	 * @param incId
	 * @param userId
	 * @param userGroupIds
	 * @param deptId
	 * @param docIds
	 * @param isOrganizedAdmin
	 * @return
	 */
	Boolean delDocBatch(Long incId, Long userId, String userName, List<Long> userGroupIds, Long deptId, String docIds, boolean isOrganizedAdmin);

	/**
	 * 获取文档权限-刨根问底
	 * @param docId
	 * @param incId
	 * @return
	 */
	ResultTwoDataCode<List<DocumentPermissionVO>, List<Long>> getDocDocumentPermissionsToTheRoot(Long docId, Long incId);

	List<DocumentExtend> getPermission(Long incId, Long userId, List<Long> userGroupIds, Long deptId,
											  boolean isOrganizedAdmin,List<DocumentExtend>documents, String parentPath);

	/**
	 * 锁定组织文档
	 * @param request
	 * @param documentIds
	 * @return
	 */
    ResultCode<String> lockOrgDocument(HttpServletRequest request, String documentIds);

	/**
	 * 解锁组织文档
	 * @param request
	 * @param documentIds
	 * @return
	 */
	ResultCode<String> unLockOrgDocument(HttpServletRequest request, String documentIds);
}
