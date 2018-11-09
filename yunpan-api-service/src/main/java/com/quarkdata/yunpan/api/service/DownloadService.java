package com.quarkdata.yunpan.api.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author maorl
 * @date 12/12/17.
 */
public interface DownloadService extends BaseLogService {

    String getUrlsByDocId(List<String> documentIds,Integer incId);

    String getUrlByVersion(String versionIds, Integer incId);

     List<String> getDocNameById(List<String> ids);

     Long getIncIdByCode(String code);

    /**
     * 调用下载工具类 DownloadUtils 将 api 服务器上的文档下载到客户端
     * @param request
     * @param filePath
     * @param response
     * @param documentIds
     */
    void download(HttpServletRequest request, String filePath, HttpServletResponse response, List<String> documentIds, String ETag, String lastModified);

    String downloadFile(long documentVersionId);
}
