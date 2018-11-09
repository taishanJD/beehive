package com.quarkdata.yunpan.api.model.common;

/**
 * author:liuda
 * Date:2018/9/11
 * Time:18:17
 */
public class PushConstant {
    /**
     * redis中远程控制指令队列名称
     */
    public static final String REDIS_RPC_CMD_LIST_NAME = "beehive_rpc_cmd_list";

    /**
     * redis中的指令队列名称
     */
    public static final String REDIS_CMD_LIST_NAME = "beehive_cmd_list";

    /**
     * mongo中的指令备份集合名称
     */
    public static final String MONGO_CMD_COLLECTION_NAME = "beehive_cmd_list";

    /**
     * redis中的指令队列名称
     */
    public static final String REDIS_IOS_CMD_LIST_NAME = "beehive_ios_cmd_list";

    /**
     * redis中的消息队列名称
     */
    public static final String REDIS_IOS_MESSAGE_LIST_NAME = "beehive_ios_msg_list";
    /**
     * redis中pushkit推送队列名称
     */
    public static final String REDIS_IOS_PUSHKIT_LIST_NAME = "beehive_ios_pushkit_list";

    /**
     * mongo中的指令备份集合名称
     */
    public static final String MONGO_IOS_CMD_COLLECTION_NAME = "beehive_ios_cmd_list";

    /**
     * mongo中的消息备份集合名称
     */
    public static final String MONGO_IOS_MESSAGE_COLLECTION_NAME = "beehive_ios_msg_list";

    /**
     * redis中设备在线离线状态变更list
     */
    public static final String REDIS_DEVICE_ONLINE_STATUS_CHANGE_LIST =
            "beehive_device.online.status.change.list";

    public static final String CLIENT_TYPE_IOS = "IOS";

    public static final String CLIENT_TYPE_ANDROID = "ANDROID";

    public static final String CLIENT_TYPE_WEB = "WEB";

    public static final String CLIENT_TYPE_MAC = "MAC";

    /**
     * 设备离线
     */
    public static final String DEV_ONLINE_STATUS_OUT = "0";

    /**
     * 设备在线
     */
    public static final String DEV_ONLINE_STATUS_ON = "1";

    /**
     * 推动指令---新消息
     */
    public static final String BEEHIVE_PUSH_CMD = "m";
}
