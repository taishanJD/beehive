package com.quarkdata.yunpan.api.dal.api;


import com.quarkdata.quark.share.model.dataobj.Incorporation;
import com.quarkdata.quark.share.model.vo.UserInfoVO;

/**
 * 用户信息 bean，作用域：request
 * @author wujianbo
 */
public class UserInfoDefault implements UserInfo{

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
        return null;
    }

    /**
     * 获取当前用户所在企业
     *
     * @return
     */
    @Override
    public Incorporation getIncorporation() {
        return null;
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
