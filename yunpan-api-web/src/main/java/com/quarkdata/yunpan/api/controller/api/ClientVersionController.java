package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.service.ClientVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by yanyq1129@thundersoft.com on 2018/8/6.
 * 获取app客户端版本信息
 */
@RestController
@RequestMapping("/api/client/version")
public class ClientVersionController {

    @Autowired
    private ClientVersionService clientVersionService;

    /**
     * @param platform 1-Android 2-iOS
     * @return
     */
    @RequestMapping("/list")
    public ResultCode<List<Map<String, String>>> getClientVersionList(@RequestParam(required = true, defaultValue = "0") String platform) {
        Integer incId = UserInfoUtil.getUserInfoVO().getUser().getIncid();
        return this.clientVersionService.getClientVersionList(incId, platform);
    }
}
