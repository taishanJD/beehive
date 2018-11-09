package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.quark.share.model.dataobj.Department;
import com.quarkdata.quark.share.model.dataobj.Group;
import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.dal.api.UserInfoRedis;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.dal.dao.*;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.TokenApi;
import com.quarkdata.yunpan.api.model.common.ActionType;
import com.quarkdata.yunpan.api.model.common.DocumentConstants;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.*;
import com.quarkdata.yunpan.api.model.vo.AccessLinkVO;
import com.quarkdata.yunpan.api.model.vo.DocumentIdLinkVO;
import com.quarkdata.yunpan.api.service.AccessLinkService;
import com.quarkdata.yunpan.api.util.RestDateUtils;
import com.quarkdata.yunpan.api.util.ResultUtil;
import com.quarkdata.yunpan.api.util.SpringContextHolder;
import com.quarkdata.yunpan.api.util.StringUtils;
import com.quarkdata.yunpan.api.util.common.mapper.JsonMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author maorl
 * @date 12/19/17.
 */
@Service
public class AccessLinkServiceImpl extends BaseLogServiceImpl implements AccessLinkService {
    static Logger logger = LoggerFactory.getLogger(AccessLinkServiceImpl.class);
    @Autowired
    ExternalLinkMapper externalLinkMapper;
    @Autowired
    TokenApi tokenApi;
    @Autowired
    DocumentPermissionMapper2 documentPermissionMapper2;
    @Autowired
    DocumentMapper documentMapper;
    @Autowired
    DocumentMapper2 documentMapper2;

    private LogMapper logMapper;

    private LogDocumentRelMapper logDocumentRelMapper;

    @Resource
    public void setLogMapper(LogMapper logMapper) {
        this.logMapper = logMapper;
        super.setLogMapper(logMapper);
    }

    @Resource
    public void setLogDocumentRelMapper(LogDocumentRelMapper logDocumentRelMapper) {
        this.logDocumentRelMapper = logDocumentRelMapper;
        super.setLogDocumentRelMapper(logDocumentRelMapper);
    }

    /**
     * 通过外链访问
     *
     * @param linkCode
     * @param authorization
     * @param fetchCode
     * @return
     */
    @Override
    public ResultCode accessLink(HttpServletRequest request, String linkCode, String authorization, String fetchCode) {
        ResultCode resultCode;
        try {
            ExternalLinkExample example = new ExternalLinkExample();
            example.createCriteria().andExternalLinkCodeEqualTo(linkCode);
            List<ExternalLink> externalLinks = externalLinkMapper.selectByExample(example);
            if (externalLinks != null && externalLinks.size() < 1) {
                return ResultUtil.error(Messages.API_LINK_IS_NULL_CODE, Messages.API_LINK_IS_NULL_MSG);
            }
            ExternalLink link = externalLinks.get(0);

            // 验证提取码
            String isSecret = link.getIsSecret();
            if(DocumentConstants.Link.IS_SECRET_YES.equals(isSecret)) {
                if(StringUtils.isBlank(fetchCode)) {
                    return ResultUtil.error(Messages.Link.MISSING_FETCH_CODE, Messages.Link.MISSING_FETCH_CODE_MSG);
                } else if(!fetchCode.equals(link.getFetchCode())) {
                    return ResultUtil.error(Messages.Link.FETCH_CODE_ERROR_CODE, Messages.Link.FETCH_CODE_ERROR_MSG);
                }
            }

            //判定权限
            String linkType = link.getExternalLinkType();
            //所有人可见
            if ("0".equals(linkType)) {
                resultCode = isExpire(request, link);
                //组织内人员可见
            } else if ("1".equals(linkType)) {
                if (isAuthorization(authorization)) {
                    resultCode = isExpire(request, link);
                } else {
                    resultCode = ResultUtil.error(Messages.API_LINK_LOGIN_CODE, Messages.API_LINK_LOGIN_MSG);
                }
            } else {
                if (isAuthorization(authorization)) {
                    if (havePermission(link)) {
                        resultCode = isExpire(request, link);
                    } else {
                        resultCode = ResultUtil.error(Messages.API_LINK_PERMISSION_CODE, Messages.API_LINK_PERMISSION_MSG);
                    }
                } else {
                    resultCode = ResultUtil.error(Messages.API_LINK_LOGIN_CODE, Messages.API_LINK_LOGIN_MSG);
                }
            }
        } catch (IOException e) {
            logger.error("访问外链页面异常", e);
            resultCode = ResultUtil.error(Messages.API_LINK_EXCEPTION_CODE, Messages.API_LINK_EXCEPTION_MSG);
        }
        return resultCode;
    }

