package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.TokenApi;
import com.quarkdata.yunpan.api.model.common.DocumentConstants;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.dataobj.DocumentVersion;
import com.quarkdata.yunpan.api.model.dataobj.ExternalLink;
import com.quarkdata.yunpan.api.model.vo.DocumentIdLinkVO;
import com.quarkdata.yunpan.api.service.*;
import com.quarkdata.yunpan.api.util.ResultUtil;
import com.quarkdata.yunpan.api.util.common.mapper.JsonMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author maorl
 * @date 12/19/17.
 */

@Controller
public class AccessLinkController extends BaseController {
    @Autowired
    TokenApi tokenApi;
    @Autowired
    AccessLinkService linkService;
    @Autowired
    DownloadService downloadService;

    @Autowired
    DocumentService documentService;

    @Autowired
    private DocumentVersionService documentVersionService;

    @Autowired
    LogService logService;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @ResponseBody
    @RequestMapping(value = "/access_link")
    public ResultCode accessLink(HttpServletRequest request, String linkCode, String authorization, String fetchCode) {
        return linkService.accessLink(request, linkCode, authorization, fetchCode);
    }

    @ResponseBody
    @RequestMapping(value = "/get_owner")
    public ResultCode getOwner(HttpServletRequest request) {
        String linkCode = request.getParameter("linkCode");
        return linkService.getOwner(linkCode);
    }

    @ResponseBody
    @RequestMapping(value = "/download_link")
    public ResultCode downloadByDocIds(HttpServletRequest request, HttpServletResponse response) {
        String[] ids = request.getParameter("documentIds").split(",");
        List<String> documentIds = Arrays.asList(ids);
        String linkCode = request.getParameter("linkCode");
        Long incId = downloadService.getIncIdByCode(linkCode);
        if (linkService.isLegal(documentIds, linkCode)) {
            // 校验是否允许下载
            ExternalLink linkByLinkCode = this.linkService.getLinkByLinkCode(linkCode);
            if(DocumentConstants.Link.ALLOW_DOWNLOAD_NO.equals(linkByLinkCode.getAllowDownload())) {
                return ResultUtil.error(Messages.Link.NOT_ALLOW_DOWNLOAD_CODE, Messages.Link.NOT_ALLOW_DOWNLOAD_NO_MSG);
            }

            String filePath = downloadService.getUrlsByDocId(documentIds,incId.intValue());
            if (filePath==null) {
                return null;
            }
            this.addDownloadCountByLinkCode(linkCode);

            Document document = this.documentService.getDocumentById(Long.parseLong(documentIds.get(0)));
            DocumentVersion documentVersion = null;
            if(document != null && !StringUtils.equals(document.getDocumentType(), DocumentConstants.DOCUMENT_DOCUMENT_TYPE_DIR)) {
                documentVersion = this.documentVersionService.getDocumentVersionByVersionId(document.getDocumentVersionId());
            }
            Long lastModified = document == null ? new Date().getTime() : document.getUpdateTime().getTime();
            String ETag = documentVersion == null ? "" : documentVersion.getMd5();
            this.downloadService.download(request, filePath, response, documentIds, ETag, lastModified.toString());

            return null;
        }
        return ResultUtil.error(Messages.API_LINK_LEGAL_CODE, Messages.API_LINK_LEGAL_MSG);
    }


    /**
     * 获取外链文件夹中的文件
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/get_link_subfile")
    public ResultCode getSubFileLink(HttpServletRequest request, String linkCode, String parentId) {
        ResultCode<List<DocumentIdLinkVO>> result = new ResultCode<>();
        if(linkCode == null || parentId == null) {
            result.setCode(Messages.MISSING_INPUT_CODE);
            result.setMsg(Messages.MISSING_INPUT_MSG);
            return result;
        }
        result = linkService.getSubFileLink(Long.valueOf(parentId), linkCode);
        return result;
    }

    /**
     * 预览前检查文件是否存在
     * @param docVersionId
     * @return
     */
    @RequestMapping(value = "/preview_exist")
    @ResponseBody
    public ResultCode<Boolean> checkExist(String documentIds, String linkCode, String docVersionId) {
        ResultCode<Boolean> result = new ResultCode<>();
        if(StringUtils.isBlank(docVersionId) || StringUtils.isBlank(linkCode) || StringUtils.isBlank(documentIds)) {
            result.setCode(Messages.MISSING_INPUT_CODE);
            result.setMsg(Messages.MISSING_INPUT_MSG);
            return result;
        }
        try {
            String[] split = documentIds.split(",");
            if(this.linkService.isLegal(Arrays.asList(split), linkCode)) {
                Boolean resultData = this.documentService.checkExist(docVersionId);
                result.setData(resultData);
            } else {
                result.setCode(Messages.API_AUTHEXCEPTION_CODE);
                result.setMsg(Messages.API_AUTHEXCEPTION_MSG);
            }
        } catch (Exception e) {
            result.setCode(Messages.API_ERROR_CODE);
            result.setMsg(Messages.API_ERROR_MSG);
            logger.error("check file exist error", e);
        }
        return result;
    }

