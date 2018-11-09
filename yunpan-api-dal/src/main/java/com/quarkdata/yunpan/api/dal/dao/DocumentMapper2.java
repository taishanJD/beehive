package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.quark.share.model.vo.UserRankVO;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.dataobj.DocumentPermission;
import com.quarkdata.yunpan.api.model.dataobj.ExternalLink;
import com.quarkdata.yunpan.api.model.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author typ 2017年12月12日
 */
public interface DocumentMapper2 {

    /**
     * 查询组织文件
     *
     * @param incId
     * @param userId
     * @param userGroupId
     * @param deptId
     * @param parentId
     * @return
     */
    List<Document> getOrganizedFiles(@Param("incId") Long incId,
                                     @Param("userId") Long userId,
                                     @Param("userGroupId") Long userGroupId,
                                     @Param("deptId") Long deptId,
                                     @Param("parentId") Long parentId);

    /**
     * 查询组织文件 携带收藏和标签信息
     *
     * @param incId
     * @param userId
     * @param userGroupIds
     * @param deptId
     * @param parentId
     * @return
     */
    List<DocumentExtend> getOrganizedFilesCarryCol_tags(@Param("incId") Long incId,
                                                        @Param("userId") Long userId,
                                                        @Param("userGroupIds") List<Long> userGroupIds,
                                                        @Param("deptId") Long deptId,
                                                        @Param("parentId") Long parentId,
                                                        @Param("documentName") String documentName);

    /**
     * 查询组织文件 携带收藏和标签信息
     *
     * @param incId
     * @param userId
     * @param userGroupIds
     * @param deptId
     * @param parentId
     * @return
     */
    List<DocumentExtend> getOrganizedAllFilesCarryCol_tags(@Param("incId") Long incId,
                                                           @Param("userId") Long userId,
                                                           @Param("userGroupIds") List<Long> userGroupIds,
                                                           @Param("deptId") Long deptId,
                                                           @Param("parentId") Long parentId,
                                                           @Param("documentName") String documentName,
                                                           @Param("parentPath") String parentPath,
                                                           @Param("isExact") Long isExact,
                                                           @Param("startNum") Integer startNum,
                                                           @Param("pageSize") Integer pageSize);


    /**
     * 查询共享空间文件 携带收藏和标签信息
     *
     * @param incId
     * @param userId
     * @param userGroupIds
     * @param deptId
     * @param parentId
     * @return
     */
    List<DocumentExtend> getExternalSpaceFilesCarryCol_tags(@Param("incId") Long incId,
                                                            @Param("userId") Long userId,
                                                            @Param("userGroupIds") List<Long> userGroupIds,
                                                            @Param("deptId") Long deptId,
                                                            @Param("parentId") Long parentId,
                                                            @Param("documentName") String documentName,
                                                            @Param("parentPath") String parentPath,
                                                            @Param("isExact") Long isExact);


    /**
     * 检查上传权限 大于0表示有权限，0表示没有权限
     *
     * @param userId
     * @param userGroupIds
     * @param deptId
     * @param dirId        上传到的目录
     * @return
     */
    long checkUploadPrivilege(@Param("userId") Long userId,
                              @Param("userGroupIds") List<Long> userGroupIds,
                              @Param("deptId") Long deptId,
                              @Param("dirId") Long dirId);

    /**
     * 查询个人文件 携带收藏和标签信息
     *
     * @param incId
     * @param userId
     * @param parentId
     * @param startNum
     * @param pageSize
     * @return
     */
    List<DocumentExtend> getPersonalFilesCarryCol_tags(@Param("incId") Long incId,
                                                       @Param("userId") Long userId,
                                                       @Param("parentId") Long parentId,
                                                       @Param("documentName") String documentName,
                                                       @Param("isExact") Long isExact,
                                                       @Param("startNum") Integer startNum,
                                                       @Param("pageSize") Integer pageSize);

