package com.quarkdata.yunpan.api.async;

import com.alibaba.fastjson.JSONObject;
import com.quarkdata.yunpan.api.util.JedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by liuda
 * Date : 2018/3/6
 */
public class EventProducer {
    private static final Logger logger = LoggerFactory.getLogger(EventProducer.class);

    public boolean fireEvent(EventModel eventModel){
        try {
            //序列化
            String json = JSONObject.toJSONString(eventModel);
            //产生key
            String key = RedisListKey.getEventQueueKey();
            //放入工作队列中
            JedisUtils.lpush(key, json);
            logger.info("生产redis信息");
            return true;
        }catch (Exception e){
            logger.error("EventProducer fireEvent 异常 ：" + e.getMessage());
            return false;
        }

    }
}
