package com.quarkdata.yunpan.api.async;

/**
 * Created by liuda
 * Date : 2018/3/6
 */
public class RedisListKey {
    private static String EVENT_KEY = "os:message:ASYNC_EVENT_KEY";

    public static String getEventQueueKey(){
        return EVENT_KEY;
    }
}
