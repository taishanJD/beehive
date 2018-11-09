package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.common.Constants;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.service.ClientDownloadPathService;
import com.quarkdata.yunpan.api.util.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by yanyq1129@thundersoft.com on 2018/7/25.
 * 查询客户端下载路径
 */
@RestController
public class ClientDownloadPathController {

    protected org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ClientDownloadPathService clientDownloadPathService;

    @Value("${oneSpaceApiBasePath}")
    private String oneSpaceApiBasePath;

    /**
     * 获取App升级路径
     * @param domain
     * @param platform android,ios,windows,mac ','分割
     * @return
     */
    @RequestMapping("/api/client/path")
    public ResultCode<Map<String, String>> getClientDownloadPathByPlatform(String id, String domain, String platform) {
        ResultCode<Map<String, String>> result = new ResultCode<>();
        if(StringUtils.isBlank(platform)) {
            result.setCode(Messages.MISSING_INPUT_CODE);
            result.setMsg(Messages.MISSING_INPUT_MSG);
            return result;
        }
        Map<String, String> pathMap = this.clientDownloadPathService.getClientDownloadPathByPlatform(id, domain, platform);
        result.setData(pathMap);
        return result;
    }


    /**
     * 获取扫码下载页面地址
     * @param request
     * @return
     */
    @RequestMapping("/api/client/sweep_download")
    public ResultCode<String> sweepDownload(HttpServletRequest request) {
        ResultCode<String> result = new ResultCode<>();
        Integer incId = UserInfoUtil.getIncorporation().getId();
        String serverPath = "http://" + request.getServerName() + ":" + request.getServerPort();
        result.setData(serverPath + "/client.html?server=" + serverPath + "&incId=" + incId);
        return result;
    }

    /**
     * 扫码下载页面获取默认包下载地址
     * @param platform
     * @return
     */
    @RequestMapping("/client/get_default_client")
    public void getDefaultClient(String platform, Integer incId, HttpServletResponse response) {
        String path = this.clientDownloadPathService.getDefaultClientDownloadPathByPlatform(incId, platform);
        if(StringUtils.isNotBlank(platform) && platform.toLowerCase().equals(Constants.CLIENT_ANDROID)) {
            try {
                response.sendRedirect(path);
            } catch (IOException e) {
                logger.error("安卓客户端扫码下载失败", e);
            }
        } else {
            try {
                String newPath = new String(path.getBytes("UTF-8"), "ISO8859-1");
                response.setContentType("text/html; charset=utf-8");
                response.getOutputStream().print(path);
            } catch (IOException e) {
                logger.error("iOS客户端扫码下载失败", e);
            }
        }
    }
}
