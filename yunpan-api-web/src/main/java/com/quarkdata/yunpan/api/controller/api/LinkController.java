package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.aspect.checkDocPermission.CheckDocPermission;
import com.quarkdata.yunpan.api.aspect.isDelete.IsDelete;
import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.common.DocumentPermissionConstants;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.vo.ShareAndLinkVO;
import com.quarkdata.yunpan.api.service.DownloadService;
import com.quarkdata.yunpan.api.service.LinkService;
import com.quarkdata.yunpan.api.util.RegUtils;
import com.quarkdata.yunpan.api.util.ResultUtil;
import com.quarkdata.yunpan.api.util.common.Exception.YCException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author maorl
 * @date 12/18/17.
 */
@Controller
@RequestMapping(RouteKey.PREFIX_API + RouteKey.LINK)
public class LinkController extends BaseController {

    @Autowired
    LinkService linkService;
    @Autowired
    DownloadService downloadService;

    /**
     * 创建外链
     *
     * @param request
     * @param documentId
     * @param linkType      0: 所有人
     * @param expireTime    过期时间
     * @param allowPreview  允许预览
     * @param allowDownload 是否允许下载
     * @param isSecret      是否加密
     * @param fetchCode     提取码
     * @param emails        设置外链时可以添加邮箱： 给这些邮箱发送邮件
     * @param telephones    设置外链时可以添加手机号码： 给这些手机号发送短消息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/add_link")
    @IsDelete(ids = "#{documentId}")
    @CheckDocPermission(ids = "#{documentId}", permission = DocumentPermissionConstants.PermissionIndex.LINK)
    public ResultCode addLink(HttpServletRequest request,
                              @RequestParam String documentId,
                              @RequestParam String linkType,
                              @RequestParam Date expireTime,
                              @RequestParam Boolean allowPreview,
                              @RequestParam Boolean allowDownload,
                              @RequestParam Boolean isSecret,
                              @RequestParam String fetchCode,
                              String emails,
                              String telephones) throws MissingServletRequestParameterException {
        if (isSecret && StringUtils.isBlank(fetchCode)) {
            throw new YCException(Messages.Link.MISSING_FETCH_CODE_MSG, Messages.Link.MISSING_FETCH_CODE);
        }
        if (expireTime == null) {
            throw new YCException(Messages.Link.MISSING_EXPIRE_TIME_MSG, Messages.Link.MISSING_EXPIRE_TIME_CODE);
        }
        List<String> emailList = checkEmailFormat(emails);
        List<String> telephoneList = checkTelephoneFormat(telephones);
        ResultCode resultCode = linkService.addLink(request, documentId, linkType, expireTime, allowPreview, allowDownload, isSecret, fetchCode, emailList, telephoneList);
        return resultCode;
    }

    /**
     * 更新外链
     *
     * @param request
     * @param linkId
     * @param expireTime
     * @param allowPreview
     * @param allowDownload
     * @param fetchCode
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @IsDelete(ids = "#{documentId}")
    public ResultCode updateLink(HttpServletRequest request,
                                 @RequestParam String linkId,
                                 Date expireTime,
                                 Boolean allowPreview,
                                 Boolean allowDownload,
                                 Boolean isSecret,
                                 String fetchCode,
                                 String emails,
                                 String telephones) {
        if (expireTime == null && allowPreview == null && allowDownload == null && isSecret == null && StringUtils.isBlank(fetchCode)
                && StringUtils.isBlank(emails) && StringUtils.isBlank(telephones)) {
            throw new YCException(Messages.MISSING_INPUT_MSG, Messages.MISSING_INPUT_CODE);
        }
        if (isSecret != null && isSecret && StringUtils.isBlank(fetchCode)) {
            throw new YCException(Messages.Link.MISSING_FETCH_CODE_MSG, Messages.Link.MISSING_FETCH_CODE);
        }

        List<String> emailList = checkEmailFormat(emails);
        List<String> telephoneList = checkTelephoneFormat(telephones);
        linkService.update(request, linkId, expireTime, allowPreview, allowDownload, isSecret, fetchCode, emailList, telephoneList);
        return ResultUtil.success();
    }


    /**
     * 移出外链
     *
     * @param request
     * @param linkIds
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delete_link")
    public ResultCode deleteLink(HttpServletRequest request, String linkIds) {
        ResultCode resultCode = new ResultCode();
        List<String> ids = null;
        if (StringUtils.isNotBlank(linkIds)) {
            ids = new ArrayList<>();
            for (String id : linkIds.split(",")) {
                ids.add(id);
            }
            resultCode = linkService.deleteLink(request, ids);
        } else {
            resultCode.setCode(1);
            resultCode.setMsg("missing required input params: linkIds");
        }
        return resultCode;
    }

    /**
     * 查询我的外链列表
     *
     * @param parentId
     * @param documentName
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/get_links")
    public ResultCode<List<ShareAndLinkVO>> getLink(@RequestParam(defaultValue = "0") Long parentId,
                                                    @RequestParam(defaultValue = "0", required = false) Long docId,
                                                    String documentName,
                                                    Integer pageNum,
                                                    Integer pageSize) {
        UserInfoVO users = UserInfoUtil.getUserInfoVO();
        return linkService.getLinks(users, parentId, docId, documentName, pageNum, pageSize);
    }

    /**
     * 校验手机号格式是否正确
     *
     * @param telephones
     * @return
     */
    private List<String> checkTelephoneFormat(String telephones) {
        List<String> telephoneList = new ArrayList<>();
        if (StringUtils.isNotBlank(telephones)) {
            for (String telephone : telephones.split(",")) {
                if (StringUtils.isNotBlank(telephone)) {
                    if (!RegUtils.isMatch(telephone.trim(), RegUtils.REGEX_MOBILE)) {
                        throw new YCException(Messages.Link.TELEPHONE_FORMAT_NOT_CORRECT_MSG + ": " + telephone, Messages.Link.TELEPHONE_FORMAT_NOT_CORRECT_CODE);
                    } else {
                        telephoneList.add(telephone.trim());
                    }
                }
            }
        }
        return telephoneList;
    }

    /**
     * 校验邮箱格式是否正确
     *
     * @param emails
     * @return
     */
    private List<String> checkEmailFormat(String emails) {
        List<String> emailList = new ArrayList<>();
        if (StringUtils.isNotBlank(emails)) {
            for (String email : emails.split(",")) {
                if (StringUtils.isNotBlank(email)) {
                    if (!RegUtils.isMatch(email.trim(), RegUtils.REGEX_MAIL)) {
                        throw new YCException(Messages.Link.EMAIL_FORMAT_NOT_CORRECT_MSG + ": " + email, Messages.Link.EMAIL_FORMAT_NOT_CORRECT_CODE);
                    } else {
                        emailList.add(email.trim());
                    }
                }
            }
        }
        return emailList;
    }


}
