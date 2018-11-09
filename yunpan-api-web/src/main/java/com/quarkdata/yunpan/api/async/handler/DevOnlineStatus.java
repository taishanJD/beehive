package com.quarkdata.yunpan.api.async.handler;

import com.quarkdata.yunpan.api.async.EventConsumer;
import com.quarkdata.yunpan.api.model.common.PushConstant;
import com.quarkdata.yunpan.api.service.PushService;
import com.quarkdata.yunpan.api.util.JedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 处理设备状态
 * author:liuda
 * Date:2018/9/26
 * Time:14:07
 */
public class DevOnlineStatus {

    private static final Logger logger = LoggerFactory.getLogger(DevOnlineStatus.class);

    @Autowired
    private PushService pushService;

    @Value("${push_thread_num}")
    private Integer poolSize;

    public void createThread() throws Exception {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(poolSize);

        for (int i = 0; i < poolSize; i++) {
            final int theadIndex = i;
            fixedThreadPool.execute(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    while (true) {
//                        logger.info("fixedThreadPool onlineStatus " + theadIndex);
                        long beforeRead = System.currentTimeMillis();
                        Map<String, Object> readRedisResult = JedisUtils
                                .getLPOP(PushConstant.REDIS_DEVICE_ONLINE_STATUS_CHANGE_LIST);
                        long afterRead = System.currentTimeMillis();
//                        logger.info("fixedThreadPool onlineStatus " + theadIndex + " read spends : "
//                                + (afterRead - beforeRead) + " ms");
                        boolean isSleep = false;
                        switch ((Integer) readRedisResult.get("result")) {
                            case 1:
                                String onlineStatus = (String) readRedisResult.get("message");
                                if (StringUtils.isEmpty(onlineStatus)) {
                                    // logger.error("fixedThreadPool onlineStatus " + theadIndex + "
                                    // read null");
                                    isSleep = true;
                                    break;
                                }
                                try {
                                    String[] onlineArr = onlineStatus.split(",");
                                    boolean isOnline = false;
                                    switch (onlineArr[0]) {
                                        case "online":
                                            isOnline = true;
                                            break;
                                        case "offline":
                                            break;
                                        default:
                                    }
                                    pushService.updateDevOnlineStatus(onlineArr[1], isOnline,
                                            Long.parseLong(onlineArr[2]));
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    logger.error("fixedThreadPool onlineStatus " + theadIndex
                                            + " data : " + onlineStatus);
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                logger.error("fixedThreadPool onlineStatus " + theadIndex
                                        + " read error : " + readRedisResult);
                        }
                        if (isSleep) {
                            try {
                                Thread.sleep(1000L);
//                                logger.info("fixedThreadPool onlineStatus " + theadIndex
//                                        + " read null and sleep");
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                logger.error("fixedThreadPool onlineStatus sleep error");
                            }
                        }
                    }
                }
            });
        }
    }
}
