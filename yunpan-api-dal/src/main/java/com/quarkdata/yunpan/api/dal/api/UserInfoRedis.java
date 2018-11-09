package com.quarkdata.yunpan.api.dal.api;

import com.quarkdata.quark.share.model.dataobj.Incorporation;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.model.common.Constants;
import com.quarkdata.yunpan.api.util.JedisUtils;
import org.springframework.stereotype.Repository;


/**
 * 从 redis中获取当前用户相关 信息
 */
public class UserInfoRedis implements UserInfo {

    /**
     * 用户id
     */
    private int userId;

    /**
     * 企业id
     */
    private int incId;


    /**
     * 获取当前用户信息
     *
     * @return
     */
    @Override
    public UserInfoVO getUserInfoVO() {
        return (UserInfoVO)JedisUtils.getObject(Constants.ONESHARE_REDIS_PREFIX + Constants.ONESHARE_REDIS_DELIMITER + Constants.ONESHARE_REDIS_USER_INFO_VO + Constants.ONESHARE_REDIS_DELIMITER + userId);
    }

    /**
     * 获取当前用户所在企业
     *
     * @return
     */
    @Override
    public Incorporation getIncorporation() {
        return (Incorporation)JedisUtils.getObject(Constants.ONESHARE_REDIS_PREFIX + Constants.ONESHARE_REDIS_DELIMITER + Constants.ONESHARE_REDIS_INC + Constants.ONESHARE_REDIS_DELIMITER + incId);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getIncId() {
        return incId;
    }

    public void setIncId(int incId) {
        this.incId = incId;
    }
}