    /**
     * 查询个人文件 携带收藏和标签信息----带条件
     *
     * @param incId
     * @param userId
     * @param parentId
     * @param startNum
     * @param pageSize
     * @return
     */
    List<DocumentExtend> getPersonalFilesByDocName(@Param("incId") Long incId,
                                                   @Param("userId") Long userId,
                                                   @Param("parentId") Long parentId,
                                                   @Param("documentName") String documentName,
                                                   @Param("parentPath") String parentPath,
                                                   @Param("startNum") Integer startNum,
                                                   @Param("pageSize") Integer pageSize);


    /**
     * 系统管理员文件列表
     *
     * @param incId
     * @param parentId
     * @param documentName
     * @param startNum
     * @param pageSize
     * @return
     */
    List<DocumentExtend> getSystemAdminFileList(@Param("incId") Long incId,
                                                @Param("userId") Long userId,
                                                @Param("parentId") Long parentId,
                                                @Param("parentPath") String parentPath,
                                                @Param("documentName") String documentName,
                                                @Param("isExact") Long isExact,
                                                @Param("startNum") Integer startNum,
                                                @Param("pageSize") Integer pageSize);

    /**
     * 系统管理员文件列表
     *
     * @param incId
     * @param parentId
     * @param documentName
     * @param startNum
     * @param pageSize
     * @return
     */
    List<DocumentExtend> getAdminFileList(@Param("incId") Long incId,
                                                @Param("userId") Long userId,
                                                @Param("parentId") Long parentId,
                                                @Param("parentPath") String parentPath,
                                                @Param("documentName") String documentName,
                                                @Param("isExact") Long isExact,
                                                @Param("startNum") Integer startNum,
                                                @Param("pageSize") Integer pageSize);

    /**
     * 查询回收站中未彻底删除的个人文档
     *
     * @return
     */
    List<Document> selectUserDocumentInRecycle(long incId);

    /**
     * 查询未删除的所有版本的组织文档
     *
     * @param incId
     * @return
     */
    List<Document> selectIncDocumentNotDeleted(long incId);

    /**
     * 查询回收站中未彻底删除的组织文档
     *
     * @param incId
     * @return
     */
    List<Document> selectIncDocumentInRecycle(long incId);

    /**
     * 查询用户使用量排行
     *
     * @return
     */
    List<UserRankVO> getUserRank(long incId);

    /**
     * 根据id批量删除文件或文件夹（放入回收站）
     *
     * @author jiadao
     * @date 2017-12-19
     */
    int delDocByBatch(@Param("docIds") List<Long> ids,
                      @Param("updateUser") Long updateUser,
                      @Param("updateUsername") String updateUsername);

    /**
     * 获取一个文件夹中所有尚未删除的子文档
     *
     * @param folderId 文件夹id
     * @return 所有尚未删除的子文档列表
     * @author jiadao
     * @date 2017-12-19
     */
    List<Long> getAllNotDeleteChildDocIdByFolderId(@Param("folderId") String folderId);

    /**
     * 获取一个文件夹下所有的文档
     *
     * @author jiadao
     */
    List<Long> getAllChildDocIdByFolderId(@Param("folderId") String folderId);

    /**
     * 获取父文档的is_delete值
     *
     * @author jiadao
     */
    List<String> getParentDocIsDeleteByDocIds(@Param("docIds") List<Long> docIds);

    /**
     * 获取一个文件夹的所有子一级文档
     *
     * @author jiadao
     */
    List<Long> getDocIdListByDocParentIdInDocument(@Param("docId") Long docId);

    /**
     * 批量还原文档
     *
     * @author jiadao
     */
    int recoverDocByDocIds(@Param("updateUser") Long updateUser,
                           @Param("docIds") List<Long> docIds);


//	List<String> getNamesByIds(@Param("ids")String[] ids);


    /**
     * 查询满足归档条件的文件
     *
     * @param
     * @return
     */

    List<Document> selectByLastTimeAndDocumentAndTagID(@Param("lastUpdateTime") String lastUpdateTime,
                                                       @Param("documentType") List<String> documentType,
                                                       @Param("tagIdList") List<Long> tagIdList,
                                                       @Param("path") String path);


