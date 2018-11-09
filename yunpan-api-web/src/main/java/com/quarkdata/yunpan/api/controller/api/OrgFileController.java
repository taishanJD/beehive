package com.quarkdata.yunpan.api.controller.api;

import com.alibaba.fastjson.JSON;
import com.quarkdata.yunpan.api.aspect.checkDocPermission.CheckDocPermission;
import com.quarkdata.yunpan.api.aspect.isDelete.IsDelete;
import com.quarkdata.yunpan.api.aspect.isEnoughSpace.IsEnoughSpace;
import com.quarkdata.yunpan.api.aspect.isLock.IsLock;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.message.MessageAnnotation;
import com.quarkdata.yunpan.api.message.MessageType;
import com.quarkdata.yunpan.api.message.MessageUtils;
import com.quarkdata.yunpan.api.model.common.*;
import com.quarkdata.yunpan.api.model.vo.*;
import com.quarkdata.yunpan.api.service.DocumentPermissionService;
import com.quarkdata.yunpan.api.service.OrgFileService;
import com.quarkdata.yunpan.api.util.ResultUtil;
import com.quarkdata.yunpan.api.util.common.Exception.YCException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanyq1129@thundersoft.com on 2018/9/10.
 * 更新组织文件权限后的新版控制器
 */
@RequestMapping(RouteKey.PREFIX_API + "/organized/file")
@RestController
public class OrgFileController {

    private Logger logger = LoggerFactory.getLogger(OrgFileController.class);

    @Autowired
    private OrgFileService orgFileService;

    @Autowired
    private DocumentPermissionService documentPermissionService;


    /**
     * 查询组织文件列表
     *
     * @param documentName
     * @param parentId
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @IsDelete(ids = "#{parentId}")
    @ResponseBody
    public ResultCode list(String documentName,
                           @RequestParam(value = "parentId", required = false, defaultValue = "0") Long parentId,
                           @RequestParam(value = "pageNum", required = false) Integer pageNum,
                           @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        ResultCode<List<DocumentExtend>> resultCode = null;
        documentName = StringUtils.isBlank(documentName) ? null : documentName.trim();
        Long incId = UserInfoUtil.getIncId();
        Long userId = UserInfoUtil.getUserId();
        List<Long> groupIds = UserInfoUtil.getGroupIds();
        Long deptId = UserInfoUtil.getDeptId();
        boolean systemAdmin = UserInfoUtil.isSystemAdmin();
        resultCode = this.orgFileService.list(incId, userId, groupIds, deptId, systemAdmin, parentId, documentName, pageNum, pageSize);
        return resultCode;
    }

    @RequestMapping("/isOrganizedAdmin")
    public ResultCode<Boolean> isAdmin() {
        ResultCode<Boolean> resultCode = new ResultCode<Boolean>();
        boolean isAdmin = UserInfoUtil.isAdmin();
        resultCode.setData(isAdmin);
        return resultCode;
    }

    /**
     * 上传组织文件
     *
     * @param file
     * @param fileName
     * @param isOverwrite
     * @param parentId
     * @param request
     * @return
     */
    @RequestMapping(value = "/upload_file", method = RequestMethod.POST)
    @IsDelete(ids = "#{parentId}")
    @IsEnoughSpace(type = DocumentConstants.DOCUMENT_TYPE_ORGANIZED, docSize = "#{file.size}")
    @CheckDocPermission(ids = "#{parentId}", permission = DocumentPermissionConstants.PermissionIndex.UPLOAD)
    public ResultCode<DocumentExtend> uploadFile(HttpServletRequest request,
                                                 @RequestParam(value = "file", required = true) MultipartFile file, String fileName,
                                                 @RequestParam(value = "isOverwrite", required = false, defaultValue = "0") int isOverwrite,
                                                 @RequestParam(value = "parentId", required = true) Long parentId) {
        ResultCode<DocumentExtend> resultCode = null;
        try {
            // 获取登录态信息
            Long incId = UserInfoUtil.getIncId();
            Long userId = UserInfoUtil.getUserId();
            String userName = UserInfoUtil.getUserInfoVO().getUser().getUserName();
            List<Long> groupIds = UserInfoUtil.getGroupIds();
            Integer deptId = UserInfoUtil.getDeptId().intValue();
            boolean isAdmin = UserInfoUtil.isAdmin();

            logger.info("start upload file,params --> " + "originalFilename:{},fileName:{},"
                            + "isOverwrite:{},parentId:{},userId:{}," + "userName:{},incId:{},deptId:{},groupIds:{},isAdmin:{}",
                    file.getOriginalFilename(), fileName, isOverwrite, parentId, userId, userName, incId, deptId, groupIds, isAdmin);

            resultCode = orgFileService.uploadFile(request, file, fileName, isOverwrite, parentId, userId, userName, incId, deptId, groupIds, isAdmin);
            logger.info("after upload file,result --> " + JSON.toJSONString(resultCode));
        } catch (DuplicateKeyException e) {
            logger.info("处理上传重复组织文件");
            resultCode = new ResultCode<DocumentExtend>();
            resultCode.setCode(Messages.API_INSERT_ERROR_CODE);
            resultCode.setMsg(Messages.API_INSERT_ERROR_MSG);
        } catch (Exception e) {
            logger.error("upload organized file", e);
            resultCode = new ResultCode<DocumentExtend>();
            resultCode.setCode(Messages.API_ERROR_CODE);
            resultCode.setMsg(Messages.API_ERROR_MSG);
        }
        return resultCode;
    }

