package com.quarkdata.yunpan.api.service;

import java.util.Map;

/**
 * Created by yanyq1129@thundersoft.com on 2018/7/25.
 */
public interface ClientDownloadPathService {

    /**
     * 获取升级包下载地址
     * @param id
     * @param domain
     * @param platform
     * @return
     */
    Map<String,String> getClientDownloadPathByPlatform(String id, String domain, String platform);

    /**
     * 获取默认包下载地址
     * @param platform
     * @return
     */
    String getDefaultClientDownloadPathByPlatform(Integer incId, String platform);
}
