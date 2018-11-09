package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.IncUserConfig;
import com.quarkdata.yunpan.api.model.vo.MessageExtend;
import com.quarkdata.yunpan.api.model.vo.MessageVo;
import com.quarkdata.yunpan.api.service.MessageService;
import com.quarkdata.yunpan.api.util.ResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.management.relation.RelationSupport;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(RouteKey.PREFIX_API+RouteKey.MESSAGE)
public class MessageController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;

    @ResponseBody
    @RequestMapping(value = "/list")
    public ResultCode findMessage(HttpServletRequest request) {
        ResultCode result = new ResultCode();
        try {
            // token中获取用户信息
            UserInfoVO uservo = UserInfoUtil.getUserInfoVO();
            Users user = uservo.getUser();
//            List<MessageExtend> messageList = messageService.getMessageList(user.getUserId().longValue(), user.getIncid().longValue());

            List<MessageExtend> messageList = messageService.getAllMessageList(user.getUserId().longValue(), user.getIncid().longValue());
            
            IncUserConfig incUserConfig = messageService.getUserType(user.getUserId().longValue(), user.getIncid().longValue());
            MessageVo messageVo = new MessageVo();
            messageVo.setMessageList(messageList);
            if (incUserConfig != null) {
                messageVo.setMsgValue(("1".equals(incUserConfig.getIsReceiveMessage())) ? true : false);
                messageVo.setEmailValue(("1".equals(incUserConfig.getIsReceiveEmailMessage())) ? true : false);
            } else {
                incUserConfig = new IncUserConfig();
                incUserConfig.setUserId(user.getUserId().longValue());
                incUserConfig.setIncId(user.getIncid().longValue());
                incUserConfig.setIsReceiveMessage("1");
                incUserConfig.setIsReceiveEmailMessage("1");
                messageService.toIncUserConfig(incUserConfig);
                messageVo.setEmailValue(true);
                messageVo.setMsgValue(true);
            }
            result.setCode(Messages.SUCCESS_CODE);
            result.setMsg(Messages.SUCCESS_MSG);
            result.setData(messageVo);
            logger.info("消息查询成功");
        } catch (Exception e) {
            logger.error("消息查询失败", e);
            result.setCode(Messages.API_GET_MESSAGE_LIST_CODE);
            result.setMsg(Messages.API_GET_MESSAGE_LIST_MSG);
            return result;
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/update")
    public ResultCode updateValue(HttpServletRequest request) {
        ResultCode result = new ResultCode();
        // token中获取用户信息
        UserInfoVO uservo = UserInfoUtil.getUserInfoVO();
        Users user = uservo.getUser();

        try {
            String msgValue = request.getParameter("msgValue");
            String emailValue = request.getParameter("emailValue");
            IncUserConfig incUserConfig =
                    messageService.getUserType(user.getUserId().longValue(), user.getIncid().longValue());
            if (msgValue != null) {
                if ("true".equals(msgValue)) {
                    incUserConfig.setIsReceiveMessage("1");
                } else if ("false".equals(msgValue)) {
                    incUserConfig.setIsReceiveMessage("0");
                }
            } else if (emailValue != null) {
                if ("true".equals(emailValue)) {
                    incUserConfig.setIsReceiveEmailMessage("1");
                } else if ("false".equals(emailValue)) {
                    incUserConfig.setIsReceiveEmailMessage("0");
                }
            }
            messageService.updateUserType(incUserConfig);
            result.setCode(Messages.SUCCESS_CODE);
            result.setMsg(Messages.SUCCESS_MSG);
            logger.info("修改用户接受消息状态成功");

           /* //测试消息发送
            ArrayList<Long>  list= new ArrayList<>();
            list.add(212L);
            ArrayList<Receiver> list1  = new ArrayList<>();
            Receiver receiver = new Receiver();
            receiver.setRecId("52");
            receiver.setRecType("0");
            receiver.setPermission("0");
            list1.add(receiver);
            MessageUtils.addMessage(request,"0", MessageType.UPDATE_TYPE,list,list1);*/
        } catch (Exception e) {
            logger.error("修改用户接受消息状态失败", e);
            result.setCode(Messages.API_UPDATE_MESSAGE_TYPE_CODE);
            result.setMsg(Messages.API_UPDATE_MESSAGE_TYPE_MSG);
            return result;
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/read")
    public ResultCode delMsg(HttpServletRequest request) {
        ResultCode result = new ResultCode();
        try {
            // token中获取用户信息
            UserInfoVO uservo = UserInfoUtil.getUserInfoVO();
            Users user = uservo.getUser();
            String msgIds = request.getParameter("msgIds");
            if (StringUtils.isNotBlank(msgIds)){
                messageService.updateIsRead(user.getIncid().longValue(),user.getUserId().longValue(), msgIds);
            }
            result.setCode(Messages.SUCCESS_CODE);
            result.setMsg(Messages.SUCCESS_MSG);
        }catch (Exception e){
            logger.error("修改未读消息错误", e);
            result.setCode(Messages.API_UPDATE_MESSAGE_IS_READ_CODE );
            result.setMsg(Messages.API_UPDATE_MESSAGE_IS_READ_MSG);
        }

        return result;
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public ResultCode<String> delMsgBatch(HttpServletRequest request, String msgIds) {
        ResultCode<String> resultCode = new ResultCode<>();
        if(StringUtils.isBlank(msgIds)) {
            resultCode.setCode(Messages.MISSING_INPUT_CODE);
            resultCode.setMsg(Messages.MISSING_INPUT_MSG);
            return resultCode;
        }
        Integer incId = UserInfoUtil.getIncorporation().getId();
        Users user = UserInfoUtil.getUserInfoVO().getUser();
        try {
            this.messageService.delMsgBatch(incId, user, msgIds);
        } catch (Exception e) {
            resultCode.setCode(1);
            resultCode.setMsg("delete message error");
            logger.error("<<<<<< delete message error", e);
        }
        return resultCode;
    }

    @RequestMapping(value = "/not_read_list")
    @ResponseBody
    public ResultCode<Integer> getNotReadList(){
        try {
            Integer incId = UserInfoUtil.getIncorporation().getId();
            Users user = UserInfoUtil.getUserInfoVO().getUser();
            Integer notReadList = messageService.getNotReadList(incId, user);
            return ResultUtil.success(notReadList);
        }catch (Exception e){
            return ResultUtil.error(Messages.GET_NOT_READ_LIST_CODE,Messages.GET_NOT_READ_LIST_MSG);
        }
    }
}
