package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.quark.share.model.dataobj.Department;
import com.quarkdata.quark.share.model.dataobj.Group;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.dal.dao.DocumentMapper;
import com.quarkdata.yunpan.api.dal.dao.DocumentShareMapper2;
import com.quarkdata.yunpan.api.dal.dao.ExternalLinkMapper2;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.vo.DocumentPermissionVO;
import com.quarkdata.yunpan.api.model.vo.ShareAndLinkVO;
import com.quarkdata.yunpan.api.service.LinkService;
import com.quarkdata.yunpan.api.service.ShareAndLinkService;
import com.quarkdata.yunpan.api.service.ShareService;
import com.quarkdata.yunpan.api.util.RestDateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by yanyq1129@thundersoft.com on 2018/5/18.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ShareAndLinkServiceImpl implements ShareAndLinkService {

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private DocumentShareMapper2 documentShareMapper2;

    @Autowired
    private ExternalLinkMapper2 externalLinkMapper2;

    @Autowired
    private ShareService shareService;

    @Autowired
    private LinkService linkService;

    @Override
    public ResultCode<List<ShareAndLinkVO>> getShareAndLinkList(Integer incId, UserInfoVO userInfoVO, Long parentId, String documentName, Integer pageNum, Integer pageSize) {
        ResultCode<List<ShareAndLinkVO>> resultCode = new ResultCode<>();
        List<ShareAndLinkVO> list = new ArrayList<>();
        Integer userId = userInfoVO.getUser().getUserId();
        Department department = userInfoVO.getDepartment();
        List<Group> groupsList = userInfoVO.getGroupsList();
        Map<String, Object> params = new HashMap<>();
        params.put("incId", incId);
        params.put("userId", userId);
        params.put("parentId", parentId);
        params.put("documentName", StringUtils.isBlank(documentName) ? null : documentName);
        /*params.put("department", department);
        if(groupsList != null && groupsList.size() > 0) {
            params.put("groupsList", groupsList);
        } else {
            params.put("groupsList", null);
        }*/
        if(pageNum != null && pageNum > 0 && pageSize != null && pageSize > 0 ) {
            params.put("startNum", (pageNum - 1) * pageSize);
            params.put("pageSize", pageSize);
        } else {
            params.put("startNum", null);
            params.put("pageSize", null);
        }
        // 查询我的共享数据
        List<ShareAndLinkVO> shareList = this.documentShareMapper2.getShareVOList(params);
        // 查询我的外链数据
        List<ShareAndLinkVO> linkList = this.externalLinkMapper2.getLinkVOList(params);

        // 设置共享数据namePath
        if(shareList != null && shareList.size() > 0) {
            for(ShareAndLinkVO shareAndLinkVO: shareList) {
                if(shareAndLinkVO.getType().equals("1") && shareAndLinkVO.getDocumentType().equals("dir")) {
                    StringBuffer sb = new StringBuffer("/个人文件");
                    sb.append(this.getNamePathByIdPath(shareAndLinkVO.getIdPath().split("/")));
                    shareAndLinkVO.setNamePath(sb.toString());
                }
            }
        }
        // 设置外链数据 namePath和倒计时
        if(linkList != null && linkList.size() > 0) {
            for(ShareAndLinkVO shareAndLinkVO: linkList) {
                if(shareAndLinkVO.getExpirationTime() != null && !shareAndLinkVO.getExpirationTime().equals("-1")) {
                    shareAndLinkVO.setDisplayTime(RestDateUtils.getRestTime(shareAndLinkVO.getExpirationTime()));
                }
                if(shareAndLinkVO.getType().equals("1") && shareAndLinkVO.getDocumentType().equals("dir")) {
                    String[] ids = shareAndLinkVO.getIdPath().split("/");
                    StringBuffer sb = new StringBuffer("/个人文件");
                    sb.append(this.getNamePathByIdPath(ids));
                    shareAndLinkVO.setNamePath(sb.toString());
                }
                if(shareAndLinkVO.getType().equals("0") && shareAndLinkVO.getDocumentType().equals("dir")) {
                    String[] ids = shareAndLinkVO.getIdPath().split("/");
                    StringBuffer sb = new StringBuffer("/组织文件");
                    sb.append(this.getNamePathByIdPath(ids));
                    shareAndLinkVO.setNamePath(sb.toString());
                }
            }
        }

        list.addAll(shareList);
        list.addAll(linkList);

        resultCode.setData(list);

        return resultCode;
    }

    @Override
    public Map<Long, List<DocumentPermissionVO>> deleteMyShareAndLink(HttpServletRequest request, Integer incId, UserInfoVO userInfoVO, String shareIds, String linkIds) {
        Map<Long, List<DocumentPermissionVO>> messageData = null;
        if(StringUtils.isNotBlank(shareIds)) {
            messageData = this.shareService.deleteMyShare(request, incId, userInfoVO, shareIds);
        }
        if(StringUtils.isNotBlank(linkIds)) {
            List<String> ids_link = Arrays.asList(linkIds.split(","));
            this.linkService.deleteLink(request, ids_link);
        }
        return messageData;
    }

    /**
     * 通过文档idPath获取namePath
     * @param ids
     */
    private String getNamePathByIdPath(String[] ids) {
        StringBuffer sb = new StringBuffer();
        if(ids != null && ids.length > 0) {
            for(String id: ids) {
                if(StringUtils.isNotBlank(id.trim())) {
                    Document document = this.documentMapper.selectByPrimaryKey(Long.parseLong(id));
                    sb.append("/" + document.getDocumentName());
                }
            }
        }
        return sb.toString();
    }
}
