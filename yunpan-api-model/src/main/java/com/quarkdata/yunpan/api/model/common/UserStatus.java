package com.quarkdata.yunpan.api.model.common;

/**
 * Created by yanyq1129@thundersoft.com on 2018/5/15.
 */
public class UserStatus {
    public static final Integer STATUSERNOTACTIVE = 1; // 未激活
    public static final Integer STATUSACTIVE = 2; // 账户激活
    public static final Integer STATUSLOCK = 3; // 账户锁定，管理员锁定
    public static final Integer STATUSDISABLED = 4; // 账户停用
    public static final Integer STATUSLOCKPSWERROR = 5; // 账户锁定，密码输错超过设置的阈值，导致账户锁定
    public static final Integer PASSWORDOVERTIME = 6; // 用户密码过期

    /**
     * 用户状态--未激活
     */
    public static final Integer USER_STATUS_UNACTIVATED = 1;

    /**
     * 用户状态--激活
     */
    public static final Integer USER_STATUS_ACTIVATED = 2;

    /**
     * 用户状态--被管理员锁定
     */
    public static final Integer USER_STATUS_LOCKED_ADMIN = 3;

    /**
     * 用户状态--被锁定--密码输入次数过多
     */
    public static final Integer USER_STATUS_LOCKED_USER = 5;

    /**
     * 用户状态--停用
     */
    public static final Integer USER_STATUS_BLOCKED = 4;
}
