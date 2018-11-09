package com.quarkdata.yunpan.api.service;

import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.vo.DocumentPermissionVO;
import com.quarkdata.yunpan.api.model.vo.ShareAndLinkVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by yanyq1129@thundersoft.com on 2018/5/18.
 */
public interface ShareAndLinkService {

    /**
     * 获取我的分享和我的外链列表
     * @return
     * @param incId
     * @param userInfoVO
     * @param pageNum
     * @param pageSize
     */
    ResultCode<List<ShareAndLinkVO>> getShareAndLinkList(Integer incId, UserInfoVO userInfoVO, Long parentId, String documentName, Integer pageNum, Integer pageSize);

    /**
     * 分享人删除自己分享或外链的记录
     * @param request
     * @param incId
     * @param userInfoVO
     * @param shareIds
     * @param linkIds
     */
    Map<Long, List<DocumentPermissionVO>> deleteMyShareAndLink(HttpServletRequest request, Integer incId, UserInfoVO userInfoVO, String shareIds, String linkIds);
}