    /**
     * 验证调用接口是否合法
     *
     * @param documentIds
     * @param linkCode
     * @return
     */
    @Override
    public boolean isLegal(List<String> documentIds, String linkCode) {
        try {
            ExternalLinkExample example = new ExternalLinkExample();
            example.createCriteria().andExternalLinkCodeEqualTo(linkCode);
            List<ExternalLink> externalLinks = externalLinkMapper.selectByExample(example);
            if (externalLinks != null && externalLinks.size() < 1) {
                return false;
            }
            ExternalLink link = externalLinks.get(0);
            if (link.getExternalLinkExpireTime().before(new Date())) {
                return false;
            }
            Document document = documentMapper.selectByPrimaryKey(link.getDocumentId());
            String idPath = document.getIdPath();
            List<Long> docIds = new ArrayList<>();
            for (String id: documentIds) {
                docIds.add(Long.valueOf(id));
            }
            DocumentExample documentExample = new DocumentExample();
            documentExample.createCriteria().andIdIn(docIds).andIdPathLike(idPath + "%");
            List<Document> documents = documentMapper.selectByExample(documentExample);
            if (documents.size() != docIds.size()) {
                return false;
            }
//             addDownloadCount(link);
        } catch (Exception e) {
            logger.error("验证下载链接是否合法出现异常", e);
            return false;
        }
        return true;
    }

    @Override
    public ResultCode getOwner(String linkCode) {
        ResultCode resultCode;
        try {
            ExternalLinkExample example = new ExternalLinkExample();
            example.createCriteria().andExternalLinkCodeEqualTo(linkCode);
            List<ExternalLink> externalLinks = externalLinkMapper.selectByExample(example);
            String owner = externalLinks.get(0).getCreateUsername();
            resultCode = ResultUtil.success(owner);
        } catch (Exception e) {
            logger.error("获取链接创建者是失败", e);
            resultCode = ResultUtil.error(Messages.API_LINK_OWNER_CODE, Messages.API_LINK_OWNER_MSG);
        }
        return resultCode;
    }

    /**
     * 增加下载次数
     *
     * @param link
     */
    private void addDownloadCount(ExternalLink link) {
        try {
            link.setDownloadCount(link.getDownloadCount() + 1);
            externalLinkMapper.updateByPrimaryKeySelective(link);
        } catch (Exception e) {
            logger.error("增加下载次数失败");
            e.printStackTrace();
        }
    }

    /**
     * 判断是否有权限访问该外链文件
     *
     * @param link
     * @return
     */
    private boolean havePermission(ExternalLink link) {
        UserInfoVO users = UserInfoUtil.getUserInfoVO();
        Integer depId = users.getDepartment().getId();
        List<Group> groupsList = users.getGroupsList();
        List<Integer> groupIds = new ArrayList<>();
        for (Group group :
                groupsList) {
            if (group == null) {
                continue;
            }
            groupIds.add(group.getId());
        }
        Integer userId = users.getUser().getUserId();
        Integer incId = users.getUser().getIncid();
        List<DocumentPermission> permission = documentPermissionMapper2.getPermission(userId, groupIds, depId, incId, link.getDocumentId());
        return permission.size() > 0;
    }

    /**
     * 外链是否过期并获取数据
     *
     * @param link
     * @return
     */
    private ResultCode isExpire(HttpServletRequest request, ExternalLink link) {
        ResultCode resultCode;

        Date expireTime = link.getExternalLinkExpireTime();
        Date now = new Date();
        if (expireTime.after(now)) {
            resultCode = getLinkDoc(request, link);
        } else {
            resultCode = ResultUtil.error(Messages.API_LINK_IS_EXPIRE_CODE, Messages.API_LINK_IS_EXPIRE_MSG);
        }
        return resultCode;
    }

    /**
     * 通过外链获取文件
     *
     * @param link
     * @return
     */
    private ResultCode getLinkDoc(HttpServletRequest request, ExternalLink link) {
        ResultCode resultCode;
        try {
            List<AccessLinkVO> docs = new ArrayList<>();
            Document document = documentMapper.selectByPrimaryKey(link.getDocumentId());
            if (document == null) {
                return ResultUtil.error(Messages.API_LINK_IS_NULL_CODE, Messages.API_LINK_IS_NULL_MSG);
            }
            AccessLinkVO vo = new AccessLinkVO(document);
            vo.setCreateTime(link.getCreateTime());
            vo.setDisplayTime(RestDateUtils.getRestTime(link.getExternalLinkExpireTime()));
            vo.setLinkType(link.getExternalLinkType());
            vo.setLinkCode(link.getExternalLinkCode());
            docs.add(vo);
            resultCode = ResultUtil.success(docs);
//            link.setViewCount(link.getViewCount() + 1);
            externalLinkMapper.updateByPrimaryKeySelective(link);
            // 添加访问外链的操作日志
            this.addDocumentLog(request, document.getId().toString(), ActionType.ACCESS_LINK, document.getDocumentName(), new Date());
        } catch (Exception e) {
            logger.error("通过链接访问文件失败", e);
            resultCode = ResultUtil.error(Messages.API_LINK_DOC_CODE, Messages.API_LINK_DOC_MSG);
        }
        return resultCode;
    }


