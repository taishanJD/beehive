package com.quarkdata.yunpan.api.controller.api;

import com.alibaba.fastjson.JSON;
import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.yunpan.api.aspect.checkDocPermission.CheckDocPermission;
import com.quarkdata.yunpan.api.aspect.isDelete.IsDelete;
import com.quarkdata.yunpan.api.aspect.isLock.IsLock;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.message.MessageAnnotation;
import com.quarkdata.yunpan.api.message.MessageType;
import com.quarkdata.yunpan.api.message.MessageUtils;
import com.quarkdata.yunpan.api.model.common.DocumentPermissionConstants;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.request.AddArchivalFileVo;
import com.quarkdata.yunpan.api.model.request.Receiver;
import com.quarkdata.yunpan.api.model.vo.*;
import com.quarkdata.yunpan.api.service.ArchivalFileService;
import com.quarkdata.yunpan.api.service.ShareService;
import com.quarkdata.yunpan.api.task.DocumentTypeList;
import com.quarkdata.yunpan.api.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/**
 * 归档
 */
@RestController
@RequestMapping(RouteKey.PREFIX_API + RouteKey.ARCHIVAL)
public class ArchivalFileController {
    static Logger LOGGER = LoggerFactory.getLogger(ArchivalFileController.class);


    @Autowired
    private DocumentTypeList documentTypeList;

    @Autowired
    private ArchivalFileService archivalFileService;

    @Autowired
    ShareService shareService;

    /**
     * 查询归档列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/list")
    public ResultCode<List<DocumentExtend>> getArchivalFile(HttpServletRequest request) {
        Long parentId = Long.valueOf(request.getParameter("parentId"));
        String documentType = request.getParameter("documentType");
        String documentName = request.getParameter("documentName");
        String editDay = request.getParameter("editDay");
        Long incId = UserInfoUtil.getIncId();
        Long userId = UserInfoUtil.getUserId();
        List<Long> userGroupIds = UserInfoUtil.getGroupIds();
        Long deptId = UserInfoUtil.getDeptId();

        ResultCode<List<DocumentExtend>> resultCode =
                archivalFileService.getArchivalFile(incId, userId, parentId, documentType, documentName, editDay, userGroupIds, deptId, UserInfoUtil.getUserInfoVO().getRole().getId());
        return resultCode;
    }

    /**
     * 获取预览列表
     *
     * @param addArchivalFileVo
     * @return
     */
    @RequestMapping(value = "/prolist")
    public ResultCode getProList(@RequestBody AddArchivalFileVo addArchivalFileVo) {
        ResultCode<ReturnList> rc = new ResultCode();
        String archiveType = addArchivalFileVo.getArchiveType();
        List<Long> docIdList = addArchivalFileVo.getDocIdList();
        if ("0".equals(archiveType)) {
            String lastUpdateTime = addArchivalFileVo.getLastUpdateTime();
            List<String> documentType = addArchivalFileVo.getDocumentType();
            //TODO 前端修改完全后可删去
            if (documentType != null && documentType.size() > 0) {
                if (documentType.contains("all")) {
                    documentType = null;
                }
            }
            List<Long> tagIdList = addArchivalFileVo.getTagIdList();
            rc = archivalFileService.autoCreateArchivalFile(docIdList, lastUpdateTime, documentType, tagIdList);
        } else {
            rc = archivalFileService.manualProList(docIdList);
        }
        return rc;
    }

    /**
     * 新建归档
     *
     * @param addArchivalFileVo
     * @return
     */
    @RequestMapping(value = "/create")
    @MessageAnnotation
    @IsDelete(ids = "#{addArchivalFileVo.docIdList}")
    @IsLock(ids = "#{addArchivalFileVo.docIdList}")
    @CheckDocPermission(ids = "#{addArchivalFileVo.docIdList}", permission = DocumentPermissionConstants.PermissionIndex.ARCHIVE)
    public ResultCode createArchivalFile(@RequestBody AddArchivalFileVo addArchivalFileVo, HttpServletRequest request) {
        Users user = UserInfoUtil.getUserInfoVO().getUser();
        String docName = addArchivalFileVo.getDocName();
        String docDes = addArchivalFileVo.getDocDes();
        List<Receiver> receiverList = addArchivalFileVo.getReceiverList();
        List<Long> docIdList = addArchivalFileVo.getDocIdList();
        String isKeepSource = addArchivalFileVo.getIsKeepSource();
        ResultCode resultCode = archivalFileService.manualCreateArchivalFile(request, receiverList, docName, docDes, user, docIdList, isKeepSource);

        // 处理消息
        Document document = (Document) resultCode.getData();
        ArrayList<Long> documentIds = new ArrayList<>();
        documentIds.add(document.getId());
        if (receiverList != null && receiverList.size() > 0) {
            for (Receiver r : receiverList) {
                switch (r.getRecType()) {
                    case "user":
                        r.setRecType("0");
                        break;
                    case "group":
                        r.setRecType("1");
                        break;
                    case "dept":
                        r.setRecType("2");
                        break;
                }
            }
        }
        MessageUtils.addMessage(request, resultCode.getCode().toString(), MessageType.Archival, documentIds, receiverList);
        return resultCode;
    }

