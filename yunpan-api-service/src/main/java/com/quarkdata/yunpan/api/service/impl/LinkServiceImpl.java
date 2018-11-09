package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.dal.dao.*;
import com.quarkdata.yunpan.api.model.common.ActionType;
import com.quarkdata.yunpan.api.model.common.DocumentConstants;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.dataobj.ExternalLink;
import com.quarkdata.yunpan.api.model.dataobj.ExternalLinkMsgNotification;
import com.quarkdata.yunpan.api.model.dataobj.ExternalLinkMsgNotificationExample;
import com.quarkdata.yunpan.api.model.vo.ShareAndLinkVO;
import com.quarkdata.yunpan.api.service.LinkService;
import com.quarkdata.yunpan.api.util.EmailUtil;
import com.quarkdata.yunpan.api.util.RestDateUtils;
import com.quarkdata.yunpan.api.util.ResultUtil;
import com.quarkdata.yunpan.api.util.SmsUtils;
import com.quarkdata.yunpan.api.util.common.Exception.YCException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author maorl
 * @date 12/18/17.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class LinkServiceImpl extends BaseLogServiceImpl implements LinkService {
    private static Logger logger = LoggerFactory.getLogger(LinkServiceImpl.class);
    @Autowired
    ExternalLinkMapper externalLinkMapper;
    @Autowired
    ExternalLinkMapper2 externalLinkMapper2;

    @Autowired
    private ExternalLinkMsgNotificationMapper externalLinkMsgNotificationMapper;

    @Value("${onShare.web.path}")
    private String OS_WEB_PATH;

    @Autowired
    private ThreadPoolTaskExecutor sendTaskExecutor;

    @Autowired
    private DocumentMapper documentMapper;

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
     * 添加外链
     *
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
    @Override
    public ResultCode addLink(HttpServletRequest request, String documentId, String linkType, Date expireTime, Boolean allowPreview, Boolean allowDownload, Boolean isSecret, final String fetchCode, final List<String> emails, final List<String> telephones) {
        logger.info("插入外链文档id:" + documentId);
        UserInfoVO userInfoVO = UserInfoUtil.getUserInfoVO();
        ResultCode resultCode;
        try {
            ExternalLink link = new ExternalLink();
            link.setDocumentId(Long.valueOf(documentId));
            link.setIncId(Long.valueOf(userInfoVO.getUser().getIncid()));
            final String code = getLinkCode();
            link.setExternalLinkCode(code);
            link.setExternalLinkType(linkType);
            final String userName = userInfoVO.getUser().getUserName();
            link.setCreateUsername(userName);
            link.setCreateTime(new Date());
            link.setCreateUser(Long.valueOf(userInfoVO.getUser().getUserId()));
            link.setExternalLinkExpireTime(expireTime);
            if (!allowDownload) {
                link.setAllowDownload(DocumentConstants.Link.ALLOW_DOWNLOAD_NO);
            }
            if (!allowPreview) {
                link.setAllowPreview(DocumentConstants.Link.ALLOW_PREVIEW_NO);
            }
            if (isSecret && StringUtils.isNotBlank(fetchCode)) {
                link.setIsSecret(DocumentConstants.Link.IS_SECRET_YES);
                link.setFetchCode(fetchCode);
            }

            externalLinkMapper.insertSelective(link);
            insertLinkNotification(link.getId(), emails, telephones);

            // 发送邮件和短消息
            notification(isSecret, fetchCode, emails, telephones, code, userName);

            resultCode = ResultUtil.success(code);

            logger.info("插入外链成功");

            // 记录操作日志
            Document document = this.documentMapper.selectByPrimaryKey(Long.parseLong(documentId));
            this.addDocumentLog(request, document.getId().toString(), ActionType.OUTERLINK, document.getDocumentName(), link.getCreateTime());

        } catch (Exception e) {
            logger.error("insert link failed", e);
            resultCode = ResultUtil.error(Messages.API_ADD_LINK_FAILED_CODE, Messages.API_ADD_LINK_FAILED_MSG);
        }
        return resultCode;
    }

    /**
     * 删除外链
     *
     * @param request
     * @param linkIds
     * @return
     */
    @Override
    public ResultCode deleteLink(HttpServletRequest request, List<String> linkIds) {
        ResultCode resultCode;
        try {
            for (String id : linkIds) {
                // 记录操作日志
                Document document = this.documentMapper.selectByPrimaryKey(this.externalLinkMapper.selectByPrimaryKey(Long.parseLong(id)).getDocumentId());
                this.addDocumentLog(request, document.getId().toString(), ActionType.DELETE_OUTERLINK, document.getDocumentName(), new Date());

                // 刪除通知记录
                ExternalLinkMsgNotificationExample example_notification = new ExternalLinkMsgNotificationExample();
                example_notification.createCriteria().andIncIdEqualTo(UserInfoUtil.getIncId()).andLinkIdEqualTo(Long.parseLong(id));
                this.externalLinkMsgNotificationMapper.deleteByExample(example_notification);

                externalLinkMapper.deleteByPrimaryKey(Long.valueOf(id));
                logger.info("删除外链，id：" + id);

            }
            resultCode = ResultUtil.success();
        } catch (NumberFormatException e) {
            logger.error("删除外链失败", e);
            resultCode = ResultUtil.error(Messages.API_DEL_LINK_FAILED_CODE, Messages.API_DEL_LINK_FAILED_MSG);
        }
        return resultCode;
    }

    /**
     * 获取链接列表
     *
     * @param users
     * @param parentId
     * @param docId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ResultCode<List<ShareAndLinkVO>> getLinks(UserInfoVO users, Long parentId, Long docId, String documentName, Integer pageNum, Integer pageSize) {
        ResultCode<List<ShareAndLinkVO>> resultCode;
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("incId", users.getUser().getIncid());
            params.put("userId", users.getUser().getUserId());
            params.put("parentId", 0 == parentId ? null : parentId);
            params.put("docId", 0 == docId ? null : docId);
            params.put("documentName", StringUtils.isBlank(documentName) ? null : documentName);
            if (pageNum != null && pageNum > 0 && pageSize != null && pageSize > 0) {
                params.put("startNum", (pageNum - 1) * pageSize);
                params.put("pageSize", pageSize);
            } else {
                params.put("startNum", null);
                params.put("pageSize", null);
            }
            List<ShareAndLinkVO> links = externalLinkMapper2.getLinkVOList(params);
            if (links != null && links.size() > 0) {
                for (ShareAndLinkVO shareAndLinkVO : links) {
                    if (shareAndLinkVO.getExpirationTime() != null && !shareAndLinkVO.getExpirationTime().equals("-1")) {
                        shareAndLinkVO.setDisplayTime(RestDateUtils.getRestTime(shareAndLinkVO.getExpirationTime()));
                    }
                }
            }
            resultCode = ResultUtil.success(links);
        } catch (Exception e) {
            logger.error("获取链接列表失败", e);
            resultCode = ResultUtil.error(Messages.API_GET_MY_LINK_CODE, Messages.API_GET_MY_LINK_MSG);
        }
        return resultCode;
    }

    @Override
    public void update(HttpServletRequest request, String linkId, Date expireTime, Boolean allowPreview, Boolean allowDownload, Boolean isSecret, String fetchCode, List<String> emailList, List<String> telephoneList) {
        ExternalLink externalLink = this.externalLinkMapper.selectByPrimaryKey(Long.parseLong(linkId));
        if (externalLink == null) {
            throw new YCException(Messages.Link.NOT_EXIST_MSG, Messages.Link.NOT_EXIST_CODE);
        }

        ExternalLink record = new ExternalLink();
        record.setId(Long.parseLong(linkId));
        record.setExternalLinkExpireTime(expireTime);
        if (allowPreview != null && allowPreview) {
            record.setAllowPreview(DocumentConstants.Link.ALLOW_PREVIEW_YES);
        } else {
            record.setAllowPreview(DocumentConstants.Link.ALLOW_PREVIEW_NO);
        }
        if (allowDownload != null && allowDownload) {
            record.setAllowDownload(DocumentConstants.Link.ALLOW_DOWNLOAD_YES);
        } else {
            record.setAllowDownload(DocumentConstants.Link.ALLOW_DOWNLOAD_NO);
        }
        if (isSecret != null && isSecret && StringUtils.isNotBlank(fetchCode)) {
            record.setIsSecret(DocumentConstants.Link.IS_SECRET_YES);
            record.setFetchCode(fetchCode);
        } else {
            record.setIsSecret(DocumentConstants.Link.IS_SECRET_NO);
            record.setFetchCode("");
        }
        this.externalLinkMapper.updateByPrimaryKeySelective(record);

        // 1.判断提取码是否发生变化, 如果发生变化给原来的通知接收人发送通知
        String isSecret_primary = externalLink.getIsSecret();
        String fetchCode_primary = externalLink.getFetchCode();
        List<String> emailList_primary = new ArrayList<>();
        List<String> telephoneList_primary = new ArrayList<>();

        ExternalLinkMsgNotificationExample example = new ExternalLinkMsgNotificationExample();
        example.createCriteria().andIncIdEqualTo(UserInfoUtil.getIncId()).andLinkIdEqualTo(Long.parseLong(linkId));
        List<ExternalLinkMsgNotification> externalLinkMsgNotifications = this.externalLinkMsgNotificationMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(externalLinkMsgNotifications)) {
            for (ExternalLinkMsgNotification externalLinkMsgNotification : externalLinkMsgNotifications) {
                if(DocumentConstants.Link.NOTIFICATION_RECEIVE_TYPE_EMAIL.equals(externalLinkMsgNotification.getReceiverType())) {
                    emailList_primary.add(externalLinkMsgNotification.getReceiverDetail());
                } else if(DocumentConstants.Link.NOTIFICATION_RECEIVE_TYPE_TELEPHONE.equals(externalLinkMsgNotification.getReceiverType())){
                    telephoneList_primary.add(externalLinkMsgNotification.getReceiverDetail());
                }
            }
        }

        if (DocumentConstants.Link.IS_SECRET_YES.equals(isSecret_primary)) {
            // 原先有加密
            if (isSecret != null && isSecret && !fetchCode.equals(fetchCode_primary)) {
                // 提取码变化, 通知原来通知到的用户
                this.notification(isSecret, fetchCode, emailList_primary, telephoneList_primary, externalLink.getExternalLinkCode(), UserInfoUtil.getUserName());
            }
        } else {
            // 原先未加密
            if (isSecret != null && isSecret) {
                // 现在加密了,得通知原来通知到的用户
                this.notification(isSecret, fetchCode, emailList, telephoneList, externalLink.getExternalLinkCode(), UserInfoUtil.getUserName());
            }
        }

        // 2.通知新加用户
        List<String> emailList_newAdded = new ArrayList<>();
        List<String> telephoneList_newAdded = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(emailList)) {
            Collection subtract = CollectionUtils.subtract(emailList, emailList_primary);
            emailList_newAdded.addAll(subtract);
        }
        if(CollectionUtils.isNotEmpty(telephoneList)) {
            Collection subtract = CollectionUtils.subtract(telephoneList, telephoneList_primary);
            telephoneList_newAdded.addAll(subtract);
        }
        this.notification(isSecret, fetchCode, emailList_newAdded, telephoneList_newAdded, externalLink.getExternalLinkCode(), UserInfoUtil.getUserName());

        // 3.插入新添加通知记录
        this.insertLinkNotification(Long.parseLong(linkId), emailList_newAdded, telephoneList_newAdded);
    }


    private String getLinkCode() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 发送外链通知
     *
     * @param isSecret
     * @param fetchCode
     * @param emails
     * @param telephones
     * @param code
     * @param userName
     */
    private void notification(Boolean isSecret, final String fetchCode, final List<String> emails, final List<String> telephones, final String code, final String userName) {
        if (isSecret!= null && isSecret) {
            if (CollectionUtils.isNotEmpty(emails)) {
                final String[] emailArr = new String[emails.size()];
                final String bodyText = "<h3>您好，" + userName + "与您共享文件，秘钥为 " + "<span style=\"color: #0A5B9A; font-size: large\">"
                        + fetchCode + "</span>，请点此链接查看：</h3><a href=\"" + this.OS_WEB_PATH + "/s/" + code + "\">" + this.OS_WEB_PATH + "/s/" + code + "</a>";
                final String subject = "【Beehive企业云盘】";
                this.sendTaskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        EmailUtil.sendRichContent(emails.toArray(emailArr), bodyText, subject, null, null);
                    }
                });
            }
            if (CollectionUtils.isNotEmpty(telephones)) {
                this.sendTaskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        SmsUtils.sendSaiYouSmsOfLink(StringUtils.join(telephones, ","), userName, OS_WEB_PATH + "/s/" + code, true, fetchCode);
                    }
                });
            }
        } else {
            if (CollectionUtils.isNotEmpty(emails)) {
                final String[] emailArr = new String[emails.size()];
                final String bodyText = "<h3>您好，" + userName + "与您共享文件，请点此链接查看：</h3><a href=\"" + this.OS_WEB_PATH + "/s/" + code + "\">" + this.OS_WEB_PATH + "/s/" + code + "</a>";
                final String subject = "【Beehive企业云盘】";
                this.sendTaskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        EmailUtil.sendRichContent(emails.toArray(emailArr), bodyText, subject, null, null);
                    }
                });
            }
            if (CollectionUtils.isNotEmpty(telephones)) {
                this.sendTaskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        SmsUtils.sendSaiYouSmsOfLink(StringUtils.join(telephones, ","), userName, OS_WEB_PATH + "/s/" + code, false, null);
                    }
                });
            }
        }
    }

    /**
     * 插入外链通知记录
     * @param linkId
     * @param emails
     * @param telephones
     */
    private void insertLinkNotification(Long linkId, List<String> emails, List<String> telephones) {
        if (CollectionUtils.isNotEmpty(emails)) {
            for (String email : emails) {
                ExternalLinkMsgNotification link_notification_email = new ExternalLinkMsgNotification();
                link_notification_email.setIncId(UserInfoUtil.getIncId());
                link_notification_email.setLinkId(linkId);
                link_notification_email.setReceiverType(DocumentConstants.Link.NOTIFICATION_RECEIVE_TYPE_EMAIL);
                link_notification_email.setReceiverDetail(email);
                try {
                    this.externalLinkMsgNotificationMapper.insertSelective(link_notification_email);
                } catch (Exception e) {
                    logger.error("外链通知记录已存在");
                }
            }
        }
        if (CollectionUtils.isNotEmpty(telephones)) {
            for (String telephone : telephones) {
                ExternalLinkMsgNotification link_notification_telephone = new ExternalLinkMsgNotification();
                link_notification_telephone.setIncId(UserInfoUtil.getIncId());
                link_notification_telephone.setLinkId(linkId);
                link_notification_telephone.setReceiverType(DocumentConstants.Link.NOTIFICATION_RECEIVE_TYPE_TELEPHONE);
                link_notification_telephone.setReceiverDetail(telephone);
                try {
                    this.externalLinkMsgNotificationMapper.insertSelective(link_notification_telephone);
                } catch (Exception e) {
                    logger.error("外链通知记录已存在");
                }
            }
        }
    }

    /**
     * 获取将来日期
     *
     * @param d
     * @param day
     * @return
     */
    private Date getDateAfter(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }
}
