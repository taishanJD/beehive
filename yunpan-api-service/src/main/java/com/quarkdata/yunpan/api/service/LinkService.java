package com.quarkdata.yunpan.api.service;

import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.vo.ShareAndLinkVO;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @author maorl
 * @date 12/18/17.
 */
public interface LinkService extends BaseLogService {
    /**
     * 创建外链
     * @param request
     * @param documentId
     * @param linkType
     * @param expireTime
     * @param allowPreview
     * @param allowDownload
     * @param isSecret
     * @param fetchCode
     * @param emails
     * @param telephones
     * @return
     */
    ResultCode addLink(HttpServletRequest request, String documentId, String linkType, Date expireTime, Boolean allowPreview, Boolean allowDownload, Boolean isSecret, String fetchCode, List<String> emails, List<String> telephones);

    /**
     * 移出外链
     * @param request
     * @param linkIds
     * @return
     */
    ResultCode deleteLink(HttpServletRequest request, List<String> linkIds);

    /**
     * 获取我的外链列表
     * @param users
     * @param parentId
     * @param docId
     * @param documentName
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResultCode<List<ShareAndLinkVO>> getLinks(UserInfoVO users, Long parentId, Long docId, String documentName, Integer pageNum, Integer pageSize);

    /**
     * 修改外链信息
     * @param request
     * @param linkId
     * @param expireTime
     * @param allowPreview
     * @param allowDownload
     * @param isSecret
     * @param fetchCode
     * @param emailList
     * @param telephoneList
     * @return
     */
    void update(HttpServletRequest request, String linkId, Date expireTime, Boolean allowPreview, Boolean allowDownload, Boolean isSecret, String fetchCode, List<String> emailList, List<String> telephoneList);
}
