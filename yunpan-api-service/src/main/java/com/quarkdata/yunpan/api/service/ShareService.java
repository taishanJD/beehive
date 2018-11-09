package com.quarkdata.yunpan.api.service;

import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.DocumentShare;
import com.quarkdata.yunpan.api.model.request.Receiver;
import com.quarkdata.yunpan.api.model.vo.DocumentPermissionVO;
import com.quarkdata.yunpan.api.model.vo.DocumentShareExtend;
import com.quarkdata.yunpan.api.model.vo.ShareAndLinkVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author maorl
 * @date 12/6/17.
 */
public interface ShareService extends BaseLogService {

    ResultCode deleteShare(long incId, Users user, HttpServletRequest request, List<String> ids);

    ResultCode addShare(HttpServletRequest request, List<String> documentIds, List<Receiver> receiverList, List<Receiver> excludeList, Users users);

    /**
     * 查询与我共享列表
     * @param users
     * @param parentId
     * @param documentName
     * @return
     */
    ResultCode<List<DocumentShareExtend>> getShareList(UserInfoVO users, Long parentId, String documentName, Integer pageNum, Integer pageSize);

    /**
     * 获取分享文件夹中的子文件
     * @param parentId
     * @return
     */
    ResultCode<List<DocumentShareExtend>> getSubFileList(Long incId, UserInfoVO users, String documentName, Long parentId);

    ResultCode getFileCount(UserInfoVO users);

    /**
     * 共享人删除自己的文档共享记录
     * @param request
     * @param incId
     * @param userInfoVO
     * @param shareIds
     */
    Map<Long, List<DocumentPermissionVO>> deleteMyShare(HttpServletRequest request, Integer incId, UserInfoVO userInfoVO, String shareIds);

    /**
     * 根据文档id获取文档共享记录
     * @param incId
     * @param userInfoVO
     * @param docId
     * @return
     */
    ResultCode<List<DocumentPermissionVO>> getShareRecordsByDocumentId(Integer incId, UserInfoVO userInfoVO, long docId);
    /**
     * 根据文档id获取文档共享记录(不包装被共享单位)
     * @param incId
     * @param userInfoVO
     * @param docId
     * @return
     */
    ResultCode<List<DocumentPermissionVO>> getShareRecordsByDocumentIdSimple(Integer incId, UserInfoVO userInfoVO, long docId);

    /**
     * 查询我的共享列表
     * @param incId
     * @param userInfoVO
     * @param parentId
     * @param documentName
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResultCode<List<ShareAndLinkVO>> getMyShare(Integer incId, UserInfoVO userInfoVO, Long parentId, String documentName, Integer pageNum, Integer pageSize);
    /**
     * 查询我共享文件夹下所有文件
     * @return
     * */
    ResultCode getAllChildren(UserInfoVO users, Long parentId);

    DocumentShare getShareById(long shareId);

    /**
     * 根据文档ID判断是否与我共享
     * @param incId
     * @param userId
     * @param groupIds
     * @param integer
     * @param docId
     * @return
     */
    Boolean getIsShareToMeByDocId(Integer incId, Integer userId, List<Long> groupIds, Integer integer, Long docId);
}
