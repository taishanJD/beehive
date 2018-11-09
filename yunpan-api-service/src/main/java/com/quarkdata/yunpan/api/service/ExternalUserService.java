package com.quarkdata.yunpan.api.service;

/**
 * Created by yanyq1129@thundersoft.com on 2018/6/4.
 */
public interface ExternalUserService {
    /**
     * 定时删除过期系统账号相关数据
     */
    Long timingDelete();
}