    /**
     * App上传组织文件
     *
     * @param file
     * @param fileName
     * @param isOverwrite
     * @param parentId
     * @param request
     * @return
     */
    @RequestMapping(value = "/upload_file_app", method = RequestMethod.POST)
    @IsDelete(ids = "#{parentId}")
    @IsEnoughSpace(type = DocumentConstants.DOCUMENT_TYPE_ORGANIZED, docSize = "#{file.size}")
    @CheckDocPermission(ids = "#{parentId}", permission = DocumentPermissionConstants.PermissionIndex.UPLOAD)
    public ResultCode<DocumentExtend> uploadFileApp(HttpServletRequest request,
                                                    @RequestParam(value = "file", required = true) MultipartFile file, String fileName,
                                                    @RequestParam(value = "isOverwrite", required = false, defaultValue = "0") int isOverwrite,
                                                    @RequestParam(value = "parentId", required = true) Long parentId) {
        ResultCode<DocumentExtend> resultCode = null;
        try {
            // 获取登录态信息
            Long incId = UserInfoUtil.getIncId();
            Long userId = UserInfoUtil.getUserId();
            String userName = UserInfoUtil.getUserInfoVO().getUser().getUserName();
            Integer deptId = UserInfoUtil.getDeptId().intValue();
            List<Long> groupIds = UserInfoUtil.getGroupIds();
            boolean isAdmin = UserInfoUtil.isAdmin();

            logger.info("start upload file,params --> " + "originalFilename:{},fileName:{}," + "isOverwrite:{},parentId:{},userId:{}," + "userName:{},incId:{},deptId:{},groupIds:{},isAdmin:{}",
                    file.getOriginalFilename(), fileName, isOverwrite, parentId, userId, userName, incId, deptId, groupIds, isAdmin);

            resultCode = orgFileService.uploadFileApp(request, file, fileName,
                    isOverwrite, parentId, userId, userName, incId, deptId, groupIds, isAdmin);
            logger.info("after upload file,result --> " + JSON.toJSONString(resultCode));
        } catch (DuplicateKeyException e) {
            logger.info("处理上传重复组织文件");
            resultCode = new ResultCode<DocumentExtend>();
            resultCode.setCode(Messages.API_INSERT_ERROR_CODE);
            resultCode.setMsg(Messages.API_INSERT_ERROR_MSG);
        } catch (Exception e) {
            logger.error("upload organized file", e);
            resultCode = new ResultCode<DocumentExtend>();
            resultCode.setCode(Messages.API_ERROR_CODE);
            resultCode.setMsg(Messages.API_ERROR_MSG);
        }
        return resultCode;
    }

    /**
     * 创建组织文件夹
     *
     * @param createOrganizedDirectoryVO
     * @param request
     * @return
     */
    @RequestMapping(value = "/create_dir", method = RequestMethod.POST)
    @IsDelete(ids = "#{createOrganizedDirectoryVO.parentId}")
    @MessageAnnotation
    public ResultCode<DocumentExtend> createDir(@RequestBody CreateOrganizedDirectoryVO createOrganizedDirectoryVO,
                                                HttpServletRequest request) {
        ResultCode<DocumentExtend> resultCode = null;
        if (createOrganizedDirectoryVO == null || StringUtils.isBlank(createOrganizedDirectoryVO.getDocumentName())) {
            throw new YCException(Messages.MISSING_INPUT_MSG, Messages.MISSING_INPUT_CODE);
        }
        // 获取登录态信息
        Long incId = UserInfoUtil.getIncId();
        Long userId = UserInfoUtil.getUserId();
        List<Long> groupIds = UserInfoUtil.getGroupIds();
        Long deptId = UserInfoUtil.getDeptId();
        String userName = UserInfoUtil.getUserInfoVO().getUser().getUserName();
        boolean isAdmin = UserInfoUtil.isAdmin();

        Long parentId = createOrganizedDirectoryVO.getParentId();

        try {
            resultCode = this.orgFileService.createOrganizedDir(request, parentId, createOrganizedDirectoryVO.getDocumentName().trim(), createOrganizedDirectoryVO.getPermissions(), createOrganizedDirectoryVO.getPermissionsOfGenerateAccounts(), userId, userName, incId, isAdmin, groupIds, deptId);
            // 发送消息通知
            if (CollectionUtils.isNotEmpty(createOrganizedDirectoryVO.getPermissions()) && Messages.SUCCESS_CODE == resultCode.getCode()) {
                List<Long> documentIds = new ArrayList<>();
                documentIds.add(resultCode.getData().getId());
                MessageUtils.createMessage(request, resultCode.getCode(),
                        DocumentConstants.DOCUMENT_ROOT_PARENTID.equals(parentId) ? MessageType.organized_space : MessageType.add_permission,
                        documentIds, createOrganizedDirectoryVO.getPermissions());
            }
        } catch (YunPanApiException e) {
            logger.error("new organized dir,errCode is {},errMsg is {}", e.getErrCode(), e.getErrMsg());
            resultCode = new ResultCode<>();
            resultCode.setCode(e.getErrCode());
            resultCode.setMsg(e.getErrMsg());
        }
        return resultCode;
    }

