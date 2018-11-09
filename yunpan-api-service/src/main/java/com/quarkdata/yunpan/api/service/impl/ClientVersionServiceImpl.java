package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.yunpan.api.dal.rest.onespace.OneSpaceApi;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.service.ClientVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by yanyq1129@thundersoft.com on 2018/8/6.
 */
@Service
@Transactional(readOnly = true)
public class ClientVersionServiceImpl implements ClientVersionService {

    @Autowired
    private OneSpaceApi oneSpaceApi;

    @Override
    public ResultCode<List<Map<String, String>>> getClientVersionList(Integer incId, String platform) {
        return this.oneSpaceApi.getClientVersionList(incId, platform);
    }
}