    /**
     * 查询满足归档条件的文件id列表
     *
     * @param
     * @return
     */

    List<Long> selectByLastTimeToListId(@Param("lastUpdateTime") String lastUpdateTime,
                                        @Param("documentType") List<String> documentType,
                                        @Param("tagIdList") List<Long> tagIdList,
                                        @Param("path") String path);

    /**
     * 手动归档时获取一个文件夹所有文件
     *
     * @param path
     * @return
     */
    List<Document> selectManualProDocList(String path);

    /**
     * 获取一个文件夹下所有未删除文件
     *
     * @param folderId
     * @return
     */
    List<Long> getAllNotDelByFolderId(@Param("folderId") String folderId);

    /**
     * 获取一个文件夹的所有未删除子一级文档
     *
     * @param docId
     * @return
     */
    List<Long> getNotDelDocParentIdInDocument(@Param("docId") Long docId);


    /**
     * 按路径查询需要归档的文件id
     *
     * @param path
     * @return
     */
    List<Long> selectByPath(String path);

    /**
     * 按路径查询需要归档的文件
     *
     * @param path
     * @return
     */
    List<Document> selectDocumentByPath(String path);

    /**
     * 验证同组织下的同名归档文件
     *
     * @param incId       组织id
     * @param archiveName 需要验证的文件名
     * @return
     */
    Integer checkArchiveName(@Param("incId") Long incId,
                             @Param("archiveName") String archiveName);

    /**
     * 获取文件系统中的所有文件类型的列表
     *
     * @return
     */
    List<String> selectAllDocumentType();

    /**
     * 归档列表
     *
     * @param incId
     * @param userId
     * @param parentId
     * @param documentType
     * @param documentName
     * @param editDay
     * @return
     */
    List<DocumentExtend> getArchivalFile(@Param("incId") Long incId,
                                         @Param("userId") Long userId,
                                         @Param("parentId") Long parentId,
                                         @Param("documentType") String documentType,
                                         @Param("documentName") String documentName,
                                         @Param("editDay") String editDay,
                                         @Param("userGroupIds") List<Long> userGroupIds,
                                         @Param("deptId") Long deptId);

    /**
     * 获取非首层归档文件
     *
     * @param incId
     * @param userId
     * @param parentId
     * @param documentType
     * @param documentName
     * @param editDay
     * @return
     */
    List<DocumentExtend> getNotTopArvhivalFile(@Param("incId") Long incId,
                                               @Param("userId") Long userId,
                                               @Param("parentId") Long parentId,
                                               @Param("documentType") String documentType,
                                               @Param("documentName") String documentName,
                                               @Param("editDay") String editDay);

    /**
     * 归档列表----条件查询
     *
     * @param incId
     * @param userId
     * @param parentId
     * @param documentType
     * @param documentName
     * @param editDay
     * @param userGroupIds
     * @param deptId
     * @param parentPath
     * @return
     */
    List<DocumentExtend> getArchivalFileByMap(@Param("incId") Long incId,
                                              @Param("userId") Long userId,
                                              @Param("parentId") Long parentId,
                                              @Param("documentType") String documentType,
                                              @Param("documentName") String documentName,
                                              @Param("editDay") String editDay,
                                              @Param("userGroupIds") List<Long> userGroupIds,
                                              @Param("deptId") Long deptId,
                                              @Param("parentPath") String parentPath);


    /**
     * 获取一个文件给与自己所有权限中的最高权限
     *
     * @param incId
     * @param userId
     * @param userGroupIds
     * @param deptId
     * @return
     */
    String getMaxPermission(@Param("documentId") Long documentId,
                            @Param("incId") Long incId,
                            @Param("userId") Long userId,
                            @Param("userGroupIds") List<Long> userGroupIds,
                            @Param("deptId") Long deptId);

