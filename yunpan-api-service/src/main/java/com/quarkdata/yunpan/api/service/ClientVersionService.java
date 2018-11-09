package com.quarkdata.yunpan.api.service;

import com.quarkdata.yunpan.api.model.common.ResultCode;

import java.util.List;
import java.util.Map;

/**
 * Created by yanyq1129@thundersoft.com on 2018/8/6.
 */
public interface ClientVersionService {

    /**
     * 获取app客户端版本信息
     * @param incId
     * @param platform
     * @return
     */
    ResultCode<List<Map<String, String>>> getClientVersionList(Integer incId, String platform);
}
