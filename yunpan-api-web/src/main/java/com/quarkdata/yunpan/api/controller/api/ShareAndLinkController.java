package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.message.MessageAnnotation;
import com.quarkdata.yunpan.api.message.MessageType;
import com.quarkdata.yunpan.api.message.MessageUtils;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.vo.DocumentPermissionVO;
import com.quarkdata.yunpan.api.model.vo.ShareAndLinkVO;
import com.quarkdata.yunpan.api.service.ShareAndLinkService;
import com.quarkdata.yunpan.api.service.ShareService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by yanyq1129@thundersoft.com on 2018/5/18.
 */
@Component
@RequestMapping(RouteKey.PREFIX_API + "/my_share")
public class ShareAndLinkController {

    @Autowired
    private ShareAndLinkService shareAndLinkService;

    @Autowired
    private ShareService shareService;

    /**
     * 获取我的分享列表和我的外链列表
     * @return
     */
    @RequestMapping(value = "/share_and_link_list", method = RequestMethod.GET)
    @ResponseBody
    public ResultCode<List<ShareAndLinkVO>> getShareAndLinkList(Long parentId, String documentName, Integer pageNum, Integer pageSize) {
        ResultCode<List<ShareAndLinkVO>> resultCode = null;
        // 获取当前登录用户信息
        Integer incId = UserInfoUtil.getIncorporation().getId();
        UserInfoVO userInfoVO = UserInfoUtil.getUserInfoVO();
        resultCode = this.shareAndLinkService.getShareAndLinkList(incId, userInfoVO, parentId, documentName, pageNum, pageSize);
        return resultCode;
    }


    /**
     * 分享人删除自己分享或外链的记录
     * @param request
     * @param shareIds
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delete_my_share_and_link")
    @MessageAnnotation
    public ResultCode deleteMyShareAndLink(HttpServletRequest request, String shareIds, String linkIds) {
        ResultCode<Object> resultCode = new ResultCode<>();
        Integer incId = UserInfoUtil.getIncorporation().getId();
        UserInfoVO userInfoVO = UserInfoUtil.getUserInfoVO();
        if(StringUtils.isBlank(shareIds) && StringUtils.isBlank(linkIds)) {
            resultCode.setCode(1);
            resultCode.setMsg("missing required input params");
        } else {
            try {
                Map<Long, List<DocumentPermissionVO>> messageData = this.shareAndLinkService.deleteMyShareAndLink(request, incId, userInfoVO, shareIds, linkIds);

                // 取消分享发送消息通知
                if(MapUtils.isNotEmpty(messageData)) {
                    for(Map.Entry<Long, List<DocumentPermissionVO>> entry: messageData.entrySet()) {
                        MessageUtils.createMessage(request, Messages.SUCCESS_CODE, MessageType.delete_Share, entry.getKey(), entry.getValue());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                resultCode.setCode(1);
                resultCode.setMsg("delete share failure");
            }
        }
        return resultCode;
    }

}
