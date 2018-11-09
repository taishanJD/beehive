package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.service.DocumentService;
import com.quarkdata.yunpan.api.service.DownloadService;
import com.quarkdata.yunpan.api.service.LogService;
import com.quarkdata.yunpan.api.util.DownloadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 文档在线预览
 * Created by yanyq1129@thundersoft.com on 2018/2/6.
 */
@Controller
@RequestMapping(RouteKey.PREFIX_API)
public class FilePreviewController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DownloadService downloadService;

    @Autowired
    private LogService logService;


    /**
     * 下载预览文件
     * @param req
     * @param resp
     */
    @RequestMapping(value = "/preview/**")
    public void previewFile(HttpServletRequest req, HttpServletResponse resp) {
        String pathInfo = req.getRequestURI();
        String docVersionId = pathInfo.substring(pathInfo.lastIndexOf("-") + 1, pathInfo.lastIndexOf("."));
        String filePath = this.documentService.getFilePathByVersionId(Long.valueOf(docVersionId));
        List<String> documentIds = new ArrayList<>();
        Document document = this.documentService.getDocumentByVersion(Long.valueOf(docVersionId));
        documentIds.add(document.getId().toString());
        this.documentService.previewFile(req, filePath, resp, documentIds);
    }

    /**
     * 预览文档不需要下载时记录预览日志
     * @param urlAddress
     */
    @RequestMapping(value = "/preview-log")
    @ResponseBody
    public ResultCode<String> addPreviewLog(HttpServletRequest request, String urlAddress) {
        ResultCode result = new ResultCode();
        String documentVersionId = urlAddress.substring(urlAddress.lastIndexOf("-") + 1, urlAddress.lastIndexOf("."));
        this.logService.addPreviewLogByDocumentVersionId(request, documentVersionId);
        return result;
    }
}



