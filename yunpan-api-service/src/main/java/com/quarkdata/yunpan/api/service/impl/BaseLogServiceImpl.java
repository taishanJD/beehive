package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.quark.share.model.dataobj.Incorporation;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.dal.dao.LogDocumentRelMapper;
import com.quarkdata.yunpan.api.dal.dao.LogMapper;
import com.quarkdata.yunpan.api.model.dataobj.Log;
import com.quarkdata.yunpan.api.model.dataobj.LogDocumentRel;
import com.quarkdata.yunpan.api.service.BaseLogService;
import com.quarkdata.yunpan.api.util.GetIPUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yanyq1129@thundersoft.com on 2018/4/18.
 */
public class BaseLogServiceImpl implements BaseLogService {

    private LogMapper logMapper;

    private LogDocumentRelMapper logDocumentRelMapper;

    public void setLogMapper(LogMapper logMapper) {
        this.logMapper = logMapper;
    }

    public void setLogDocumentRelMapper(LogDocumentRelMapper logDocumentRelMapper) {
        this.logDocumentRelMapper = logDocumentRelMapper;
    }

    @Override
    public void addDocumentLog(HttpServletRequest req, String documentId, Integer actionType, String detail, Date createTime) {
        Map<String, Object> map = this.getUserAndIncInfo();
        Long incId = Long.valueOf(map.get("incId").toString());
        Long userId = Long.valueOf(map.get("userId").toString());
        String userName = map.get("userName").toString();

        Log log = new Log();
        log.setCreateUserId(userId);
        log.setCreateUsername(userName);
        log.setIncId(incId);
        log.setActionType(actionType.toString());
        log.setActionDetail(detail);
        log.setCreateTime(createTime);
        log.setUserIp(GetIPUtils.getRequestIp(req));
        this.logMapper.insert(log);

        LogDocumentRel logDocumentRel = new LogDocumentRel();
        logDocumentRel.setIncId(incId);
        logDocumentRel.setCreateUserId(userId);
        logDocumentRel.setLogId(log.getId());
        logDocumentRel.setDocumentId(Long.parseLong(documentId));
        logDocumentRel.setCreateTime(createTime);
        this.logDocumentRelMapper.insert(logDocumentRel);
    }

    @Override
    public void addOtherLog(HttpServletRequest req, Integer actionType, String detail, Date createTime) {
        Map<String, Object> map = this.getUserAndIncInfo();
        Long incId = Long.valueOf(map.get("incId").toString());
        Long userId = Long.valueOf(map.get("userId").toString());
        String userName = map.get("userName").toString();

        Log log = new Log();
        log.setCreateUserId(userId);
        log.setCreateUsername(userName);
        log.setIncId(incId);
        log.setActionType(actionType.toString());
        log.setActionDetail(detail);
        log.setCreateTime(createTime);
        log.setUserIp(GetIPUtils.getRequestIp(req));
        this.logMapper.insert(log);

    }


    private Map<String, Object> getUserAndIncInfo() {
        Map<String, Object> map = new HashMap<String, Object>();
        // 从登录态中获取用户信息
        UserInfoVO userInfoVO = UserInfoUtil.getUserInfoVO();
        Long userId = -1L;
        String userName = "";
        if (userInfoVO != null){
            userId = Long.parseLong(userInfoVO.getUser().getUserId().toString());
            userName = userInfoVO.getUser().getUserName();
        }
        // 获取当前用户所在企业信息
        Incorporation incorporation = UserInfoUtil.getIncorporation();
        Long incId = -1L;
        if (incorporation != null){
            incId = Long.parseLong(incorporation.getId().toString());
        }
        map.put("incId", incId);
        map.put("userId", userId);
        map.put("userName", userName);

        return map;
    }
}
