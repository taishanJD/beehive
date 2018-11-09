package com.quarkdata.yunpan.api.util;

import com.alibaba.fastjson.JSON;
import com.quarkdata.yunpan.api.model.common.AndroidPush;
import com.quarkdata.yunpan.api.model.common.IosPush;
import com.quarkdata.yunpan.api.model.common.PushConstant;
import com.quarkdata.yunpan.api.model.common.WebPush;
import com.quarkdata.yunpan.api.model.dataobj.MessagePushChannel;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * author:liuda
 * Date:2018/9/13
 * Time:17:01
 */
public class PushUtils {

    /**
     * 批量推送
     * @param messagePushChannels 设备对象
     * @param command 指令标识
     * @param params 额外数据
     */
    public static void pushAndroid(List<MessagePushChannel> messagePushChannels, String command ,Map<String, String> params) {
        if (CollectionUtils.isNotEmpty(messagePushChannels)){
            ArrayList<AndroidPush> androidPushes = new ArrayList<>();
            for (MessagePushChannel mc :
                    messagePushChannels) {
                if (mc.getClientType().equals(PushConstant.CLIENT_TYPE_ANDROID)){
                    AndroidPush androidPush = new AndroidPush(mc.getToken(),command,params,null,null);
                    androidPushes.add(androidPush);
                }
            }
            sendAndroidPushEntity(androidPushes);
        }
    }

    private static boolean sendAndroidPushEntity(List<AndroidPush> entities) {
        if (entities == null || entities.isEmpty()) {
            return false;
        }
        List<String> list = new ArrayList<String>();
        for (AndroidPush entity : entities) {
            list.add(JSON.toJSONString(entity));
        }
        Map<String, Object> result =
                JedisUtils.addTailEle(PushConstant.REDIS_CMD_LIST_NAME, list);
        return 1 == (int) result.get("result");
    }

    /**
     * 批量推送ios简单消息
     * @param messagePushChannels 设备对象
     * @param alertTitle 弹出消息标题
     * @param alertBody 弹出消息标题
     * @param badge 数目图标
     * @param sound 声音提示
     * @param params 指令数据
     */
    public static void pushIos(List<MessagePushChannel> messagePushChannels,
                               String alertTitle ,String alertBody,String badge ,String sound ,Map<String, String> params){
        if (CollectionUtils.isNotEmpty(messagePushChannels)){
            ArrayList<IosPush> iosPushes = new ArrayList<>();
            for (MessagePushChannel mc :
                    messagePushChannels) {
                if (mc.getClientType().equals(PushConstant.CLIENT_TYPE_IOS)){
                    IosPush iosPush = new IosPush(mc.getToken(), alertTitle,alertBody, badge, sound, params);
                    iosPushes.add(iosPush);
                }
            }
            sendIosKit(iosPushes);
        }
    }

    private static boolean sendIosKit(List<IosPush> list){

        ArrayList<String> messages = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)){
            for (IosPush msg :
                    list) {
                messages.add(JSON.toJSONString(msg));
            }
            Map<String, Object> result =
                    JedisUtils.addTailEle(PushConstant.REDIS_IOS_PUSHKIT_LIST_NAME, messages);
            return 1 == (int) result.get("result");
        }else {
            return false;
        }
    }

    /**
     * 批量推送到web端消息
     * @param messagePushChannels 设备对象集合
     * @param command 指令标识
     * @param params 额外数据
     */
    public static void pushWeb(List<MessagePushChannel> messagePushChannels, String command ,Map<String, String> params) {
        if (CollectionUtils.isNotEmpty(messagePushChannels)){
            ArrayList<WebPush> webPushes = new ArrayList<>();
            for (MessagePushChannel mc :
                    messagePushChannels) {
                if (mc.getClientType().equals(PushConstant.CLIENT_TYPE_WEB)){
                    WebPush webPush = new WebPush(mc.getToken(), command, params);
                    webPushes.add(webPush);
                }
            }
            sendWebEntity(webPushes);
        }
    }

    private static boolean sendWebEntity(List<WebPush> list){
        ArrayList<String> messages = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)){
            for (WebPush msg :
                    list) {
                messages.add(JSON.toJSONString(msg));
            }
            Map<String, Object> result =
                    JedisUtils.addTailEle(PushConstant.REDIS_RPC_CMD_LIST_NAME, messages);
            return 1 == (int) result.get("result");
        }else {
            return false;
        }
    }
}