    @RequestMapping(value = "/preview_link/**")
    public void downloadlink(HttpServletRequest request, HttpServletResponse response,
                             String documentIds, String linkCode, String docVersionId) throws IOException {
        String[] ids = null;
        if (StringUtils.isNotBlank(documentIds)) {
            ids = documentIds.split(",");
        }
        else {
            String docIds = request.getHeader("documentIds");
            if(StringUtils.isNotBlank(docIds)) {
                ids = request.getHeader("documentIds").split(",");
            }
        }
        List<String> documentIds_list = new ArrayList<>();
        if(ids != null) {
            documentIds_list = Arrays.asList(ids);
        }

        if (StringUtils.isBlank(linkCode)) {
            linkCode = request.getHeader("linkCode");
        }

        if(StringUtils.isBlank(docVersionId)) {
            docVersionId = request.getHeader("docVersionId");
        }

        Long incId = downloadService.getIncIdByCode(linkCode);
        if (linkService.isLegal(documentIds_list, linkCode)) {
            // 校验是否允许预览
            ExternalLink linkByLinkCode = this.linkService.getLinkByLinkCode(linkCode);
            if(DocumentConstants.Link.ALLOW_PREVIEW_NO.equals(linkByLinkCode.getAllowPreview())) {
                ResultCode error = ResultUtil.error(Messages.Link.NOT_ALLOW_PREVIEW_CODE, Messages.Link.NOT_ALLOW_PREVIEW_MSG);
                response.reset();
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.setStatus(HttpStatus.SC_OK);
                response.getWriter().print(JsonMapper.toJsonString(error));
            }

            String filePath = downloadService.getUrlsByDocId(documentIds_list,incId.intValue());
            this.addViewCountByLinkCode(linkCode);
            this.documentService.previewFile(request, filePath, response, documentIds_list);
        }
    }

    /**
     * 预览外链文档不需要下载时记录预览日志(外链给所有人)
     * @param request
     * @param documentIds
     * @param linkCode
     * @param docVersionId
     */
    @RequestMapping(value = "/preview-log")
    @ResponseBody
    public ResultCode<String> addPreviewLog(HttpServletRequest request, String documentIds, String linkCode, String docVersionId) {
        ResultCode<String> result = new ResultCode<>();
        String[] ids = null;
        if (StringUtils.isNotBlank(documentIds)) {
            ids = documentIds.split(",");
        }
        else {
            String docIds = request.getHeader("documentIds");
            if(StringUtils.isNotBlank(docIds)) {
                ids = request.getHeader("documentIds").split(",");
            }
        }
        List<String> documentIds_list = new ArrayList<>();
        if(ids != null) {
            documentIds_list = Arrays.asList(ids);
        }

        if (StringUtils.isBlank(linkCode)) {
            linkCode = request.getHeader("linkCode");
        }

        if(StringUtils.isBlank(docVersionId)) {
            docVersionId = request.getHeader("docVersionId");
        }

        if (linkService.isLegal(documentIds_list, linkCode)) {
            this.addViewCountByLinkCode(linkCode);
//            this.logService.addPreviewLogByDocumentId(request, documentIds_list);
        }
        return result;
    }

    /**
     * 增加外链文件预览次数
     * @param linkCode
     */
    private void addViewCountByLinkCode(String linkCode) {
        ExternalLink link = this.linkService.getLinkByLinkCode(linkCode);
        if(link != null) {
            link.setViewCount(link.getViewCount() + 1);
            this.linkService.updateByPrimaryKey(link);
        }
    }

    /**
     * 增加外链文件下载次数
     * @param linkCode
     */
    private void addDownloadCountByLinkCode(String linkCode) {
        ExternalLink link = this.linkService.getLinkByLinkCode(linkCode);
        if(link != null) {
            link.setDownloadCount(link.getDownloadCount() + 1);
            this.linkService.updateByPrimaryKey(link);
        }
    }

}