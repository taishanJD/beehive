package com.quarkdata.yunpan.api.message;

import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.request.Receiver;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

//@Controller
@RequestMapping(RouteKey.PREFIX_API + "/message")
public class MessageControllerDemo {

    @RequestMapping("/add")
    // ----------在需要消息通知的方法上添加此注解------------
    @MessageAnnotation
    @ResponseBody
    public ResultCode<String> addMessage(HttpServletRequest request) {
        ResultCode<String> result = new ResultCode<>();

        // 1.......逻辑代码......

        //  2在返回之前调用工具类传入参数
        String notice=null;//"1"操作不成功   "0"操作成功   相当于result.getCode()的值

        String messageType=MessageType.Archival;//操作的类型

        List<Long> documentIds=null;//操作的文档集合，单条文档或多条文档

        List< Receiver > receiverList=null;//消息通知需要提醒的人 Receiver类型{recId:接受者id
        // recType：接受者类型     permission:给与的权限(0只读  1读写  2管理  3(用于不显示权限的消息))}

        MessageUtils.addMessage(request,notice,messageType,documentIds,receiverList);

        return result;

    }
}
