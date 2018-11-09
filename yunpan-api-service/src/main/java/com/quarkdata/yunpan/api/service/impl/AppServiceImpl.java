package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.yunpan.api.dal.rest.onespace.OneSpaceApi;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.service.AppService;
import com.quarkdata.yunpan.api.util.StringUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by yanyq1129@thundersoft.com on 2018/8/21.
 */
@Service
public class AppServiceImpl implements AppService {

    @Autowired
    private OneSpaceApi oneSpaceApi;

    @Override
    public ResultCode<String> getIOSCertificate() {
        ResultCode<String> resultCode = new ResultCode<>();
        Map<String, String> result = this.oneSpaceApi.getIOSCertificate();
        if(MapUtils.isNotEmpty(result) && StringUtils.isNotEmpty(result.get("result"))) {
            resultCode.setData(result.get("result"));
        } else {
            resultCode.setCode(Messages.GET_CER_FAIL_CODE);
            resultCode.setMsg(Messages.GET_CER_FAIL_MSG);
        }
        return resultCode;
    }
}
