package com.quarkdata.yunpan.api.async;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.quarkdata.yunpan.api.async.handler.MailHandler;
import com.quarkdata.yunpan.api.model.request.Receiver;
import com.quarkdata.yunpan.api.util.JedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuda
 * Date : 2018/3/6
 */
public class EventConsumer implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);


    @Autowired
    private MailHandler mailHandler;

    @Override
    public void afterPropertiesSet() throws Exception {
        //启动线程从工作队列中取出事件进行处理
        try{
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("Redis消息处理任务启动完成 ");
                while (true){
                    String key = RedisListKey.getEventQueueKey();
                    //获取存储的(经过序列化的)事件event
                    List<String> events = JedisUtils.brpop(0, key);
                    System.out.println(events);
                        for (String jsonEvent : events){
                            if (jsonEvent.equals(key)){
                                continue;
                            }
                            EventModel eventModel = JSON.parseObject(jsonEvent, EventModel.class);
                            mailHandler.doHandler(eventModel);
                            logger.info("消费redis取出消息");
                        }
                }
            }
        });
//        thread.start();
        }catch (Exception e){
            logger.error("消息队列，redis读取后,mailHandler处理失败");
            logger.error("异常信息"+e);
        }
    }
    public static void main(String[] args){
        /**
         * 简单测试
         */
        String key = RedisListKey.getEventQueueKey();
        Receiver receiver = new Receiver();
        receiver.setRecId("52");
        receiver.setRecType("user");
        receiver.setPermission("0");
        List<Receiver> list= new ArrayList<>();
        list.add(receiver);
        String json = JSONObject.toJSONString(new EventModel(null,"0",null,null,list));
        System.out.println(json);
        EventModel eventModel = JSON.parseObject(json, EventModel.class);
        System.out.println(eventModel);
    }

}

