package com.quarkdata.yunpan.api.service;

import com.amazonaws.services.s3.model.S3Object;
import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.dataobj.DocumentVersion;
import com.quarkdata.yunpan.api.model.vo.DocumentRecentBrowseVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by xiexl on 2018/1/11.
 */
public interface DocumentService extends BaseLogService {
    /**
     * 文件重命名，ceph不操作
     * @param req
     * @param id
     * @param documentName
     */
    ResultCode<String> rename(HttpServletRequest req, Long id, String documentName, Long userId, List<Long> userGroupIds, Long deptId);

    /**
     * 移动文件至目标目录，ceph不移动
     * @param req
     * @param sourceDocumentIds
     * @param targetDocumentId
     */
    ResultCode<String> move(HttpServletRequest req, String sourceDocumentIds, Long targetDocumentId, String targetDocumentType);

    /**
     * 拷贝文件至目标目录，ceph拷贝
     * @param req
     * @param sourceDocumentIds
     * @param targetDocumentId
     */
    ResultCode<String> copy(HttpServletRequest req, String sourceDocumentIds, Long targetDocumentId, String targetDocumentType);


    /**
     * 判断文件名是否在此目录下存在
     * @param documentName
     * @return
     */
    boolean exists(String type,Long parentId,Long incId,String documentName, Long userId);

    /**
     * 统计个人空间已经使用大小,单位G
     * @param id
     * @return
     */
    Double statisticsZoneSize(Long id);

    /**
     * 获取个人空间配定大小,单位G
     * @return
     */
    Double getZoneByUserId(Long incId);

    /**
     * 计算个人空间使用率
     * @param userId
     * @param incId
     * @return
     */
    String calculateZoneUtilization(Long userId,Long incId);

    /**
     * 根据文档ID&&文档版本ID获取文档访问URL
     * @param docVersionId
     * @return
     */
    S3Object getDocumentByVersionId(Long docVersionId);

    /**
     * 根据文档版本id获取文档
     * @param docVersionId
     * @return
     */
    Document getDocumentByVersion(Long docVersionId);

    String getFilePathByVersionId(Long docVersionId);

    /**
     * 根据文档id批量修改文件类型(组织文件or个人文件)
     * @param incId
     * @param user
     * @param documentIds
     * @param type
     */
    void updateDocumentTypeByIdBatch(Integer incId, Users user, String[] documentIds, String type);

    /**
     * 新建[文件夹]时检查是否已有同名[文件夹]
     * @param incId
     * @param user
     * @param parentId
     * @param directoryName
     * @return
     */
    boolean checkDirectoryName(Integer incId, Users user, String parentId, String directoryName, String type);

    /**
     * 根据id获取文档
     * @param id
     * @return
     */
    Document getDocumentById(Long id);

    /**
     * 根据版本id获取文档当前版本号
     * @param versionId
     * @return
     */
    DocumentVersion getDocumentVsersionByVersion(long versionId);

    /**
     * 获取最近浏览历史文件
     * @return
     * @param incId
     * @param userId
     * @param pageNum
     * @param pageSize
     */
    ResultCode<List<DocumentRecentBrowseVO>> getRecentBrowseDocuments(Integer incId, Integer userId, List<Long> userGroupIds, Long deptId, Integer pageNum, Integer pageSize);

    /**
     * 文档预览
     * @param req
     * @param filePath
     * @param resp
     * @param documentIds
     */
    void previewFile(HttpServletRequest req, String filePath, HttpServletResponse resp, List<String> documentIds);

    /**
     * 移除最近浏览记录
     * @param incId
     * @param userId
     * @param logIds
     */
    void removeRecentBrowseRecords(Integer incId, Integer userId, String logIds);

    Boolean checkExist(String docVersionId);

    /**
     * 根据id查询文档信息: 个人文件检查create_user,组织文件鉴权
     * @param incId
     * @param userId
     * @param groupIds
     * @param deptId
     * @param docId
     * @return
     */
    ResultCode<Document> getDocumentByIdWithPermission(Integer incId, Integer userId, List<Long> groupIds, Long deptId, Long docId);

    /**
     * 根据idPath查询namePath
     *
     * @param incId
     * @param idPath
     * @return
     */
    ResultCode<Map<String, String>> getNamePathByIdPath(Integer incId, String idPath);

    /**
     * 获取个人文件,组织文件,归档文件最近一次操作时间
     * @param incId
     * @param userId
     * @return
     */
    ResultCode<Map<String, Date>> getLastUpdateTime(Integer incId, Integer userId);

    /**
     * 自动解锁文档
     * @param unlockDay
     */
    Long timingUnlock(Integer unlockDay);
}
