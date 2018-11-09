package com.quarkdata.yunpan.api.service;

import com.quarkdata.yunpan.api.model.common.ResultCode;

/**
 * Created by yanyq1129@thundersoft.com on 2018/8/21.
 */
public interface AppService {
    ResultCode<String> getIOSCertificate();
}
