package com.quarkdata.yunpan.api.dal.rest.onespace;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.util.HttpTookit;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yanyq1129@thundersoft.com on 2018/8/6.
 * 从one-space获取app客户端版本信息
 */
@Repository
public class OneSpaceApi {

    private static Logger logger = Logger.getLogger(OneSpaceApi.class);

    @Value("${server.domain}")
    private String domain;

    /**
     * 获取客户端版本信息
     * @param incId
     * @param platform
     * @return
     */
    public ResultCode<List<Map<String, String>>> getClientVersionList(Integer incId, String platform) {
        String url = OneSpaceApiConstants.oneSpaceApiBasePath + OneSpaceApiConstants.client_version_list;
        Map<String, String> params = new HashMap<>();
        params.put("incId", String.valueOf(incId));
        params.put("platform",platform);
        String doGet = HttpTookit.doGet(url, params);
        logger.info("request url : " + url + " ,params : " + params + " ,result : " + doGet);
        ResultCode<List<Map<String, String>>> result = JSON.parseObject(doGet,
                new TypeReference<ResultCode<List<Map<String, String>>>>() {
                });
        return result;
    }

    /**
     * 获取客户端升级包下载地址
     * @param appId
     * @param domain
     * @param platType
     */
    public String getClientDownloadPath(String appId, String domain, String platType) {
        String url = OneSpaceApiConstants.oneSpaceApiBasePath + OneSpaceApiConstants.client_download_path;
        Map<String, String> params = new HashMap<>();
        params.put("appId", appId);
        params.put("domain", this.domain);
        params.put("platType", platType);
        String doPost = HttpTookit.doPost(url, params);
        logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPost);
        ResultCode<Map<String, String>> result = JSON.parseObject(doPost,
                new TypeReference<ResultCode<Map<String, String>>>() {
                });
        if(result != null && MapUtils.isNotEmpty(result.getData())) {
            if(platType.equals(OneSpaceApiConstants.ClientFlag.CLIENT_ANDROID)) {
                return result.getData().get("versionPath");
            }
            if(platType.equals(OneSpaceApiConstants.ClientFlag.CLIENT_IOS)) {
                return result.getData().get("downloadUrl");
            }
        }
        return "";
    }

    /**
     * 获取默认包下载地址
     * @param platform
     * @return
     */
    public String getDefaultClientDownloadPath(Integer incId, String platform) {
        String url = OneSpaceApiConstants.oneSpaceApiBasePath + OneSpaceApiConstants.client_download_path_default;
        Map<String, String> params = new HashMap<>();
        params.put("companyID", incId == null ? "" : incId.toString());
        params.put("platform", platform.toLowerCase());
        String doGet = HttpTookit.doGet(url, params);
        logger.info("request url : " + url + " ,params : " + params + " ,result : " + doGet);
        ResultCode<Map<String, String>> result = JSON.parseObject(doGet,
                new TypeReference<ResultCode<Map<String, String>>>() {
                });
        return result.getData().get("downUrl");
    }

    /**
     * 获取ios客户端安全证书
     * @return
     */
    public Map<String,String> getIOSCertificate() {
        String url = OneSpaceApiConstants.oneSpaceApiBasePath + OneSpaceApiConstants.get_ios_certificate;
        String doGet = HttpTookit.doGet(url, null);
        logger.info("request url : " + url + " , result : " + doGet);
        Map<String, String> result = JSON.parseObject(doGet,
                new TypeReference<Map<String, String>>() {
                });
        return result;
    }
}