    /**
     * @param ids
     * @return
     */
    List<String> getNamesByIds(@Param("ids") List<String> ids,
                               @Param("orderIds") String orderIds);


    /**
     * 验证文档或目录是否存在
     *
     * @param map
     * @return
     */
    Integer exists(Map<String, Object> map);

    /**
     * 统计未删除个人文档占用空间
     *
     * @param incId
     * @return
     */
    Long sumUserDocumentUndeleted(long incId);

    /**
     * 统计回收站中未删除个人文档占用空间
     *
     * @param incId
     * @return
     */
    Long sumUserDocumentInRecycle(long incId);

    /**
     * 统计未删除个人文档占用空间
     *
     * @param incId
     * @return
     */
    Long sumIncDocumentUndeleted(long incId);

    /**
     * 统计回收站中未删除组织文档占用空间
     *
     * @param incId
     * @return
     */
    Long sumIncDocumentInRecycle(long incId);

    /**
     * 统计个人空间已使用大小
     *
     * @param id
     * @return
     */
    Integer statisticsZoneSize(@Param("id") Long id);


    /**
     * 获取分享文件的子文件
     *
     * @param userId
     * @param groupIds
     * @param departmentId
     * @param documentName
     * @param parentId
     * @return
     */
    List<DocumentIdExtendVO> getSubFileList(@Param("userId") Integer userId,
                                            @Param("groupIds") List<Integer> groupIds,
                                            @Param("departmentId") Integer departmentId,
                                            @Param("documentName") String documentName,
                                            @Param("parentId") Long parentId,
                                            @Param("incId") Integer incId);

    /**
     * 获取外链文件夹中的文件
     * 任何人都能访问
     *
     * @param
     * @return
     */
    List<DocumentIdLinkVO> getSubFileLinkA(@Param("parentId") Long parentId,
                                           @Param("linkCode") String linkCode);

    /**
     * 获取外链文件夹中的文件
     * 企业（incId）内的用户都能访问
     *
     * @param
     * @return
     */
    List<DocumentIdLinkVO> getSubFileLinkB(@Param("parentId") Long parentId,
                                           @Param("linkCode") String linkCode,
                                           @Param("incId") Long incId);

    /**
     * 获取外链文件夹中的文件
     * 仅具有访问权限的用户可以访问，用户userId
     *
     * @param
     * @return
     */
    List<DocumentIdLinkVO> getSubFileLinkC(@Param("parentId") Long parentId,
                                           @Param("linkCode") String linkCode,
                                           @Param("userId") Integer userId);

    /**
     * 获取外链文件夹中的文件
     * 仅具有访问权限的用户可以访问，用户组groupId
     *
     * @param
     * @return
     */
    List<DocumentIdLinkVO> getSubFileLinkD(@Param("parentId") Long parentId,
                                           @Param("linkCode") String linkCode,
                                           @Param("groupId") Integer groupId);

    /**
     * 获取外链文件夹中的文件
     * 仅具有访问权限的用户可以访问，部门depId
     *
     * @param
     * @return
     */
    List<DocumentIdLinkVO> getSubFileLinkE(@Param("parentId") Long parentId,
                                           @Param("linkCode") String linkCode,
                                           @Param("depId") Integer depId);

    ExternalLink getExterLinkByCode(String linkCode);

    List<DocumentPermission> getDocPermissionById(Long parentId);

    /**
     * 根据父ID获取子目录或文档
     *
     * @param map
     * @return
     */
    List<Document> getDocumentByParentId(Map<String, Object> map);

    /**
     * 统计未删除个人文档占用空间(以当前用户查询)
     *
     * @param map
     * @return
     */
    long sumUserDocumentUndeletedByCurrentUser(Map<String, Object> map);

    /**
     * 统计回收站中未删除个人文档占用空间(以当前用户查询)
     *
     * @param map
     * @return
     */
    long sumUserDocumentInRecycleByCurrentUser(Map<String, Object> map);

