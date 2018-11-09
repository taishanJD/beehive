package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.aspect.checkDocPermission.CheckDocPermission;
import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.aspect.isDelete.IsDelete;
import com.quarkdata.yunpan.api.aspect.isDelete.IsDeleteType;
import com.quarkdata.yunpan.api.aspect.isShare.IsShare;
import com.quarkdata.yunpan.api.model.common.DocumentConstants;
import com.quarkdata.yunpan.api.model.common.DocumentPermissionConstants;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.dataobj.DocumentVersion;
import com.quarkdata.yunpan.api.service.DocumentPermissionService;
import com.quarkdata.yunpan.api.service.DocumentService;
import com.quarkdata.yunpan.api.service.DocumentVersionService;
import com.quarkdata.yunpan.api.service.DownloadService;
import com.quarkdata.yunpan.api.util.common.Exception.YCException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * @author maorl
 * @date 12/12/17 10:34 AM
 */
@Controller
@RequestMapping(RouteKey.PREFIX_API + RouteKey.DOWNLOAD)
public class DownloadController extends BaseController{


    @Autowired
    DownloadService downloadService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentVersionService documentVersionService;

    @Autowired
    private DocumentPermissionService documentPermissionService;

    @ResponseBody
    @RequestMapping(value = "/download_doc")
    @IsDelete(ids = "#{documentIdStr}")
    @IsShare(documentIds = "#{documentIdStr}")
    @CheckDocPermission(ids = "#{documentIdStr}", permission = DocumentPermissionConstants.PermissionIndex.DOWNLOAD)
    public ResultCode downloadByDocIds(HttpServletRequest request, HttpServletResponse response,
                                       @RequestParam("documentIds") String documentIdStr) {
        ResultCode<String> result = null;
        try {
            String[] ids = documentIdStr.split(",");
            List<String> documentIds = Arrays.asList(ids);
            UserInfoVO userVO = UserInfoUtil.getUserInfoVO();
            Integer incId = userVO.getUser().getIncid();
            String filePath = downloadService.getUrlsByDocId(documentIds,incId);
            Document document = this.documentService.getDocumentById(Long.parseLong(documentIds.get(0)));
            DocumentVersion documentVersion = null;
            if(document != null && !StringUtils.equals(document.getDocumentType(), DocumentConstants.DOCUMENT_DOCUMENT_TYPE_DIR)) {
                documentVersion = this.documentVersionService.getDocumentVersionByVersionId(document.getDocumentVersionId());
            }
            Long lastModified = document == null ? new Date().getTime() : document.getUpdateTime().getTime();
            String ETag = documentVersion == null ? "" : documentVersion.getMd5();
            if (filePath!=null) {
                this.downloadService.download(request, filePath, response, documentIds, ETag, lastModified.toString());
            }
        } catch (Exception e) {
            logger.error("download error");
            result = new ResultCode<>();
            result.setCode(Messages.API_ERROR_CODE);
            result.setMsg(Messages.API_ERROR_MSG);
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/download_version")
    @IsDelete(ids = "#{versionId}", className = IsDeleteType.DocumentVersion)
    public ResultCode downloadByVersions(HttpServletRequest request, HttpServletResponse response, @RequestParam String versionId) {
        ResultCode<String> result = null;
        DocumentVersion documentVersionByVersionId = this.documentVersionService.getDocumentVersionByVersionId(Long.parseLong(versionId));
        if(documentVersionByVersionId != null) {
            Boolean hasPermission = this.documentPermissionService.hasPermission(UserInfoUtil.getIncId(), UserInfoUtil.getUserId(), UserInfoUtil.getGroupIds(), UserInfoUtil.getDeptId(), documentVersionByVersionId.getDocumentId(), DocumentPermissionConstants.PermissionIndex.DOWNLOAD);
            if(!hasPermission) {
                throw new YCException(Messages.API_AUTHEXCEPTION_MSG, Messages.API_AUTHEXCEPTION_CODE);
            }
        } else {
            throw new YCException(Messages.FILE_VERSION_NOT_EXIST_MSG, Messages.FILE_VERISON_NOT_EXIST_CODE);
        }
        String filePath = downloadService.getUrlByVersion(versionId, UserInfoUtil.getIncorporation().getId());
        Document document = this.documentService.getDocumentByVersion(Long.parseLong(versionId));
        DocumentVersion documentVersion = this.documentVersionService.getDocumentVersionByVersionId(Long.parseLong(versionId));
        Long lastModified = document == null ? new Date().getTime() : document.getUpdateTime().getTime();
        String ETag = documentVersion == null ? "" : documentVersion.getMd5();
        List<String> documentIds = new ArrayList<>();
        documentIds.add(document.getId().toString());
        try {
            this.downloadService.download(request, filePath, response, documentIds, ETag, String.valueOf(lastModified));
        } catch (Exception e) {
            logger.error("download error, documentName is: {}", document.getDocumentName());
            result = new ResultCode<>();
            result.setCode(Messages.API_ERROR_CODE);
            result.setMsg(Messages.API_ERROR_MSG);
        }
        return result;
    }





}