    /**
     * 设置文档权限
     *
     * @param documentPrivilege
     * @param request
     * @return
     */
    @MessageAnnotation
    @RequestMapping(value = "/set_document_privilege", method = RequestMethod.POST)
    @IsDelete(ids = "#{documentPrivilege.documentId}")
    @IsLock(ids = "#{documentPrivilege.documentId}")
    @CheckDocPermission(ids = "#{documentPrivilege.documentId}", permission = DocumentPermissionConstants.PermissionIndex.SET_PERMISSION)
    public ResultCode<String> setDocumentPrivilege(HttpServletRequest request, @RequestBody SetDocumentPrivilegeVO documentPrivilege) {
        // 获取登录态信息
        boolean isOrganizedAdmin = UserInfoUtil.isAdmin();
        Long incId = UserInfoUtil.getIncId();
        Long userId = UserInfoUtil.getUserId();
        List<Long> userGroupIds = UserInfoUtil.getGroupIds();
        Long deptId = UserInfoUtil.getDeptId();
        logger.info("start set document privilege,params --> {},incId:{},isOrganizedAdmin:{}",
                JSON.toJSONString(documentPrivilege), incId, isOrganizedAdmin);

        ResultCode<String> resultCode = null;
        try {
            resultCode = orgFileService.setDocumentPrivilege(request, documentPrivilege.getDocumentId(), documentPrivilege.getPermissions(),
                    documentPrivilege.getPermissionsOfGenerateAccounts(), incId, isOrganizedAdmin, userId, userGroupIds, deptId);
            logger.info("after set document privilege,result --> " + JSON.toJSONString(resultCode));
            //消息通知处理
            List<Long> documentIds = new ArrayList<>();
            documentIds.add(documentPrivilege.getDocumentId());
            MessageUtils.createMessage(request, resultCode.getCode(), MessageType.add_permission, documentIds, documentPrivilege.getPermissions());
        } catch (YunPanApiException e) {
            logger.error("set document privilege,errCode is {},errMsg is {}", e.getErrCode(), e.getErrMsg());
            resultCode = new ResultCode<String>();
            resultCode.setCode(e.getErrCode());
            resultCode.setMsg(e.getErrMsg());
        }
        return resultCode;
    }

    @RequestMapping("/getDocDocumentPermissionsToTheRoot")
    @IsDelete(ids = "#{docId}")
    public ResultTwoDataCode<List<DocumentPermissionVO>, List<Long>> getDocDocumentPermissionsToTheRoot(Long docId) {
        Long incId = UserInfoUtil.getIncId();
        ResultTwoDataCode<List<DocumentPermissionVO>, List<Long>> resultCode = orgFileService.getDocumentPermissions(docId, incId);
        return resultCode;
    }

    @RequestMapping(value = "/getDocumentPermissionOfMine", method = RequestMethod.GET)
    @IsDelete(ids = "#{docId}")
    public ResultCode<Integer> getDocumentPermissionOfMine(Long docId) {
        Long incId = UserInfoUtil.getIncId();
        Long userId = UserInfoUtil.getUserId();
        List<Long> userGroupIds = UserInfoUtil.getGroupIds();
        Long deptId = UserInfoUtil.getDeptId();
        return ResultUtil.success(this.documentPermissionService.getPermissionByDocumentId(docId, incId, userId, userGroupIds, deptId, UserInfoUtil.isSystemAdmin()));
    }