    /**
     * 验证同组织下的同名归档文件
     *
     * @param archiveName 需要验证的文件名
     * @return
     */
    @RequestMapping(value = "/checkArchiveName")
    public ResultCode checkArchiveName(@RequestParam("archiveName") String archiveName) {
        Long incId = UserInfoUtil.getIncId();
        ResultCode resultCode = archivalFileService.checkArchiveName(incId, archiveName);
        return resultCode;
    }

    @RequestMapping(value = "/documentType")
    public ResultCode getDocmentType() {
        ResultCode resultCode = documentTypeList.getResultCode();
        return resultCode;
    }

    /**
     * 归档文件编辑
     *
     * @param
     * @param req
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @IsDelete(ids = "#{document.id}")
    @CheckDocPermission(ids = "#{document.id}", permission = DocumentPermissionConstants.PermissionIndex.RENAME)
    public ResultCode<String> editAchiveFile(@RequestBody Document document, HttpServletRequest req) {
        //获取登录态
        List<Long> userGroupIds = UserInfoUtil.getGroupIds();
        Long deptId = UserInfoUtil.getDeptId();
        if (document != null) {
            archivalFileService.updateArchiveFile(document.getId(), document.getDocumentName(), document.getDescription(), req, userGroupIds, deptId);
        }
        return ResultUtil.success();
    }

    /**
     * 设置文档权限消息通知处理
     *
     * @param request
     * @param resultCode
     * @param documentPrivilege
     */
    private void setMessage(HttpServletRequest request, ResultCode<String> resultCode, SetDocumentPrivilegeVO documentPrivilege) {
        ArrayList<Long> longs = new ArrayList<>();
        longs.add(documentPrivilege.getDocumentId());
        ArrayList<Receiver> receivers = new ArrayList<>();
        Receiver receiver = new Receiver();
        if (documentPrivilege.getPermissions() != null && documentPrivilege.getPermissions().size() > 0) {
            for (DocumentPrivilegeVO vo : documentPrivilege.getPermissions()) {
                receiver.setRecId(vo.getReceiverId().toString());
                receiver.setRecType(vo.getReceiverType());
                receiver.setPermission(vo.getPermission());
                receivers.add(receiver);
            }
            MessageUtils.addMessage(request, resultCode.getCode().toString(), MessageType.add_permission, longs, receivers);
        }
    }

    /**
     * 设置归档文件权限
     *
     * @param request
     * @param setDocumentPrivilegeVO
     * @return
     */
    @RequestMapping(value = "/set_permission", method = RequestMethod.POST)
    @MessageAnnotation
    @IsDelete(ids = "#{setDocumentPrivilegeVO.documentId}")
    @CheckDocPermission(ids = "#{setDocumentPrivilegeVO.documentId}", permission = DocumentPermissionConstants.PermissionIndex.SET_PERMISSION)
    public ResultCode<String> setPermissionOfArchivalFile(HttpServletRequest request, @RequestBody SetDocumentPrivilegeVO setDocumentPrivilegeVO) {
        ResultCode<String> resultCode = null;
        // 获取登录态信息
        Long incId = UserInfoUtil.getIncId();
        boolean isAdmin = UserInfoUtil.isAdmin();
        Long userId = UserInfoUtil.getUserInfoVO().getUser().getUserId().longValue();
        List<Long> userGroupIds = UserInfoUtil.getGroupIds();
        Long deptId = UserInfoUtil.getDeptId();
        LOGGER.info("start set archival_document privilege,params --> {},incId:{},isOrganizedAdmin:{}",
                JSON.toJSONString(setDocumentPrivilegeVO), incId, isAdmin);
        resultCode = this.archivalFileService.setPermissionOfArchivalFile(request, setDocumentPrivilegeVO.getDocumentId(),
                setDocumentPrivilegeVO.getPermissions(), setDocumentPrivilegeVO.getPermissionsOfGenerateAccounts(),
                incId, isAdmin, userId, userGroupIds, deptId);
        //消息通知处理
        setMessage(request, resultCode, setDocumentPrivilegeVO);
        return resultCode;
    }

    /**
     * 获取归档文件权限
     *
     * @param docId
     * @return
     */
    @RequestMapping(value = "/get_permission")
    public ResultCode<List<DocumentPermissionVO>> getArchivalDocumentPermissions(Long docId) {
        Long incId = UserInfoUtil.getIncorporation().getId().longValue();
        ResultCode<List<DocumentPermissionVO>> resultCode = this.archivalFileService.getArchivalDocumentPermissions(docId, incId);
        return resultCode;
    }

}
