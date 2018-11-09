package com.quarkdata.yunpan.api.dal.api;

import com.quarkdata.quark.share.model.dataobj.Incorporation;
import com.quarkdata.quark.share.model.vo.UserInfoVO;


/**
 * 用户信息 bean，作用域：request
 * @author wujianbo
 */
public interface UserInfo {

    /**
     * 获取当前用户信息
     * @return
     */
    UserInfoVO getUserInfoVO();

    /**
     * 获取当前用户所在企业
     * @return
     */
    Incorporation getIncorporation();

}
