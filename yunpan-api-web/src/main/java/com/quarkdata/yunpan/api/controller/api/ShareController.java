package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.aspect.isDelete.IsDelete;
import com.quarkdata.yunpan.api.aspect.isShare.IsShare;
import com.quarkdata.yunpan.api.message.MessageAnnotation;
import com.quarkdata.yunpan.api.message.MessageType;
import com.quarkdata.yunpan.api.message.MessageUtils;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.request.AddShareObj;
import com.quarkdata.yunpan.api.model.request.Receiver;
import com.quarkdata.yunpan.api.model.vo.DocumentPermissionVO;
import com.quarkdata.yunpan.api.model.vo.ShareAndLinkVO;
import com.quarkdata.yunpan.api.service.DownloadService;
import com.quarkdata.yunpan.api.service.ShareService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author maorl
 * @date 12/6/17 7:16 PM
 */
@Controller
@RequestMapping(RouteKey.PREFIX_API +  RouteKey.SHARE)
public class ShareController extends BaseController{

    @Autowired
    ShareService shareService;
    @Autowired
    DownloadService downloadService;

    @MessageAnnotation
    @ResponseBody
    @RequestMapping(value = "/add_share")
    @IsDelete(ids = "#{addShareObj.documentIds}")
    public ResultCode addShare(@RequestBody AddShareObj addShareObj, HttpServletRequest request) {
        ResultCode result = new ResultCode();
        if(addShareObj == null || (addShareObj != null && addShareObj.getReceiver().size() <= 0)) {
            result.setCode(0);
            result.setData("no share receiver");
            return result;
        }
        List<String> documentIds = addShareObj.getDocumentIds();
        List<Receiver> receiverList = addShareObj.getReceiver();
        List<Receiver> excludeList = addShareObj.getExclusions();
        //获取登录态
        UserInfoVO userInfoVO = UserInfoUtil.getUserInfoVO();
        Users user = userInfoVO.getUser();
        ResultCode resultCode = shareService.addShare(request, documentIds, receiverList, excludeList, user);
        //消息通知
        ArrayList<Long> longs = new ArrayList<>();
        if (documentIds!=null&&documentIds.size()>0){
            for (String s :
                    documentIds) {
                longs.add(Long.valueOf(s));
            }
        }
        MessageUtils.addMessage(request,resultCode.getCode().toString(), MessageType.Share,longs,receiverList);
        return resultCode;
    }

    @ResponseBody
    @RequestMapping(value = "/delete_share")
    @IsShare(shareIds = "#{shareIds}")
    public ResultCode deleteShare(HttpServletRequest request, String shareIds) {
        //获取登录态
        Users user = UserInfoUtil.getUserInfoVO().getUser();
        Integer incId = UserInfoUtil.getIncorporation().getId();
        if(StringUtils.isNotBlank(shareIds)) {
            List<String> ids = Arrays.asList(shareIds.split(","));
            ResultCode resultCode = shareService.deleteShare(Long.parseLong(incId.toString()), user, request, ids);
            return resultCode;
        } else{
            ResultCode<String> resultCode = new ResultCode<>();
            resultCode.setCode(1);
            resultCode.setMsg("no input params");
            return resultCode;
        }
    }

    /**
     * 查询与我共享列表
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/get_share_list")
    public ResultCode getShareList(String documentName, @RequestParam(defaultValue = "0") Long parentId, Integer pageNum, Integer pageSize) {
        UserInfoVO user = UserInfoUtil.getUserInfoVO();
        return shareService.getShareList(user, parentId, documentName, pageNum, pageSize);
    }

    /**
     * 获取分享文件的子文件
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/get_subfile")
    @IsDelete(ids = "#{parentId}")
    public  ResultCode getSubFileList(HttpServletRequest request, String parentId) {
        String documentName=request.getParameter("documentName");
        UserInfoVO user = UserInfoUtil.getUserInfoVO();
        return shareService.getSubFileList(Long.parseLong(UserInfoUtil.getIncorporation().getId().toString()), user,documentName,Long.valueOf(parentId));
    }

    @ResponseBody
    @RequestMapping(value = "/getCount")
    public ResultCode getFileCount(HttpServletRequest request) {
        UserInfoVO user = UserInfoUtil.getUserInfoVO();
        return shareService.getFileCount(user);
    }

    /**
     * 共享人删除自己的文档共享记录
     * @param request
     * @param shareIds
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delete_my_share")
    @MessageAnnotation
    public ResultCode deleteMyShare(HttpServletRequest request, String shareIds) {
        ResultCode<Object> resultCode = new ResultCode<>();
        Integer incId = UserInfoUtil.getIncorporation().getId();
        UserInfoVO userInfoVO = UserInfoUtil.getUserInfoVO();
        if(StringUtils.isNotBlank(shareIds)) {
            try {
                Map<Long, List<DocumentPermissionVO>> messageData = this.shareService.deleteMyShare(request, incId, userInfoVO, shareIds);

                // 发送消息通知
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
        } else {
            resultCode.setCode(1);
            resultCode.setMsg("missing required input params: shareIds");
        }
        return resultCode;
    }

    /**
     * 查询我的共享列表
     * @param documentName
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/get_my_share")
    @ResponseBody
    public ResultCode<List<ShareAndLinkVO>> getMyShare(@RequestParam(defaultValue = "0") Long parentId, String documentName, Integer pageNum, Integer pageSize) {
        ResultCode<List<ShareAndLinkVO>> resultCode = null;
        // 获取当前登录用户信息
        Integer incId = UserInfoUtil.getIncorporation().getId();
        UserInfoVO userInfoVO = UserInfoUtil.getUserInfoVO();
        resultCode = this.shareService.getMyShare(incId, userInfoVO, parentId, documentName, pageNum, pageSize);
        return resultCode;
    }

    /**
     * 根据文档id获取文档共享记录
     * @param docId
     * @return
     */
    @RequestMapping("/record")
    @ResponseBody
    @IsDelete(ids = "#{docId}")
    public ResultCode<List<DocumentPermissionVO>> getShareRecordsByDocumentId(String docId) {
        ResultCode<List<DocumentPermissionVO>> resultCode = new ResultCode<>();
        if(StringUtils.isBlank(docId)) {
            resultCode.setCode(1);
            resultCode.setMsg("missing required input params");
            return resultCode;
        }
        // 获取登录态
        Integer incId = UserInfoUtil.getIncorporation().getId();
        UserInfoVO userInfoVO = UserInfoUtil.getUserInfoVO();
        resultCode = this.shareService.getShareRecordsByDocumentId(incId, userInfoVO, Long.parseLong(docId));
        return resultCode;
    }

    /**
     * 根据parentId获取文件
     * @param parentId
     * @return
     */
    @RequestMapping(value = "/getAllChildren")
    @ResponseBody
    @IsDelete(ids = "#{parentId}")
    public ResultCode<List<Document>> getAllChildren(
            @RequestParam(value = "parentId", required = false, defaultValue = "0") Long parentId){
        Long incId = UserInfoUtil.getIncorporation().getId().longValue();
        UserInfoVO userInfoVO = UserInfoUtil.getUserInfoVO();
        ResultCode<List<Document>> resultCode = null;
        try {
            resultCode = shareService.getAllChildren(userInfoVO, parentId);
        } catch (Exception e) {
            logger.error("get org all children list", e);
            resultCode = new ResultCode<>();
            resultCode.setCode(Messages.API_ERROR_CODE);
            resultCode.setMsg(Messages.API_ERROR_MSG);
        }
        return resultCode;
    }

}
