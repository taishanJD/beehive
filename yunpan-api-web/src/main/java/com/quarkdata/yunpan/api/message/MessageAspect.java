package com.quarkdata.yunpan.api.message;


import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.async.EventModel;
import com.quarkdata.yunpan.api.async.EventProducer;
import com.quarkdata.yunpan.api.async.handler.MailHandler;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.request.Receiver;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author liuda1211
 * @Description 消息通知切面类
 */
@Component
@Aspect
public class MessageAspect {

    @Autowired
    EventProducer eventProducer;

    @Autowired
    private MailHandler mailHandler;
    /**
     * 日志记录
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageAspect.class);

    class Task implements Runnable {

        private Users user;
        private String notice;
        private String messageType;
        private List<Long> documentIds;
        private List<Receiver> receiverList;

        public Task(Users user, String notice, String messageType, List<Long> documentIds, List<Receiver> receiverList) {
            this.user = user;
            this.notice = notice;
            this.messageType = messageType;
            this.documentIds = documentIds;
            this.receiverList = receiverList;
        }

        @Override
        public void run() {
            try {
//                eventProducer.fireEvent(new EventModel(this.user,this.notice,this.messageType,this.documentIds,this.receiverList));
                mailHandler.doHandler(new EventModel(this.user,this.notice,this.messageType,this.documentIds,this.receiverList));
            }catch (Exception e){
                LOGGER.error("消息推送执行失败: " + e.getMessage());
            }
        }

    }
    @After(value = "@annotation(com.quarkdata.yunpan.api.message.MessageAnnotation) ")
    public void after(JoinPoint joinPoint){
        try {
            // 1.从登录态中获取用户信息
            // 获取当前用户信息（包括：用户、用户组、部门、角色）
            UserInfoVO userInfoVO = UserInfoUtil.getUserInfoVO();
            Users user = userInfoVO.getUser();
            //从request域中读取消息通知
            // 信息
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .getRequest();
            String notice = (String)request.getAttribute("notice ");
            String messageType = (String)request.getAttribute("MessageType");
            List<Long> documentIds = (List<Long>)request.getAttribute("documentIds");
            List<Receiver> receiverList = (List<Receiver>)request.getAttribute("receiverList");

            ExecutorService executor = Executors.newCachedThreadPool();
            Task task = new Task(user, notice, messageType, documentIds, receiverList);
            executor.execute(task);
        } catch (Exception e) {
            LOGGER.error("消息推送后置通知异常!", e);
        }
    }


}
