package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.quark.share.model.dataobj.Incorporation;
import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.yunpan.api.dal.dao.MessagePushChannelMapper;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.PushConstant;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.MessagePushChannel;
import com.quarkdata.yunpan.api.model.dataobj.MessagePushChannelExample;
import com.quarkdata.yunpan.api.service.PushService;
import com.quarkdata.yunpan.api.util.MD5Encyption;
import com.quarkdata.yunpan.api.util.PushUtils;
import com.quarkdata.yunpan.api.util.ResultUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * author:liuda
 * Date:2018/9/12
 * Time:10:26
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PushServiceImpl implements PushService {

    @Autowired
    private MessagePushChannelMapper messagePushChannelMapper;

    @Override
    public String getDeviceUDID(String source, String sourcePolicy, String identity, String IMEI, String MEID, String SN, String MAC, Incorporation inc , Users users) {
        StringBuffer sourceSB = new StringBuffer(120);
        StringBuffer sourcePolicySB = new StringBuffer(220);
        /*
         * 修改生成规则 修改于 170807 其中@为固定分隔符用于分割每个属性值,将当前企业的code码 加入udid的生成规则中,防止设备在不同企业下登录异常的bug
         */
        if (StringUtils.isNotBlank(IMEI)) {
            sourceSB.append(IMEI);
            sourcePolicySB.append("IMEI=").append(IMEI);
        }
        sourceSB.append("@");
        sourcePolicySB.append("@");
        if (StringUtils.isNotBlank(MEID)) {
            sourceSB.append(MEID);
            sourcePolicySB.append("MEID=").append(MEID);
        }
        sourceSB.append("@");
        sourcePolicySB.append("@");
        if (StringUtils.isNotBlank(SN)) {
            sourceSB.append(SN);
            sourcePolicySB.append("SN=").append(SN);
        }
        sourceSB.append("@");
        sourcePolicySB.append("@");

        if (StringUtils.isNotBlank(MAC)) {
            MAC = MAC.replaceAll("m", ":");
            sourceSB.append(MAC);
            sourcePolicySB.append("MAC=").append(MAC);
        }
        sourceSB.append("@");
        sourcePolicySB.append("@");

        if (sourceSB.length() <= 4) {
            return null;
        }

        sourceSB.append(inc.getCode());
        sourcePolicySB.append("INCCODE=").append(inc.getCode());
        String newUDID = MD5Encyption.getMD5Encyption(sourceSB.toString());

        //查看该设备是否有用户绑定
        MessagePushChannelExample messagePushChannelExample = new MessagePushChannelExample();
        MessagePushChannelExample.Criteria criteria = messagePushChannelExample.createCriteria();
        criteria.andClientTypeEqualTo(PushConstant.CLIENT_TYPE_ANDROID);
        criteria.andTokenEqualTo(newUDID);
        List<MessagePushChannel> messagePushChannels = messagePushChannelMapper.selectByExample(messagePushChannelExample);

        if (CollectionUtils.isNotEmpty(messagePushChannels)){
            Boolean iscreate = true;
            for (MessagePushChannel mc :
                    messagePushChannels) {
                if (mc.getUserId().equals(users.getUserId().longValue()) && mc.getIncId().equals(users.getIncid().longValue())){
                    iscreate = false;
                }else {
                    messagePushChannelMapper.deleteByPrimaryKey(mc.getId());
                }
            }
            if (iscreate){
                createChannel(users.getUserId().longValue(),inc.getId().longValue(),newUDID,PushConstant.CLIENT_TYPE_ANDROID);
            }
        }else {
           createChannel(users.getUserId().longValue(),inc.getId().longValue(),newUDID,PushConstant.CLIENT_TYPE_ANDROID);
        }
//        //测试发送消息
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("title","beehive");
//        map.put("body","hello");
//        ArrayList<MessagePushChannel> list = new ArrayList<>();
//        list.add(messagePushChannelMapper.selectByPrimaryKey(Long.parseLong("3")));
//        PushUtils.pushAndroid(list,"0",map);
        return newUDID;
    }

    @Override
    public ResultCode<Object> setIosToken(String token,Users user) {
        //查看该设备是否有用户绑定
        MessagePushChannelExample messagePushChannelExample = new MessagePushChannelExample();
        MessagePushChannelExample.Criteria criteria = messagePushChannelExample.createCriteria();
        criteria.andClientTypeEqualTo(PushConstant.CLIENT_TYPE_IOS);
        criteria.andTokenEqualTo(token);
        List<MessagePushChannel> messagePushChannels = messagePushChannelMapper.selectByExample(messagePushChannelExample);
        if (CollectionUtils.isNotEmpty(messagePushChannels)){
            Boolean iscreate = true;
            for (MessagePushChannel mc :
                    messagePushChannels) {
                if (mc.getUserId().equals(user.getUserId().longValue()) && mc.getIncId().equals(user.getIncid().longValue())){
                    iscreate = false;
                }else {
                    messagePushChannelMapper.deleteByPrimaryKey(mc.getId());
                }
            }
            if (iscreate){
                createChannel(user.getUserId().longValue(),user.getIncid().longValue(),token,PushConstant.CLIENT_TYPE_IOS);
            }
        }else {
            createChannel(user.getUserId().longValue(),user.getIncid().longValue(),token,PushConstant.CLIENT_TYPE_IOS);
        }
        //测试推送
//        HashMap<String, String> map = new HashMap<>();
//        map.put("hello","hello beehive");
//        ArrayList<MessagePushChannel> list = new ArrayList<>();
//        list.add(messagePushChannelMapper.selectByPrimaryKey(Long.parseLong("6")));
//        PushUtils.pushIos(list,"hello beehive","1",null,map);
        return ResultUtil.success();
    }

    @Override
    public String getWebUdid(Users users,String udid) {
//        //web端先查看当前用户是否有空余的udid标识，没有或者都在线拿sessionid生成一个新的通道
//        MessagePushChannelExample messagePushChannelExample = new MessagePushChannelExample();
//        MessagePushChannelExample.Criteria criteria = messagePushChannelExample.createCriteria();
//        criteria.andClientTypeEqualTo(PushConstant.CLIENT_TYPE_WEB);
//        criteria.andIsOnlineEqualTo("0");
//        criteria.andUserIdEqualTo(users.getUserId().longValue());
//        List<MessagePushChannel> messagePushChannels = messagePushChannelMapper.selectByExample(messagePushChannelExample);
//        if (CollectionUtils.isNotEmpty(messagePushChannels)){
//            Boolean iscreate = true;
//            for (MessagePushChannel mc :
//                    messagePushChannels) {
//                if (mc.getUserId().equals(users.getUserId().longValue()) && mc.getIncId().equals(users.getIncid().longValue())){
//                    iscreate = false;
//                }else {
//                    messagePushChannelMapper.deleteByPrimaryKey(mc.getId());
//                }
//            }
//            if (iscreate){
//                createChannel(users.getUserId().longValue(),users.getIncid().longValue(),udid,PushConstant.CLIENT_TYPE_WEB);
//            }
//        }else {
        //直接生成新的通道，掉线以后删除
        createChannel(users.getUserId().longValue(),users.getIncid().longValue(),udid,PushConstant.CLIENT_TYPE_WEB);
        //}
        //测试web推送
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("title","云盘推送");
//        map.put("body","你有一条新的消息");
//        ArrayList<MessagePushChannel> list = new ArrayList<>();
//        list.add(messagePushChannelMapper.selectByPrimaryKey(Long.parseLong("7")));
//        PushUtils.pushWeb(list,"0",map);
        //测试Android推送
//                HashMap<String, Object> map = new HashMap<>();
//        map.put("title","beehive");
//        map.put("body","hello");
//        ArrayList<MessagePushChannel> list = new ArrayList<>();
//        list.add(messagePushChannelMapper.selectByPrimaryKey(Long.parseLong("4")));
//        PushUtils.pushAndroid(list,PushConstant.BEEHIVE_PUSH_CMD,map);
        return udid;
    }

    @Override
    public ResultCode pushMessage(List<Long> userIds, String command, String title, String body, String badge ,String sound) {
        try {
            if (CollectionUtils.isNotEmpty(userIds)){
                //寻找指定通道标识    指定的用户的在线设备
                MessagePushChannelExample messagePushChannelExample = new MessagePushChannelExample();
                MessagePushChannelExample.Criteria criteria = messagePushChannelExample.createCriteria();
                criteria.andUserIdIn(userIds);
                criteria.andIsOnlineEqualTo(PushConstant.DEV_ONLINE_STATUS_ON);
                List<MessagePushChannel> messagePushChannels = messagePushChannelMapper.selectByExample(messagePushChannelExample);
                if (CollectionUtils.isNotEmpty(messagePushChannels)){
                    ArrayList<MessagePushChannel> android = new ArrayList<>();
                    ArrayList<MessagePushChannel> ios = new ArrayList<>();
                    ArrayList<MessagePushChannel> web = new ArrayList<>();
                    for (MessagePushChannel mc : messagePushChannels) {
                        switch (mc.getClientType()){
                            case PushConstant.CLIENT_TYPE_ANDROID:
                                android.add(mc);
                                break;
                            case PushConstant.CLIENT_TYPE_IOS:
                                ios.add(mc);
                                break;
                            case PushConstant.CLIENT_TYPE_WEB:
                                web.add(mc);
                                break;
                        }
                    }
                    //ios指令在额外数据中   其它端title和body在额外数据中
                    HashMap<String, String> iosMap = new HashMap<>();
                    iosMap.put("cmd",command);

                    HashMap<String, String> clientMap = new HashMap<>();
                    clientMap.put("title",title);
                    clientMap.put("body",body);

                    //推送到各端
                    PushUtils.pushAndroid(android,command,clientMap);
                    PushUtils.pushIos(ios,title,body,badge,sound,iosMap);
                    PushUtils.pushWeb(web,command,clientMap);
                }
            }
            return ResultUtil.success();
        }catch (Exception e){
            return ResultUtil.error(Messages.BEELIVE_PUSH_MESSAGE_ERROR_CODE,Messages.BEELIVE_PUSH_MESSAGE_ERROR_MSG);
        }
    }

    @Override
    public void updateDevOnlineStatus(String udid, Boolean isOnline, Long timestamp) {
        MessagePushChannelExample messagePushChannelExample = new MessagePushChannelExample();
        MessagePushChannelExample.Criteria criteria = messagePushChannelExample.createCriteria();
        criteria.andTokenEqualTo(udid);
        List<MessagePushChannel> messagePushChannels = messagePushChannelMapper.selectByExample(messagePushChannelExample);
        if (CollectionUtils.isNotEmpty(messagePushChannels)){
            for (MessagePushChannel mc :
                    messagePushChannels) {
                //修改在线状态
                if (isOnline){
                    mc.setIsOnline(PushConstant.DEV_ONLINE_STATUS_ON);
                    messagePushChannelMapper.updateByPrimaryKey(mc);
                }else {
                    if (PushConstant.CLIENT_TYPE_ANDROID.equals(mc.getClientType())){
                        //Android端修改设备状态
                        mc.setIsOnline(PushConstant.DEV_ONLINE_STATUS_OUT);
                        messagePushChannelMapper.updateByPrimaryKey(mc);
                    }else if (PushConstant.CLIENT_TYPE_WEB.equals(mc.getClientType())){
                        //WEB端直接删除
                        messagePushChannelMapper.deleteByPrimaryKey(mc.getId());
                    }
                }
            }
        }
    }

    public void createChannel(Long userId,Long incId,String udid,String clientType){
        MessagePushChannel messagePushChannel = new MessagePushChannel();
        messagePushChannel.setClientType(clientType);
        messagePushChannel.setCreateTime(new Date());
        messagePushChannel.setToken(udid);
        messagePushChannel.setIncId(incId);
        messagePushChannel.setUserId(userId);
        //ios通过apns推送，不需要判断是否在线
        if (PushConstant.CLIENT_TYPE_IOS.equals(clientType)){
            messagePushChannel.setIsOnline("1");
        }
        messagePushChannelMapper.insertSelective(messagePushChannel);
    }
}