    /**
     * 根据文件名获取文件id
     *
     * @param docName
     * @param incId
     * @param user
     * @return
     */
    Long getDocumentIdByName(@Param("docName") String docName,
                             @Param("incId") Long incId,
                             @Param("user") Long user,
                             @Param("parentId") Long parentId,
                             @Param("type") String type);

    /**
     * 获取个人文件id集合
     *
     * @param incId
     * @param userId
     * @return
     */
    List<String> getPersonalFileIdList(@Param("incId") Long incId,
                                       @Param("userId") Long userId);

    List<String> getShareSpaceFileIdList(@Param("incId") Long incId,
                                         @Param("userId") Long userId,
                                         @Param("userGroupIds") List<Long> userGroupIds,
                                         @Param("deptId") Long deptId,
                                         @Param("parentId") Long parentId,
                                         @Param("type") Integer type);

    /**
     * 获取归档列表文件id集合
     *
     * @param incId
     * @param userId
     * @param userGroupIds
     * @param deptId
     * @return
     */
    List<String> getArchivalFileIdList(@Param("incId") Long incId,
                                       @Param("userId") Long userId,
                                       @Param("userGroupIds") List<Long> userGroupIds,
                                       @Param("deptId") Long deptId);

    /**
     * 获取模式相同的同名文档
     *
     * @param parentId    父文件夹ID
     * @param namePattern 文档模式
     * @return 文档列表
     */
    List<Document> getSamePatternDocuments(@Param("parentId") Long parentId,
                                           @Param("namePattern") String namePattern);

    /**
     * 根据父id获取文件夹
     *
     * @param incId
     * @param userId
     * @param parentId
     * @return
     */
    List<DocumentZtreeVO> getPersonalDirectoryByParentId(@Param("incId") Long incId,
                                                         @Param("userId") Long userId,
                                                         @Param("parentId") Long parentId);

    /**
     * 根据parentId获取具有读写权限的组织文件子文件夹目录
     *
     * @param incId
     * @param userId
     * @param userGroupIds
     * @param deptId
     * @param parentId
     * @param documentName
     * @return
     */
    List<DocumentZtreeVO> getGreaterThanWritePrivilegeIncDirectoryByParentId(@Param("incId") Long incId,
                                                                             @Param("userId") Long userId,
                                                                             @Param("userGroupIds") List<Long> userGroupIds,
                                                                             @Param("deptId") Long deptId,
                                                                             @Param("parentId") Long parentId,
                                                                             @Param("documentName") String documentName,
                                                                             @Param("parentPath") String parentPath,
                                                                             @Param("isExact") Long isExact
    );

    /**
     * 获取最近浏览历史文件
     *
     * @param params
     * @return
     */
    List<DocumentRecentBrowseVO> getRecentBrowseDocuments(Map<String, Object> params);

    /**
     * 版本Id是文件最新版本则
     */
    String getVersionById(@Param("versionId") String versionId);

    DocumentExtend getDocumentById(@Param("documentId") String documentId);

    /**
     * 判断文件是否与我共享
     *
     * @param documentId
     * @param receiverId
     * @return
     */
    int isShared(@Param("documentId") Long documentId,
                 @Param("receiverId") Long receiverId);

    /**
     * 获取分享文件子文件
     *
     * @param params
     * @return
     */
    List<DocumentExtend> getSubShareFileList(Map<String, Object> params);

    long getOrganizedFileCount(@Param("incId") Long incId,
                               @Param("userId") Long userId,
                               @Param("userGroupIds") List<Long> userGroupIds,
                               @Param("deptId") Long deptId);


    long getSharedFilesCount(@Param("incId") Integer incId,
                             @Param("userId") Integer userId,
                             @Param("userGroupIds") List<Integer> userGroupIds,
                             @Param("deptId") Integer deptId);

    List<Document> getPersonalFileChildren(@Param("parentId") String parentId,
                                           @Param("incId") Long incId,
                                           @Param("userId") Long userId);

