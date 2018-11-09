package com.quarkdata.yunpan.api.service;

import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.common.ResultTwoDataCode;
import com.quarkdata.yunpan.api.model.common.YunPanApiException;
import com.quarkdata.yunpan.api.model.vo.DocumentExtend;
import com.quarkdata.yunpan.api.model.vo.DocumentPermissionVO;
import com.quarkdata.yunpan.api.model.vo.DocumentPrivilegeVO;
import com.quarkdata.yunpan.api.model.vo.DocumentPrivilegeVOfGenerateAccounts;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Created by yanyq1129@thundersoft.com on 2018/9/10.
 */
public interface OrgFileService {


    /**
     * @param request
     * @param file
     * @param fileName         如果不为空，则以此文件名为准，如果为空，则以file对象中的文件名为准
     * @param isOverwrite      是否覆盖，1-是：版本+1,0-否：文件名重复则返回提示信息
     * @param parentId
     * @param userId
     * @param userName
     * @param incId
     * @param deptId
     * @param groupIds
     * @param isOrganizedAdmin
     * @return
     */
    ResultCode<DocumentExtend> uploadFile(HttpServletRequest request, MultipartFile file, String fileName, int isOverwrite, Long parentId, Long userId, String userName, Long incId, Integer deptId, List<Long> groupIds, boolean isOrganizedAdmin) throws IOException, YunPanApiException;

    /**
     * App上传组织文件
     *
     * @param request
     * @param file
     * @param fileName         如果不为空，则以此文件名为准，如果为空，则以file对象中的文件名为准
     * @param isOverwrite      是否覆盖，1-是：版本+1,0-否：文件名重复则返回提示信息
     * @param parentId
     * @param userId
     * @param userName
     * @param incId
     * @param deptId
     * @param groupIds
     * @param isOrganizedAdmin
     * @return
     */
    ResultCode<DocumentExtend> uploadFileApp(HttpServletRequest request, MultipartFile file, String fileName, int isOverwrite, Long parentId, Long userId, String userName, Long incId, Integer deptId, List<Long> groupIds, boolean isOrganizedAdmin) throws IOException, YunPanApiException;

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
    ResultCode<String> setDocumentPrivilege(HttpServletRequest request, Long documentId, List<DocumentPrivilegeVO> permissions, List<DocumentPrivilegeVOfGenerateAccounts> permissionsOfGenerateAccounts, Long incId, boolean isOrganizedAdmin, Long userId, List<Long> groupIds, Long deptId) throws YunPanApiException;

    /**
     * 创建组织空间/组织文件夹
     *
     * @param request
     * @param documentName
     * @param permissions
     * @param permissionsOfGenerateAccounts
     * @param userId
     * @param userName
     * @param incId
     * @param isAdmin
     * @param groupIds
     * @param deptId
     * @return
     */
    ResultCode<DocumentExtend> createOrganizedDir(HttpServletRequest request, Long parentId, String documentName, List<DocumentPrivilegeVO> permissions, List<DocumentPrivilegeVOfGenerateAccounts> permissionsOfGenerateAccounts, Long userId, String userName, Long incId, boolean isAdmin, List<Long> groupIds, Long deptId) throws YunPanApiException;

    /**
     * 获取文档权限
     *
     * @param docId
     * @param incId
     * @return
     */
    ResultTwoDataCode<List<DocumentPermissionVO>, List<Long>> getDocumentPermissions(Long docId, Long incId);

    ResultCode<String> getFileCount(Long incId, Long userId, List<Long> userGroupIds, Long deptId);

    ResultCode<List<DocumentExtend>> getAllChildren(Long incId, Long userId, List<Long> userGroupIds, Long deptId, Long parentId);

    /**
     * 根据文档id批量删除组织文件
     *
     * @param incId
     * @param userId
     * @param userGroupIds
     * @param deptId
     * @param docIds
     * @param isOrganizedAdmin
     * @return
     */
    Boolean delDocBatch(Long incId, Long userId, String userName, List<Long> userGroupIds, Long deptId, String docIds, boolean isOrganizedAdmin);

    List<DocumentExtend> getPermission(Long incId, Long userId, List<Long> userGroupIds, Long deptId,
                                       boolean isOrganizedAdmin, List<DocumentExtend> documents, String parentPath);

    /**
     * 锁定组织文档
     *
     * @param request
     * @param documentIds
     * @return
     */
    ResultCode<String> lockOrgDocument(HttpServletRequest request, String documentIds);

    /**
     * 解锁组织文档
     *
     * @param request
     * @param documentIds
     * @return
     */
    ResultCode<String> unLockOrgDocument(HttpServletRequest request, String documentIds);

    /**
     * 查询组织文件列表
     *
     * @param incId
     * @param userId
     * @param groupIds
     * @param deptId
     * @param systemAdmin
     * @param parentId
     * @param documentName
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResultCode<List<DocumentExtend>> list(Long incId, Long userId, List<Long> groupIds, Long deptId, boolean systemAdmin, Long parentId, String documentName, Integer pageNum, Integer pageSize);

    List<DocumentExtend> getDirectoryThatHasUploadPermission(Long incId, Long userId, List<Long> groupIds, Long deptId, boolean isSystemAdmin, Long parentId, String documentName);
}