    /**
     * 判断token
     *
     * @param authorization
     * @return
     * @throws IOException
     */
    private boolean isAuthorization(String authorization) throws IOException {
        if (StringUtils.isBlank(authorization)) {
            //没有token
            return false;
        }
        ResultCode<Map<String, Long>> resultCode = tokenApi.validateToken(authorization);
        if (resultCode.getCode().equals(Messages.SUCCESS_CODE)) {
            UserInfoRedis userInfo = SpringContextHolder.getBean(UserInfoRedis.class);
            userInfo.setUserId(Integer.parseInt(resultCode.getData().get("userId") + ""));
            userInfo.setIncId(Integer.parseInt(resultCode.getData().get("incId") + ""));
            return true;
        } else {
            logger.warn("token 验证失败{}", JsonMapper.toJsonString(resultCode));
            return false;
        }
    }

    /**
     * 获取外链文件夹中的文件
     *
     * @param
     * @return
     */
    @Override
    public ResultCode<List<DocumentIdLinkVO>> getSubFileLink(Long parentId, String linkCode) {
        ResultCode<List<DocumentIdLinkVO>> result = new ResultCode<>();
        List<DocumentIdLinkVO> documents = new ArrayList<DocumentIdLinkVO>();
        //根据外链资源标识确定外链
        ExternalLink externalLink = documentMapper2.getExterLinkByCode(linkCode);
        if(externalLink == null) {
            result.setCode(Messages.API_LINK_IS_NULL_CODE);
            result.setMsg(Messages.API_LINK_IS_NULL_MSG);
            return result;
        } else if(externalLink.getExternalLinkExpireTime().before(new Date())) {
            result.setCode(Messages.API_LINK_IS_EXPIRE_CODE);
            result.setMsg(Messages.API_LINK_IS_EXPIRE_MSG);
            return result;
        }
        String externalLinkType = externalLink.getExternalLinkType();
        if (externalLinkType.equals("0")) {
            //任何人都能访问，不用关联document_permission表
            documents = this.documentMapper2.getSubFileLinkA(parentId, linkCode);
            logger.debug("document size:"+documents.size());
        }else if (externalLinkType.equals("1")) {
            //企业（incId）内的用户都能访问
            if(UserInfoUtil.getIncorporation()!=null) {
                Long incId = UserInfoUtil.getIncorporation().getId().longValue();
                documents = this.documentMapper2.getSubFileLinkB(parentId, linkCode, incId);
            }
        }else if (externalLinkType.equals("2")) {
            //仅具有访问权限的用户可以访问，根据receiver_type分为0用户、1用户组、2部门三种情况，对应接收者id：用户id或用户组id或部门id
            UserInfoVO userInfoVO = UserInfoUtil.getUserInfoVO();
            List<DocumentPermission> docPers=new ArrayList<DocumentPermission>();
            docPers = documentMapper2.getDocPermissionById(parentId);
            if (!docPers.isEmpty()) {
                for (DocumentPermission dPermission : docPers) {
                    String reType = dPermission.getReceiverType();
                    if (reType.equals("0")) {
                        //receiver_id=user_id用户个人有权限访问
                        Users user = userInfoVO.getUser();
                        Integer userId = user.getUserId();
                        documents = this.documentMapper2.getSubFileLinkC(parentId, linkCode, userId);
                        break;
                    } else if (reType.equals("1")) {
                        //receiver_id=group_id用户所在组有权限访问
                        //用户对应的用户组id
                        List<DocumentIdLinkVO> document = new ArrayList<DocumentIdLinkVO>();
                        List<Group> groups = userInfoVO.getGroupsList();
                        for (Group group : groups) {
                            Integer groupId = group.getId();
                            document = this.documentMapper2.getSubFileLinkD(parentId, linkCode, groupId);
                            documents.addAll(document);
                        }
                        break;
                    } else if (reType.equals("2")) {
                        //receiver_id=department_id用户所在部门有权限访问
                        //用户对应的部门id
                        Department department = userInfoVO.getDepartment();
                        Integer depId = department.getId();
                        documents = this.documentMapper2.getSubFileLinkE(parentId, linkCode, depId);
                        break;
                    }
                }
            }
        }
        result.setData(documents);
        return result;
    }

    @Override
    public ExternalLink getLinkByLinkCode(String linkCode) {
        ExternalLinkExample example = new ExternalLinkExample();
        example.createCriteria().andExternalLinkCodeEqualTo(linkCode);
        List<ExternalLink> links = this.externalLinkMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(links)) {
            return links.get(0);
        } else {
            return null;
        }
    }

    @Override
    public void updateByPrimaryKey(ExternalLink link) {
        if(link != null) {
            this.externalLinkMapper.updateByPrimaryKey(link);
        }
    }

}