    List<DocumentExtend> getOrganizedFileChildren(@Param("parentId") String parentId,
                                                  @Param("incId") Long incId,
                                                  @Param("userId") Long userId,
                                                  @Param("userGroupIds") List<Long> userGroupIds,
                                                  @Param("deptId") Long deptId);


    List<Document> getShareFileChildren(@Param("parentId") String parentId,
                                        @Param("incId") Integer incId,
                                        @Param("userId") Integer userId,
                                        @Param("userGroupIds") List<Integer> userGroupIds,
                                        @Param("deptId") Integer deptId);

    List<String> getVersionIdsForFullText(@Param("incId") Long incId,
                                          @Param("documentIds") String documentIds);

    List<DocumentExtend> getDocumentsByIds(@Param("documentIds") String documentIds);

    /**
     * 根据文档版本id判断文档是否删除
     *
     * @param docVersionId
     * @return
     */
    String checkFileIsDeleteByDocumentVersionId(String docVersionId);

    /**
     * 根据parentId模糊查询文件夹下所有匹配文件
     *
     * @param params
     * @return
     */
    List<Document> getAllChildrenByParentIdAndDocumentName(Map<String, Object> params);

    /**
     * 获取个人文件,组织文件,归档文件最近一次操作时间
     *
     * @param incId
     * @param userId
     * @return
     */
    Map<String, Date> getLastUpdateTime(@Param("incId") Integer incId,
                                        @Param("userId") Integer userId);

    /**
     * 直接修改归档文件夹的大小
     *
     * @param documentId
     * @return
     */
    Long sumSize(@Param("documentId") Long documentId);

    /**
     * 获取归档文件夹大小
     *
     * @param documentId
     * @return
     */
    Long getSumSize(@Param("documentId") Long documentId);

    /**
     * 获取所有归档文件夹
     *
     * @param documentId
     * @return
     */
    List getAlldir(@Param("documentId") Long documentId);

    /**
     * 修改每个子文档的大小
     *
     * @param documentIds
     * @return
     */
    Long updateAllSize(@Param("documentIds") List documentIds);

    /**
     * 解锁组织文件
     *
     * @param incid
     * @param idPath
     * @param id
     * @return
     */
    Long unLockOrgDocument(@Param("incId") Integer incid,
                           @Param("idPath") String idPath,
                           @Param("id") Long id);

    /**
     * 定时自动解锁文档
     *
     * @param unlockDay
     */
    Long timingUnlock(@Param("unlockDay") Integer unlockDay);

    /**
     * 查询系统管理员组织文件列表
     *
     * @param id
     * @param incId
     * @param parentId
     * @param idPath
     * @param documentName
     * @param documentType
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<DocumentExtend> getOrgFileListOfSystemAdmin(@Param("incId") Long id,
                                                     @Param("userId") Long incId,
                                                     @Param("parentId") Long parentId,
                                                     @Param("idPath") String idPath,
                                                     @Param("documentName") String documentName,
                                                     @Param("documentType") String documentType,
                                                     @Param("pageNum") Integer pageNum,
                                                     @Param("pageSize") Integer pageSize,
                                                     @Param("systemAdminPermission") String systemAdminPermission);

    /**
     * 查询组织文件列表
     * @param incId
     * @param userId
     * @param groupIds
     * @param deptId
     * @param parentId
     * @param idPath
     * @param documentName
     * @param documentType
     * @param pageNum
     * @param pageSize
     * @param permissionIndex
     * @return
     */
    List<DocumentExtend> getOrgFileList(@Param("incId") Long incId,
                                        @Param("userId") Long userId,
                                        @Param("groupIds") List<Long> groupIds,
                                        @Param("deptId") Long deptId,
                                        @Param("parentId") Long parentId,
                                        @Param("idPath") String idPath,
                                        @Param("documentName") String documentName,
                                        @Param("documentType") String documentType, @Param("pageNum") Integer pageNum,
                                        @Param("pageSize") Integer pageSize,
                                        @Param("associatedVisiblePermission") String associatedVisiblePermission,
                                        @Param("permissionIndex") String permissionIndex);
}