    /**
     * 根据parentId获取具有大于读写权限的组织文件子文件夹目录
     *
     * @param parentId
     * @return
     */
    @RequestMapping(value = "/dir")
    @ResponseBody
    @IsDelete(ids = "#{parentId}")
    public ResultCode<List<DocumentExtend>> getDirectoryThatHasUploadPermission(String documentName, @RequestParam(value = "parentId", required = false, defaultValue = "0") Long parentId) {
        Long incId = UserInfoUtil.getIncId();
        Long userId = UserInfoUtil.getUserId();
        List<Long> groupIds = UserInfoUtil.getGroupIds();
        Long deptId = UserInfoUtil.getDeptId();
        boolean isSystemAdmin = UserInfoUtil.isSystemAdmin();
        List<DocumentExtend> data = this.orgFileService.getDirectoryThatHasUploadPermission(incId, userId, groupIds, deptId, isSystemAdmin, parentId, documentName);
        return ResultUtil.success(data);
    }

    /**
     * 根据parentId获取个人文件数量
     *
     * @param parentId
     * @return
     */
    @RequestMapping(value = "/getCount")
    @ResponseBody
    public ResultCode<String> getCount(@RequestParam(value = "parentId", required = false, defaultValue = "0") Long parentId) {
        Long incId = UserInfoUtil.getIncId();
        Long userId = UserInfoUtil.getUserId();
        List<Long> userGroupIds = UserInfoUtil.getGroupIds();
        Long deptId = UserInfoUtil.getDeptId();
        ResultCode<String> resultCode = orgFileService.getFileCount(incId, userId, userGroupIds, deptId);
        return resultCode;
    }

    /**
     * 根据parentId获取文件
     *
     * @param parentId
     * @return
     */
    @RequestMapping(value = "/getAllChildren")
    @ResponseBody
    @IsDelete(ids = "#{parentId}")
    public ResultCode<List<DocumentExtend>> getAllChildren(@RequestParam(value = "parentId", required = false, defaultValue = "0") Long parentId) {
        Long incId = UserInfoUtil.getIncId();
        Long userId = UserInfoUtil.getUserId();
        List<Long> userGroupIds = UserInfoUtil.getGroupIds();
        Long deptId = UserInfoUtil.getDeptId();
        ResultCode<List<DocumentExtend>> resultCode = orgFileService.getAllChildren(incId, userId, userGroupIds, deptId, parentId);
        return resultCode;
    }

    /**
     * 组织文档删除接口
     *
     * @param docIds 文档id，多个用逗号相隔
     * @author jiadao
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    @IsDelete(ids = "#{docIds}")
    @IsLock(ids = "#{docIds}")
    public ResultCode<String> delDocBatch(String docIds) {
        ResultCode<String> result = new ResultCode<>();
        if (StringUtils.isBlank(docIds)) {
            throw new YCException(Messages.MISSING_INPUT_MSG, Messages.MISSING_INPUT_CODE);
        }
        String userName = UserInfoUtil.getUserInfoVO().getUser().getUserName();
        Long incId = UserInfoUtil.getIncorporation().getId().longValue();
        Long userId = UserInfoUtil.getUserInfoVO().getUser().getUserId().longValue();
        List<Long> userGroupIds = UserInfoUtil.getGroupIds();
        Long deptId = UserInfoUtil.getUserInfoVO().getDepartment().getId().longValue();
        boolean isOrganizedAdmin = UserInfoUtil.isAdmin();
        Boolean flag = this.orgFileService.delDocBatch(incId, userId, userName, userGroupIds, deptId, docIds, isOrganizedAdmin);
        result.setData(flag.toString());
        return result;
    }


    /**
     * 锁定组织文档(文件夹或者文件)
     *
     * @param request
     * @param documentIds 逗号分隔
     * @return
     */
    @RequestMapping(value = "/lock", method = RequestMethod.POST)
    @IsDelete(ids = "#{documentIds}")
    @CheckDocPermission(ids = "#{documentIds}", permission = DocumentPermissionConstants.PermissionIndex.LOCK)
    public ResultCode<String> lockOrgDocument(HttpServletRequest request, String documentIds) {
        if (StringUtils.isBlank(documentIds)) {
            throw new YCException(Messages.MISSING_INPUT_MSG, Messages.MISSING_INPUT_CODE);
        }
        ResultCode<String> resultCode = this.orgFileService.lockOrgDocument(request, documentIds);
        return resultCode;
    }

    /**
     * 解锁组织文档(文件夹或者文件)
     *
     * @param request
     * @param documentIds 逗号分隔
     * @return
     */
    @RequestMapping(value = "/unlock", method = RequestMethod.POST)
    @IsDelete(ids = "#{documentIds}")
    public ResultCode<String> unLockOrgDocument(HttpServletRequest request, String documentIds) {
        if (StringUtils.isBlank(documentIds)) {
            throw new YCException(Messages.MISSING_INPUT_MSG, Messages.MISSING_INPUT_CODE);
        }
        ResultCode<String> resultCode = this.orgFileService.unLockOrgDocument(request, documentIds);
        return resultCode;
    }
}
