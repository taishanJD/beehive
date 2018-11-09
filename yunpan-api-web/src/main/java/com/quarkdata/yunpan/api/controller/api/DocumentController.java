package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.quark.share.model.dataobj.Department;
import com.quarkdata.quark.share.model.dataobj.Group;
import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.yunpan.api.aspect.checkDocPermission.CheckDocPermission;
import com.quarkdata.yunpan.api.aspect.isLock.IsLock;
import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.aspect.isDelete.IsDelete;
import com.quarkdata.yunpan.api.model.common.ApiMessages;
import com.quarkdata.yunpan.api.model.common.DocumentPermissionConstants;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.vo.DocumentRecentBrowseVO;
import com.quarkdata.yunpan.api.service.DocumentService;
import com.quarkdata.yunpan.api.service.impl.CommonFileServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by xiexl on 2018/1/11.
 */
@Controller
@RequestMapping(RouteKey.PREFIX_API +"/"+ RouteKey.Document)
public class DocumentController extends BaseController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private CommonFileServiceImpl commonFileService;

    /**
     * 文档重命名
     * @param id 文档id
     * @param documentName 文档名称
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/rename")
    @IsDelete(ids = "#{id}")
    @IsLock(ids = "#{id}")
    @CheckDocPermission(ids = "#{id}", permission = DocumentPermissionConstants.PermissionIndex.RENAME)
    public ResultCode rename(@RequestParam("id") Long id, @RequestParam("documentName") String documentName, HttpServletRequest req) {
        ResultCode<String> result = new ResultCode<String>();
        if(null == id) {
            result.setCode(Messages.MISSING_INPUT_CODE);
            result.setMsg(Messages.MISSING_INPUT_MSG);
            return result;
        }
        if(StringUtils.isBlank(documentName)) {
            result.setCode(Messages.RENAME_MISSING_DOCUMENT_NAME_CODE);
            result.setMsg(Messages.RENAME_MISSING_DOCUMENT_NAME_MSG);
            return result;
        }
        try {
            Integer userId = UserInfoUtil.getUserInfoVO().getUser().getUserId();
            List<Long> userGroupIds = this.getGroupIds(UserInfoUtil.getUserInfoVO().getGroupsList());
            Integer deptId = UserInfoUtil.getUserInfoVO().getDepartment().getId();
            result = documentService.rename(req, id,documentName.trim(), Long.parseLong(userId.toString()), userGroupIds, Long.parseLong(deptId.toString()));
        } catch (Exception e) {
            logger.error("<<<<<< rename error <<<<<<", e);
            result.setCode(Messages.API_ERROR_CODE);
            result.setMsg(Messages.API_ERROR_MSG);
        }
        return  result;
    }


    /**
     * 文档或目录移动
     * @param sourceDocumentIds 源文档或目录ID  逗号分隔串（1,2,3）
     * @param targetDocumentId  目标目录ID
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/move")
    @IsLock(ids = "#{sourceDocumentIds}")
    public ResultCode move(@RequestParam("sourceDocumentIds") String sourceDocumentIds,
                           @RequestParam("targetDocumentId") Long targetDocumentId,
                           @RequestParam("targetDocumentType") String targetDocumentType,
                           HttpServletRequest req) {
        ResultCode<String> result = new ResultCode<String>();
        if(StringUtils.isEmpty(sourceDocumentIds) || null == targetDocumentId) {
            result.setCode(Messages.MISSING_INPUT_CODE);
            result.setMsg(Messages.MISSING_INPUT_MSG);
            return result;
        }
        try {
            result = documentService.move(req, sourceDocumentIds,targetDocumentId,targetDocumentType);
        } catch (Exception e) {
            logger.error("<<<<<< move document error <<<<<<", e);
            result.setCode(Messages.API_ERROR_CODE);
            result.setMsg(Messages.API_ERROR_MSG);
        }
        return  result;
    }


    /**
     * 复制文档或目录
     * @param sourceDocumentIds 文档或目录ID 逗号分隔串（1,2,3）
     * @param targetDocumentId 目标目录ID
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/copy")
    @IsLock(ids = "#{sourceDocumentIds}")
    public ResultCode copy(@RequestParam("sourceDocumentIds") String sourceDocumentIds,
                           @RequestParam("targetDocumentId") Long targetDocumentId,
                           @RequestParam("targetDocumentType") String targetDocumentType,
                           HttpServletRequest req) {
        ResultCode<String> result = new ResultCode<String>();
        if(StringUtils.isEmpty(sourceDocumentIds) || null == targetDocumentId) {
            result.setCode(Messages.MISSING_INPUT_CODE);
            result.setMsg(Messages.MISSING_INPUT_MSG);
            return result;
        }
        try {
            result = documentService.copy(req, sourceDocumentIds,targetDocumentId,targetDocumentType);
        } catch (Exception e) {
            logger.error("<<<<<< copy document error <<<<<<", e);
            result.setCode(Messages.API_ERROR_CODE);
            result.setMsg(Messages.API_ERROR_MSG);
        }
        return  result;
    }

    /**
     * 根据文档id批量修改文件类型(组织文件or个人文件)
     * @param docIds
     * @param type
     * @return
     */
    @RequestMapping(value = "/type")
    @ResponseBody
    public ResultCode<String> updateDocumentTypeByIdBatch(String docIds, String type) {
        ResultCode<String> result = new ResultCode<String>();
        Integer incId = UserInfoUtil.getIncorporation().getId();
        Users user = UserInfoUtil.getUserInfoVO().getUser();
        try {
            if (StringUtils.isNotBlank((docIds))) {
                String[] documentIds = docIds.split(",");
                this.documentService.updateDocumentTypeByIdBatch(incId, user, documentIds, type);
                result.setCode(Messages.SUCCESS_CODE);
                result.setMsg(Messages.SUCCESS_MSG);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Messages.UPDATE_DOCUMENT_TYPE_FAIL_CODE);
            result.setMsg(Messages.UPDATE_DOCUMENT_TYPE_FAIL_MSG);
        }
        return result;
    }

    /**
     * 新建[文件夹]时检查是否已有同名[文件夹]
     * @param parentId
     * @param directoryName
     * @return
     */
    @RequestMapping(value = "/check")
    @ResponseBody
    public ResultCode<String> checkDirectoryName(String parentId, String directoryName, String type) {
        ResultCode<String> result = new ResultCode<String>();
        Integer incId = UserInfoUtil.getIncorporation().getId();
        Users user = UserInfoUtil.getUserInfoVO().getUser();
        try {
            Boolean flag = this.documentService.checkDirectoryName(incId, user, parentId, directoryName, type);
            if(flag) {
                // 已有同名文件夹
                result.setCode(ApiMessages.CREATE_DIR_ALREADY_EXISTED_CODE);
                result.setMsg(ApiMessages.CREATE_DIR_ALREADY_EXISTED_MSG);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(1);
            result.setMsg("服务器繁忙");
        }
        return result;
    }


    /**
     * 获取最近浏览历史文件
     * @param request
     * @return
     */
    @RequestMapping(value = "recent-browse")
    @ResponseBody
    public ResultCode<List<DocumentRecentBrowseVO>> getRecentBrowseDocuments(HttpServletRequest request, Integer pageNum, Integer pageSize) {
        ResultCode<List<DocumentRecentBrowseVO>> result = new ResultCode<List<DocumentRecentBrowseVO>>();
        Integer userId = UserInfoUtil.getUserInfoVO().getUser().getUserId();
        Integer incId = UserInfoUtil.getIncorporation().getId();
        List<Long> userGroupIds = getGroupIds(UserInfoUtil.getUserInfoVO().getGroupsList());
        Long deptId = UserInfoUtil.getUserInfoVO().getDepartment().getId().longValue();
        result = this.documentService.getRecentBrowseDocuments(incId, userId, userGroupIds, deptId, pageNum, pageSize);
        return result;
    }

    /**
     * 移除最近浏览记录
     * @param request
     * @return
     */
    @RequestMapping(value = "remove_recent_browse", method = RequestMethod.POST)
    @ResponseBody
    public ResultCode<Object> removeRecentBrowseRecords(HttpServletRequest request, String logIds) {
        ResultCode<Object> resultCode = new ResultCode<>();
        if(StringUtils.isNotBlank(logIds)) {
            try {
                Integer incId = UserInfoUtil.getIncorporation().getId();
                Integer userId = UserInfoUtil.getUserInfoVO().getUser().getUserId();
                this.documentService.removeRecentBrowseRecords(incId, userId, logIds);
            } catch (Exception e) {
                this.logger.error("<<<<<< remove recent browse fail", e);
                resultCode.setCode(Messages.API_ERROR_CODE);
                resultCode.setMsg(Messages.API_ERROR_MSG);
                return resultCode;
            }
        } else {
            resultCode.setCode(Messages.MISSING_INPUT_CODE);
            resultCode.setMsg(Messages.MISSING_INPUT_MSG);
            return resultCode;
        }
        return resultCode;
    }

    @RequestMapping(value = "/exist")
    @ResponseBody
    public ResultCode<String> checkUploadingFileIsExist(@RequestParam(defaultValue = "0") Long parentId, String type, String documentName) {
        ResultCode<String> result  =new ResultCode<>();
        if(StringUtils.isBlank(documentName) || StringUtils.isBlank(type)) {
            result.setCode(Messages.MISSING_INPUT_CODE);
            result.setMsg(Messages.MISSING_INPUT_MSG);
            return result;
        }
        result.setData("true");
        return this.commonFileService.isExistDoc(Long.parseLong(UserInfoUtil.getIncorporation().getId().toString()),
                Long.parseLong(UserInfoUtil.getUserInfoVO().getUser().getUserId().toString()), parentId, documentName.trim(), type, result);
    }

    private List<Long> getGroupIds(List<Group> groupsList) {
        List<Long> groupIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(groupsList)) {
            for (Group g : groupsList) {
                if(g !=null){
                    groupIds.add(g.getId().longValue());
                }
            }
        }
        return groupIds;
    }

    /**
     * 预览前检查文件是否存在
     * @param docVersionId
     * @return
     */
    @RequestMapping(value = "/preview_exist")
    @ResponseBody
    public ResultCode<Boolean> checkExist(String docVersionId) {
        ResultCode<Boolean> result = new ResultCode<>();
        if(StringUtils.isBlank(docVersionId)) {
            result.setCode(Messages.MISSING_INPUT_CODE);
            result.setMsg(Messages.MISSING_INPUT_MSG);
            result.setData(false);
            return result;
        }
        try {
            Boolean resultData = this.documentService.checkExist(docVersionId);
            result.setData(resultData);
        } catch (Exception e) {
            result.setCode(Messages.API_ERROR_CODE);
            result.setMsg(Messages.API_ERROR_MSG);
            result.setData(false);
            logger.error("check file exist error", e);
        }
        return result;
    }

    /**
     * 根据id查询文档信息
     * @param docId
     * @return
     */
    @RequestMapping(value = "/doc")
    @ResponseBody
    public ResultCode<Document> getDocumentByIdWithPermission(String docId) {
        ResultCode<Document> result = null;
        if(StringUtils.isBlank(docId)) {
            result = new ResultCode<>();
            result.setCode(Messages.MISSING_INPUT_CODE);
            result.setMsg(Messages.MISSING_INPUT_MSG);
            return result;
        }
        try {
            Integer incId = UserInfoUtil.getIncorporation().getId();
            Integer userId = UserInfoUtil.getUserInfoVO().getUser().getUserId();
            List<Group> groupsList = UserInfoUtil.getUserInfoVO().getGroupsList();
            List<Long> groupIds = this.getGroupIds(groupsList);
            Department department = UserInfoUtil.getUserInfoVO().getDepartment();
            result = this.documentService.getDocumentByIdWithPermission(incId, userId, groupIds, department == null ? null : department.getId().longValue(), Long.parseLong(docId));
        } catch (Exception e) {
            result = new ResultCode<>();
            result.setCode(Messages.API_ERROR_CODE);
            result.setMsg(Messages.API_ERROR_MSG);
            logger.error("<<<<<< select doc error <<<<<<", e);
        }
        return result;
    }

    /**
     * 根据idPath查询namePath
     * @param idPath
     * @return
     */
    @RequestMapping(value = "/namePath")
    @ResponseBody
    public ResultCode<Map<String, String>> getNamePathByIdPath(String idPath) {
        ResultCode<Map<String, String>> result = new ResultCode<>();
        if(StringUtils.isBlank(idPath)) {
            result = new ResultCode<>();
            result.setCode(Messages.MISSING_INPUT_CODE);
            result.setMsg(Messages.MISSING_INPUT_MSG);
            return result;
        }
        try {
            result = this.documentService.getNamePathByIdPath(UserInfoUtil.getIncorporation().getId(), idPath);
        } catch (Exception e) {
            result = new ResultCode<>();
            result.setCode(Messages.API_ERROR_CODE);
            result.setMsg(Messages.API_ERROR_MSG);
            logger.error("<<<<<< get namePath error <<<<<<", e);
        }
        return result;
    }

    /**
     * 获取个人文件,组织文件,归档文件最近一次操作时间
     * @return
     */
    @RequestMapping("/time")
    @ResponseBody
    public ResultCode<Map<String, Date>> getLastUpdateTime() {
        ResultCode<Map<String, Date>> result = null;
        Integer incId = UserInfoUtil.getIncorporation().getId();
        Integer userId = UserInfoUtil.getUserInfoVO().getUser().getUserId();
        try {
            result = this.documentService.getLastUpdateTime(incId, userId);
        } catch (Exception e) {
            result = new ResultCode<>();
            result.setCode(Messages.API_ERROR_CODE);
            result.setMsg(Messages.API_ERROR_MSG);
        }
        return result;
    }

}

