package com.quarkdata.yunpan.api.util;

import com.quarkdata.quark.share.model.dataobj.Users;
import org.apache.commons.lang3.StringUtils;

public class UserUtils {
    public static final Integer STATUSERNOTACTIVE = 1; // 未激活
    public static final Integer STATUSACTIVE = 2; // 账户激活
    public static final Integer STATUSLOCK = 3; // 账户锁定，管理员锁定
    public static final Integer STATUSDISABLED = 4; // 账户停用
    public static final Integer STATUSLOCKPSWERROR = 5; // 账户锁定，密码输错超过设置的阈值，导致账户锁定
    public static final Integer PASSWORDOVERTIME = 6; // 用户密码过期

    /**
     * 根据优先级获取用户最终在前端页面显示的信息: displayName ==> userName ==> email ==> mobile
     *
     * @param user
     * @return
     */
    public static String getFinalDisplayNameFromUser(Users user) {
        String displayName = user.getDisplayName();
        String userName = user.getUserName();
        String email = user.getEmail();
        String mobile = user.getMobile();
        if (StringUtils.isNotBlank(displayName) && StringUtils.isNotBlank(userName)) {
            return displayName + "(" + userName + ")";
        } else if (StringUtils.isBlank(displayName) && StringUtils.isNotBlank(userName)) {
            return userName;
        } else if (StringUtils.isNotBlank(email)) {
            return email;
        } else if (StringUtils.isNotBlank(mobile)) {
            return mobile;
        } else {
            return "";
        }
    }

    /**
     * 根据优先级获取用户最终为云盘使用的username: displayName ==> userName ==> email ==> mobile
     *
     * @param user
     * @return
     */
    public static String getFinalUserNameFromUser(Users user) {
        String displayName = user.getDisplayName();
        String userName = user.getUserName();
        String email = user.getEmail();
        String mobile = user.getMobile();
        if (StringUtils.isNotBlank(userName)) {
            return userName;
        } else if (StringUtils.isNotBlank(displayName)) {
            return displayName;
        } else if (StringUtils.isNotBlank(email)) {
            return email;
        } else if (StringUtils.isNotBlank(mobile)) {
            return mobile;
        } else {
            return "";
        }
    }
}
