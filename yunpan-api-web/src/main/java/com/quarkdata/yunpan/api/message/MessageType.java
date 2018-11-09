package com.quarkdata.yunpan.api.message;

/**
 * 消息通知类型
 */
public class MessageType {

    /**
     * 归档
     */
    public static final String Archival = "共享归档文件";
    /**
     * 共享
     */
    public static final String Share = "共享文件";
    /**
     * 共享
     */
    public static final String delete_Share = "取消文件共享";
    /**
     * 收藏成功
     */
    public static final String Collection_success = "收藏成功";
    /**
     * 取消收藏
     */
    public static final String Collection_cancel = "取消收藏";
    /**
     * 授予权限
     */
    public static final String add_permission = "";
    /**
     * 共享组织空间
     */
    public static final String organized_space = "共享组织空间";
    /**
     *收回权限
     */
    public static final String delete_permission = "收回权限";

    /**
     * 消息通知状态信息修改
     */
    public static final String UPDATE_TYPE = "修改了消息通知状态信息";

    /**
     * 消息类型（1权限提醒，2分享提醒，3收藏提醒,4邀请提醒）
     */
    public static final String REMIND_PERMISSION = "1";
    public static final String REMIND_SHARE = "2";
    public static final String REMIND_COLLECTION = "3";
    public static final String REMIND_INVITATION = "4";



}
