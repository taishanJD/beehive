package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by yanyq1129@thundersoft.com on 2018/8/21.
 */
@RestController
public class AppController {

    @Autowired
    private AppService appService;

    /**
     * 获取iOS安全证书
     * @return
     */
    @RequestMapping("/app/ios/cer")
    public ResultCode<String> getIOSCertificate() {
        return this.appService.getIOSCertificate();
    }
}
