package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.vo.DocumentPermissionVO;
import com.quarkdata.yunpan.api.model.vo.DocumentShareExtend;
import com.quarkdata.yunpan.api.model.vo.ShareAndLinkVO;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface DocumentShareMapper2 {

    /**
     * @param userId
     * @param groupIds
     * @param departmentId
     * @param documentName
     * @param parentId
     * @param startNum
     * @param pageSize
     * @return
     */
    List<DocumentShareExtend> selectShareList(@Param("userId") Integer userId,
                                              @Param("groupIds") List<Integer> groupIds,
                                              @Param("departmentId") Integer departmentId,
                                              @Param("documentName") String documentName,
                                              @Param("incId") Integer incId,
                                              @Param("parentId") Long parentId,
                                              @Param("startNum") Integer startNum,
                                              @Param("pageSize") Integer pageSize);


    /**
     * @param docId
     * @param recType
     * @param recId
     * @return
     */
    HashMap isShared(@Param("docId") Long docId,
                     @Param("recType") String recType,
                     @Param("recId") String recId);

    /**
     * 归档不保留原文件时，删除原文件共享信息
     * @param docIdList
     */
    void deleteByDocumentIdList(List<Long> docIdList);

    /**
     * 获取我的分享(共享and外链)列表---我的共享
     * @param params
     * @return
     */
    List<ShareAndLinkVO> getShareVOList(Map<String, Object> params);

    /**
     * 根据文档id获取文档共享记录
     * @param params
     * @return
     */
    List<DocumentPermissionVO> getShareRecordsByDocumentId(Map<String, Object> params);

    /**
     * 查看是否与我共享(不排除自己移除的)
     * @param params
     * @return
     */
    Integer isSharedToMe(Map<String, Object> params);

    /**
     * 是否移出共享
     * @param incId
     * @param userId
     * @param docId
     * @return
     */
    Integer isExcludeShare(@Param("incId") Integer incId,
                           @Param("userId") Integer userId,
                           @Param("docId") Long docId);

    /**
     * 查看是否与我共享(排除自己移除的)
     * @param params
     * @return
     */
    Integer isSharedToMeWithoutMyExclude(Map<String, Object> params);
}