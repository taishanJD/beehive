package com.quarkdata.yunpan.api.service;

import com.quarkdata.quark.share.model.dataobj.Incorporation;
import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.yunpan.api.model.common.ResultCode;

import java.util.List;

/**
 * author:liuda
 * Date:2018/9/12
 * Time:10:23
 */
public interface PushService {

    /**
     * 获取设备唯一标识
     * @param source
     * @param sourcePolicy
     * @param identity
     * @param IMEI
     * @param MEID
     * @param SN
     * @param MAC
     * @param inc
     * @return
     */
    public String getDeviceUDID(String source, String sourcePolicy, String identity, String IMEI, String MEID,
                                String SN, String MAC, Incorporation inc ,Users user);

    /**
     * ios上传设备token
     * @param token
     * @return
     */
    public ResultCode<Object> setIosToken(String token,Users user);

    /**
     * web获取udid
     * @return
     */
    public String getWebUdid(Users users,String udid);

    /**
     * 推送消息
     * @param userIds 用户id集合
     * @param command 指令标识
     * @param title 消息标题
     * @param body 消息内容
     * @param badge ios图标
     * @param sound ios声音
     * @return
     */
    public ResultCode pushMessage(List<Long> userIds, String command, String title, String body, String badge ,String sound);

    /**
     * 修改web端和Android端设备状态
     * @param udid
     * @param isOnline
     * @param timestamp
     */
    public void updateDevOnlineStatus(String udid, Boolean isOnline, Long timestamp);
}
