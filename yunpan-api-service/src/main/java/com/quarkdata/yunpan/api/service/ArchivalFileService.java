package com.quarkdata.yunpan.api.service;

import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.yunpan.api.model.common.ResultCode;

import com.quarkdata.yunpan.api.model.request.Receiver;
import com.quarkdata.yunpan.api.model.vo.DocumentExtend;
import com.quarkdata.yunpan.api.model.vo.DocumentPermissionVO;
import com.quarkdata.yunpan.api.model.vo.DocumentPrivilegeVO;
import com.quarkdata.yunpan.api.model.vo.DocumentPrivilegeVOfGenerateAccounts;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ArchivalFileService extends BaseLogService {
    /**
     * 查看用户归档文件信息 携带收藏和标签信息
     * @param documentType
     * @param documentName
     * @param editDay
     * @return
     */
    ResultCode<List<DocumentExtend>> getArchivalFile(Long incId,Long userId,Long parentId,String documentType,String documentName,String editDay,List<Long> userGroupIds,Long deptId,Long roleId);


    /**
     * 自动归档预览列表
     */
    ResultCode autoCreateArchivalFile(List<Long> docIdList,String lastUpdateTime, List<String> documentType, List<Long> tagIdList);

    /**
     *手动归档预览列表
     */
    ResultCode manualProList(List<Long> docIdList);
    /**
     * 归档
     * @param docIdList 归档文档id列表
     * @param isKeepSource  是否保留原文件
     * @return
     */
    ResultCode manualCreateArchivalFile(HttpServletRequest request, List<Receiver> receiverList, String docName, String docDes, Users user, List<Long> docIdList, String isKeepSource);

    /**
     * 验证同组织下的同名归档文件
     * @param incId 组织id
     * @param archiveName 需要验证的文件名
     * @return
     */
    ResultCode checkArchiveName(Long incId,String archiveName);

    /**
     * 获取文件系统中的所有文件类型的列表
     * @return
     */
    ResultCode selectAllDocumentType();

    /**
     * 更新归档文件
     * @param id
     * @param docName
     * @param des
     * @param request
     */
    void updateArchiveFile(Long id,String docName,String des,HttpServletRequest request,List<Long> userGroupIds,Long deptId);

    /**
     * 设置归档文件权限
     * @param request
     * @param documentId
     * @param permissions
     * @param permissionsOfGenerateAccounts
     * @param incId
     * @param isOrganizedAdmin
     * @param userId
     * @param userGroupIds
     * @param deptId
     * @return
     */
    ResultCode<String> setPermissionOfArchivalFile(HttpServletRequest request, Long documentId, List<DocumentPrivilegeVO> permissions, List<DocumentPrivilegeVOfGenerateAccounts> permissionsOfGenerateAccounts, Long incId, boolean isOrganizedAdmin, Long userId, List<Long> userGroupIds, Long deptId);

    /**
     * 获取归档文件权限
     * @param docId
     * @param incId
     * @return
     */
    ResultCode<List<DocumentPermissionVO>> getArchivalDocumentPermissions(Long docId, Long incId);

    /**
     * Created by xiexl on 2018/1/11.
     */
    interface DocumentService {
    }
}
