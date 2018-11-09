package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.dal.dao.*;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.UsersApi;
import com.quarkdata.yunpan.api.model.common.*;
import com.quarkdata.yunpan.api.model.dataobj.*;
import com.quarkdata.yunpan.api.model.vo.*;
import com.quarkdata.yunpan.api.service.DocumentPermissionService;
import com.quarkdata.yunpan.api.service.OrgFileService;
import com.quarkdata.yunpan.api.util.Md5Encrypt;
import com.quarkdata.yunpan.api.util.common.Exception.YCException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * Created by yanyq1129@thundersoft.com on 2018/9/10.
 */
@Service
@Transactional(readOnly = false, rollbackFor = Exception.class)
public class OrgFileServiceImpl extends BaseLogServiceImpl implements OrgFileService {

    private Logger logger = LoggerFactory.getLogger(OrgFileServiceImpl.class);

    private LogMapper logMapper;

    private LogDocumentRelMapper logDocumentRelMapper;

    @Resource
    public void setLogMapper(LogMapper logMapper) {
        this.logMapper = logMapper;
        super.setLogMapper(logMapper);
    }

    @Resource
    public void setLogDocumentRelMapper(LogDocumentRelMapper logDocumentRelMapper) {
        this.logDocumentRelMapper = logDocumentRelMapper;
        super.setLogDocumentRelMapper(logDocumentRelMapper);
    }

    @Value("${IMAGE_TYPE}")
    private String IMAGE_TYPE;

    @Value("${MEDIA_TYPE}")
    private String MEDIA_TYPE;

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private DocumentMapper2 documentMapper2;

    @Autowired
    RecycleMapper2 recycleMapper2;

    @Autowired
    private ExternalUserMapper externalUserMapper;

    @Autowired
    private DocumentPermissionMapper documentPermissionMapper;

    @Autowired
    private DocumentPermissionMapper2 documentPermissionMapper2;

    @Autowired
    private CommonFileServiceImpl commonFileService;

    @Autowired
    private UsersApi usersApi;


    @Autowired
    private DocumentPermissionService documentPermissionService;

    @Autowired
    private DocumentVersionMapper2 documentVersionMapper2;

    @Autowired
    private DocumentPermissionInheritMapper documentPermissionInheritMapper;


    @Transactional(readOnly = false)
    @Override
    public ResultCode<DocumentExtend> uploadFile(HttpServletRequest request, MultipartFile file, String fileName,
                                                 int isOverwrite, Long parentId, Long userId, String userName,
                                                 Long incId, Integer deptId, List<Long> groupIds, boolean isAdmin) throws IOException {
        ResultCode<DocumentExtend> rc = new ResultCode<>();
        Date now = new Date();

        // 判断是否存在同名文件
        String documentName = file.getOriginalFilename();
        if (StringUtils.isNotBlank(fileName)) {
            documentName = fileName;
        }
        DocumentExample example = new DocumentExample();
        example.createCriteria().andIncIdEqualTo(incId)
                .andTypeEqualTo(DocumentConstants.DOCUMENT_TYPE_ORGANIZED)
                .andIsDeleteEqualTo(DocumentConstants.DOCUMENT_IS_DELETE_NO)
                .andParentIdEqualTo(parentId)
                .andDocumentNameEqualTo(documentName);
        List<Document> documents = documentMapper.selectByExample(example);

        if (CollectionUtils.isNotEmpty(documents)) {
            //有同名文件
            Document document = documents.get(0);
            Boolean hasPermission = this.documentPermissionService.hasPermission(incId, userId, groupIds, deptId.longValue(), document.getId(), DocumentPermissionConstants.PermissionIndex.UPLOAD);
            if (hasPermission) {
                //文件有覆盖权限
                if (DocumentConstants.UPLOAD_IS_OVERWRITE_YES != isOverwrite) {
                    rc.setCode(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_CODE);
                    rc.setMsg(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_MSG);
                    return rc;
                } else {
                    return commonFileService.uploadExistFile(file, isOverwrite, userId, userName, incId, rc, now, documents, DocumentConstants.DOCUMENT_TYPE_ORGANIZED);
                }
            } else {
                //文件没覆盖权限，但父目录有上传权限，所以可以重命名后上传
                if (DocumentConstants.UPLOAD_IS_OVERWRITE_YES != isOverwrite) {
                    //没加Overwrite标记，提示可重命名后上传
                    rc.setCode(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_CODE_RENAME_ONLY);
                    rc.setMsg(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_MSG);
                    return rc;
                } else {
                    //加Overwrite标记，出错
                    rc.setCode(ApiMessages.NO_WRITE_PERMISSION_CODE);
                    rc.setMsg(ApiMessages.NO_WRITE_PERMISSION_MSG);
                    return rc;
                }
            }
        } else {
            //没同名文件，上传新文件
            logger.info("upload new file ...");
            Document doc = commonFileService.uploadNewDocument(request, file, parentId, userId, userName, incId,
                    now, documentName, DocumentConstants.DOCUMENT_TYPE_ORGANIZED);

            // 复制父级权限
            this.copyParentPermission(incId, parentId, doc.getId());
            // 给上传者插入创建者权限
            DocumentPermission permission_creator = new DocumentPermission();
            permission_creator.setIncId(incId);
            permission_creator.setDocumentId(doc.getId());
            permission_creator.setPermission(DocumentPermissionConstants.PermissionRole.ADMIN_OR_CREATOR);
            permission_creator.setReceiverType(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER);
            permission_creator.setReceiverId(userId);
            this.documentPermissionMapper2.insert(permission_creator);


            DocumentExtend documentExtend = new DocumentExtend();
            BeanUtils.copyProperties(doc, documentExtend);
            rc.setData(documentExtend);
            //再次检查是否有重复记录，如果有，抛出异常
            documents = documentMapper.selectByExample(example);
            if (documents.size() > 1) {
                throw new DuplicateKeyException("文件夹ID：" + parentId + "中已存在" + documentName);
            }
            return rc;
        }
    }

    /**
     * 复制直属父级权限
     * @param incId
     * @param parentId
     * @param docId
     */
    private void copyParentPermission(Long incId, Long parentId, Long docId) {
        List<DocumentPermissionVO> parentPermission = this.commonFileService.getDocumentPermissionsByDocId(parentId, incId).getData();
        if(CollectionUtils.isNotEmpty(parentPermission)) {
            for(DocumentPermissionVO documentPermissionVO: parentPermission) {
                DocumentPermission documentPermission = new DocumentPermission();
                documentPermission.setIncId(incId);
                documentPermission.setDocumentId(docId);
                documentPermission.setReceiverType(documentPermissionVO.get_type());
                documentPermission.setReceiverId(documentPermissionVO.getId());
                documentPermission.setPermission(documentPermissionVO.getPermission());
                this.documentPermissionMapper2.insert(documentPermission);
            }
        }
    }

    @Transactional(readOnly = false)
    @Override
    public ResultCode<DocumentExtend> uploadFileApp(HttpServletRequest request, MultipartFile file, String fileName,
                                                    int isOverwrite, Long parentId, Long userId, String userName,
                                                    Long incId, Integer deptId, List<Long> groupIds, boolean isAdmin) throws IOException {
        ResultCode<DocumentExtend> rc = new ResultCode<>();

        Date now = new Date();

        //判断是否存在同名文件
        String documentName = file.getOriginalFilename();
        if (StringUtils.isNotBlank(fileName)) {
            documentName = fileName;
        }
        DocumentExample example = new DocumentExample();
        example.createCriteria().andIncIdEqualTo(incId)
                .andTypeEqualTo(DocumentConstants.DOCUMENT_TYPE_ORGANIZED)
                .andIsDeleteEqualTo(DocumentConstants.DOCUMENT_IS_DELETE_NO)
                .andParentIdEqualTo(parentId)
                .andDocumentNameEqualTo(documentName);
        List<Document> documents = documentMapper.selectByExample(example);

        if (CollectionUtils.isNotEmpty(documents)) {
            //有同名文件
            Document document = documents.get(0);
            Boolean hasPermission = this.documentPermissionService.hasPermission(incId, userId, groupIds, deptId.longValue(), document.getId(), DocumentPermissionConstants.PermissionIndex.UPLOAD);
            if (DocumentConstants.DOCUMENT_ROOT_PARENTID.equals(parentId)) {
                //app在根目录下上传遇到同名文件则直接自动重命名上传
                logger.info("upload new file in org root dir ...");
                // 上传一个新的文档
                documentName = this.commonFileService.generateDirName(document.getDocumentType(), parentId, incId, documentName, DocumentConstants.DOCUMENT_TYPE_ORGANIZED, userId);
                Document doc = commonFileService.uploadNewDocument(request, file, parentId, userId,
                        userName, incId, now, documentName, DocumentConstants.DOCUMENT_TYPE_ORGANIZED);

                // 给上传者插入创建者权限
                DocumentPermission permission_creator = new DocumentPermission();
                permission_creator.setIncId(incId);
                permission_creator.setDocumentId(doc.getId());
                permission_creator.setPermission(DocumentPermissionConstants.PermissionRole.ADMIN_OR_CREATOR);
                permission_creator.setReceiverType(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER);
                permission_creator.setReceiverId(userId);
                this.documentPermissionService.save(permission_creator);

                DocumentExtend documentExtend = new DocumentExtend();
                BeanUtils.copyProperties(doc, documentExtend);
                rc.setData(documentExtend);
                return rc;
            }
            if (hasPermission) {
                //文件有读写权限
                if (this.IMAGE_TYPE.contains(document.getDocumentType().toLowerCase()) || this.MEDIA_TYPE.contains(document.getDocumentType().toLowerCase())) {
                    isOverwrite = DocumentConstants.UPLOAD_IS_OVERWRITE_YES;
                }
                if (isOverwrite != DocumentConstants.UPLOAD_IS_OVERWRITE_YES) {
                    //没加Overwrite标记，提示可重命名，可覆盖
                    rc.setCode(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_CODE);
                    rc.setMsg(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_MSG);
                    return rc;
                } else {
                    //加Overwrite标记，覆盖
                    return commonFileService.uploadExistFile(file, isOverwrite, userId,
                            userName, incId, rc, now, documents, DocumentConstants.DOCUMENT_TYPE_ORGANIZED);
                }
            } else {
                //文件没覆盖权限，但父目录有上传权限，所以可以重命名后上传
                if (isOverwrite != DocumentConstants.UPLOAD_IS_OVERWRITE_YES) {
                    //没加Overwrite标记，提示可重命名后上传
                    rc.setCode(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_CODE_RENAME_ONLY);
                    rc.setMsg(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_MSG);
                    return rc;
                } else {
                    //加Overwrite标记，出错
                    rc.setCode(ApiMessages.NO_WRITE_PERMISSION_CODE);
                    rc.setMsg(ApiMessages.NO_WRITE_PERMISSION_MSG);
                    return rc;
                }
            }
        } else {
            //没同名文件，上传新文件
            logger.info("upload new file ...");
            Document doc = commonFileService.uploadNewDocument(request, file, parentId, userId,
                    userName, incId, now, documentName, DocumentConstants.DOCUMENT_TYPE_ORGANIZED);

            // 复制父级权限
            this.copyParentPermission(incId, parentId, doc.getId());
            // 给上传者插入创建者权限
            DocumentPermission permission_creator = new DocumentPermission();
            permission_creator.setIncId(incId);
            permission_creator.setDocumentId(doc.getId());
            permission_creator.setPermission(DocumentPermissionConstants.PermissionRole.ADMIN_OR_CREATOR);
            permission_creator.setReceiverType(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER);
            permission_creator.setReceiverId(userId);
            this.documentPermissionMapper2.insert(permission_creator);

            DocumentExtend documentExtend = new DocumentExtend();
            BeanUtils.copyProperties(doc, documentExtend);
            rc.setData(documentExtend);
            //再次检查是否有重复记录，如果有，抛出异常
            documents = documentMapper.selectByExample(example);
            if (documents.size() > 1) {
                throw new DuplicateKeyException("文件夹ID：" + parentId + "中已存在" + documentName);
            }
            return rc;
        }
    }

    @Transactional(readOnly = false)
    @Override
    public ResultCode<DocumentExtend> createOrganizedDir(HttpServletRequest request, Long parentId, String documentName,
                                                         List<DocumentPrivilegeVO> permissions, List<DocumentPrivilegeVOfGenerateAccounts> permissionsOfGenerateAccounts,
                                                         Long userId, String userName, Long incId, boolean isAdmin, List<Long> groupIds, Long deptId) throws YunPanApiException {
        // 权限检查
        if(DocumentConstants.DOCUMENT_ROOT_PARENTID.equals(parentId)) {
            if (!isAdmin) {
                throw new YunPanApiException(ApiMessages.NO_ORG_ADMIN_PERMISSION_CODE, ApiMessages.NO_ORG_ADMIN_PERMISSION_MSG);
            }
        } else {
            Boolean hasPermission = this.documentPermissionService.hasPermission(incId, userId, groupIds, deptId, parentId, DocumentPermissionConstants.PermissionIndex.CREATE_DIR);
            if(!hasPermission) {
                throw new YCException(Messages.API_AUTHEXCEPTION_MSG, Messages.API_AUTHEXCEPTION_CODE);
            }
        }

        this.validPermissionArguments(permissions, permissionsOfGenerateAccounts);

        ResultCode<DocumentExtend> resultCode = new ResultCode<>();
        ResultCode<Document> createResult = commonFileService.createOrganizedDir(documentName, parentId, userId,
                userName, incId, DocumentConstants.DOCUMENT_TYPE_ORGANIZED, permissions, permissionsOfGenerateAccounts);
        DocumentExtend documentExtend = new DocumentExtend();
        BeanUtils.copyProperties(createResult.getData(), documentExtend);
        resultCode.setData(documentExtend);

        //记录操作日志
        Document document = this.documentMapper.selectByPrimaryKey(createResult.getData().getId());
        this.addDocumentLog(request, document.getId().toString(),
                DocumentConstants.DOCUMENT_ROOT_PARENTID.equals(parentId) ? ActionType.CREATE_ORGANIZED_SPACE : ActionType.CREATE_FOLDER,
                document.getDocumentName(), document.getCreateTime());

        return resultCode;
    }

    /**
     * 校验前端传过来的权限参数是否是规定格式
     * @param permissions
     * @param permissionsOfGenerateAccounts
     */
    private void validPermissionArguments(List<DocumentPrivilegeVO> permissions, List<DocumentPrivilegeVOfGenerateAccounts> permissionsOfGenerateAccounts) {
        if(CollectionUtils.isNotEmpty(permissions)) {
            for(DocumentPrivilegeVO documentPrivilegeVO: permissions) {
                String permission = documentPrivilegeVO.getPermission();
                if(!DocumentPermissionConstants.PermissionRole.ADMIN_OR_CREATOR.equals(permission)
                        && !DocumentPermissionConstants.PermissionRole.ASSOCIATED_VISIBLE.equals(permission)
                        && !DocumentPermissionConstants.PermissionRole.PREVIEW.equals(permission)
                        && !DocumentPermissionConstants.PermissionRole.UPLOAD.equals(permission)
                        && !DocumentPermissionConstants.PermissionRole.DOWNLOAD.equals(permission)
                        && !DocumentPermissionConstants.PermissionRole.EDIT.equals(permission)
                        && !DocumentPermissionConstants.PermissionRole.UPLOAD_OR_DOWNLOAD.equals(permission)
                        && !DocumentPermissionConstants.PermissionRole.UPLOAD_OR_DOWNLOAD_OR_LINK.equals(permission)
                        && !DocumentPermissionConstants.PermissionRole.LINK.equals(permission)) {
                    throw new YCException(Messages.Permission.PERMISSION_ILLEGAL_MSG + ": " + permission, Messages.Permission.PERMISSION_ILLEGAL_CODE);
                }
            }
        }
        if(CollectionUtils.isNotEmpty(permissionsOfGenerateAccounts)) {
            for(DocumentPrivilegeVOfGenerateAccounts documentPrivilegeVOfGenerateAccounts: permissionsOfGenerateAccounts) {
                String permission = documentPrivilegeVOfGenerateAccounts.getPermission();
                if(!DocumentPermissionConstants.PermissionRole.ADMIN_OR_CREATOR.equals(permission)
                        && !DocumentPermissionConstants.PermissionRole.ASSOCIATED_VISIBLE.equals(permission)
                        && !DocumentPermissionConstants.PermissionRole.PREVIEW.equals(permission)
                        && !DocumentPermissionConstants.PermissionRole.UPLOAD.equals(permission)
                        && !DocumentPermissionConstants.PermissionRole.DOWNLOAD.equals(permission)
                        && !DocumentPermissionConstants.PermissionRole.EDIT.equals(permission)
                        && !DocumentPermissionConstants.PermissionRole.UPLOAD_OR_DOWNLOAD.equals(permission)
                        && !DocumentPermissionConstants.PermissionRole.UPLOAD_OR_DOWNLOAD_OR_LINK.equals(permission)
                        && !DocumentPermissionConstants.PermissionRole.LINK.equals(permission)) {
                    throw new YCException(Messages.Permission.PERMISSION_ILLEGAL_MSG + ": " + permission, Messages.Permission.PERMISSION_ILLEGAL_CODE);
                }
            }
        }
    }


    @Transactional(readOnly = false)
    @Override
    public ResultCode<String> setDocumentPrivilege(HttpServletRequest request, Long documentId,
                                                   List<DocumentPrivilegeVO> permissions, List<DocumentPrivilegeVOfGenerateAccounts> permissionsOfGenerateAccounts, Long incId,
                                                   boolean isOrganizedAdmin, Long userId, List<Long> groupIds, Long deptId) throws YunPanApiException {
        ResultCode<String> rc = new ResultCode<String>();
        this.validPermissionArguments(permissions, permissionsOfGenerateAccounts);
        Document document = this.documentMapper.selectByPrimaryKey(documentId);

        // ❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀ 单独设置权限 ❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀
        if (CollectionUtils.isNotEmpty(permissions) || CollectionUtils.isNotEmpty(permissionsOfGenerateAccounts)) {
            // 1.判定该文档之前是否单独设置过权限,没有则在单独设置权限表插入一条记录
            DocumentPermissionInheritExample example = new DocumentPermissionInheritExample();
            example.createCriteria().andIncIdEqualTo(incId).andDocumentIdEqualTo(documentId);
            List<DocumentPermissionInherit> documentPermissionInherits = this.documentPermissionInheritMapper.selectByExample(example);
            if (CollectionUtils.isEmpty(documentPermissionInherits)) {
                DocumentPermissionInherit record = new DocumentPermissionInherit();
                record.setIncId(incId);
                record.setDocumentId(documentId);
                this.documentPermissionInheritMapper.insert(record);
            }

            // 2.删除所有非单独设置的权限数据
            this.documentPermissionMapper2.deleteChildPermission(incId, documentId, document.getIdPath(), DocumentPermissionConstants.PermissionRole.ASSOCIATED_VISIBLE, DocumentPermissionConstants.PermissionRole.ADMIN_OR_CREATOR);

            // 3.1.处理组织内部用户权限
            if (CollectionUtils.isNotEmpty(permissions)) {
                for (DocumentPrivilegeVO documentPrivilegeVO : permissions) {
                    // 给子文件赋权
                    this.documentPermissionMapper2.setChildPermission(incId, documentId, document.getIdPath(), documentPrivilegeVO.getReceiverType(), documentPrivilegeVO.getReceiverId(), documentPrivilegeVO.getPermission());
                    // 给父文件夹赋权(关联可见)
                    String[] parentIds = document.getIdPath().replace(documentId.toString(), "").split("/");
                    for (String parentId : parentIds) {
                        if (StringUtils.isNotBlank(parentId)) {
                            this.documentPermissionMapper2.setFatherPermission(incId, groupIds, deptId, Long.parseLong(parentId), documentPrivilegeVO.getReceiverType(), documentPrivilegeVO.getReceiverId(), DocumentPermissionConstants.PermissionRole.ASSOCIATED_VISIBLE);
                        }
                    }
                }
            }
            // 3.2.处理系统生成账号权限数据
            if (CollectionUtils.isNotEmpty(permissionsOfGenerateAccounts)) {
                for (DocumentPrivilegeVOfGenerateAccounts docPrivilegeVOfGenerateAccounts : permissionsOfGenerateAccounts) {
                    if (StringUtils.isNotBlank(docPrivilegeVOfGenerateAccounts.getReceiverName()) && StringUtils.isNotBlank(docPrivilegeVOfGenerateAccounts.getReceiverPassword())) {
                        Users user_save = null;
                        Boolean isExist = false;
                        // 查询该系统账号是否已经添加过, 如果添加过,仅更新权限和有效期数据
                        ResultCode<List<Users>> userByUsername = this.usersApi.getUserByUsername(request, docPrivilegeVOfGenerateAccounts.getReceiverName());
                        if (userByUsername != null && CollectionUtils.isNotEmpty(userByUsername.getData())) {
                            user_save = userByUsername.getData().get(0);
                            isExist = true;
                        } else {
                            Users user = new Users();
                            user.setUserName(docPrivilegeVOfGenerateAccounts.getReceiverName());
                            user.setPassword(Md5Encrypt.md5(docPrivilegeVOfGenerateAccounts.getReceiverPassword()));
                            user.setSource(Constants.USER_SOURCE_GENERATE_ACCOUNT);
                            user.setIncid(incId.intValue());
                            user.setUserStatus(UserStatus.USER_STATUS_ACTIVATED);
                            user_save = this.usersApi.save(user).getData();
                        }
                        if (user_save == null) {
                            throw new YCException(Messages.CREATE_GENERATE_ACCOUNT_ERROR_MSG, Messages.CREATE_GENERATE_ACCOUNT_ERROR_CODE);
                        }
                        if (isExist) {
                            // 更新账号有效期数据
                            ExternalUser record_expiryDate = new ExternalUser();
                            record_expiryDate.setCreateUser(userId);
                            record_expiryDate.setCreateTime(new Date());
                            record_expiryDate.setExpiryDate(docPrivilegeVOfGenerateAccounts.getExpiryDate());
                            ExternalUserExample example_expiryDate = new ExternalUserExample();
                            ExternalUserExample.Criteria criteria_expiryDate = example_expiryDate.createCriteria();
                            criteria_expiryDate.andIncIdEqualTo(incId).andUserIdEqualTo(user_save.getUserId().longValue());
                            this.externalUserMapper.updateByExampleSelective(record_expiryDate, example_expiryDate);
                        } else {
                            // 保存账号有效期数据
                            ExternalUser externalUser = new ExternalUser();
                            externalUser.setIncId(incId);
                            externalUser.setUserId(user_save.getUserId().longValue());
                            externalUser.setCreateUser(userId);
                            externalUser.setCreateTime(new Date());
                            externalUser.setExpiryDate(docPrivilegeVOfGenerateAccounts.getExpiryDate());
                            this.externalUserMapper.insert(externalUser);
                        }
                        // 给子文件赋权
                        this.documentPermissionMapper2.setChildPermission(incId, documentId, document.getIdPath(),
                                DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_GENERATE_ACCOUNT,
                                user_save.getUserId().longValue(),
                                docPrivilegeVOfGenerateAccounts.getPermission());
                        // 给父文件夹赋权(关联可见)
                        String[] parentIds = document.getIdPath().replace(documentId.toString(), "").split("/");
                        for (String parentId : parentIds) {
                            if (StringUtils.isNotBlank(parentId)) {
                                this.documentPermissionMapper2.setFatherPermission(incId, groupIds, deptId, Long.parseLong(parentId),
                                        DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_GENERATE_ACCOUNT,
                                        user_save.getUserId().longValue(),
                                        DocumentPermissionConstants.PermissionRole.ASSOCIATED_VISIBLE);
                            }
                        }
                    }
                }
            }
        } else {
            // ❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀ 继承父级权限 ❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀
            // 1.删除文档单独设置权限的数据
            DocumentPermissionInheritExample example_delete = new DocumentPermissionInheritExample();
            example_delete.createCriteria().andIncIdEqualTo(incId).andDocumentIdEqualTo(documentId);
            this.documentPermissionInheritMapper.deleteByExample(example_delete);
            // 2.将父级文件夹的权限复制给子孙文件
            DocumentPermissionExample example_select = new DocumentPermissionExample();
            example_select.createCriteria().andIncIdEqualTo(incId).andDocumentIdEqualTo(document.getParentId());
            List<DocumentPermission> documentPermissions_father = this.documentPermissionMapper.selectByExample(example_select);
            if (CollectionUtils.isNotEmpty(documentPermissions_father)) {
                for (DocumentPermission documentPermission : documentPermissions_father) {
                    // 给子文件赋权
                    this.documentPermissionMapper2.setChildPermission(incId, documentId, document.getIdPath(), documentPermission.getReceiverType(), documentPermission.getReceiverId(), documentPermission.getPermission());
                    // 给父文件夹赋权(关联可见)
                    String[] parentIds = document.getIdPath().replace(documentId.toString(), "").split("/");
                    for (String parentId : parentIds) {
                        if (StringUtils.isNotBlank(parentId)) {
                            this.documentPermissionMapper2.setFatherPermission(incId, groupIds, deptId, Long.parseLong(parentId), documentPermission.getReceiverType(), documentPermission.getReceiverId(), DocumentPermissionConstants.PermissionRole.ASSOCIATED_VISIBLE);
                        }
                    }
                }
            }
        }

        // 记录操作日志
        this.addDocumentLog(request, document.getId().toString(), ActionType.SET_DOCUMENT_PRIVILEGE, document.getDocumentName(), new Date());

        return rc;
    }

    @Override
    public ResultCode<String> getFileCount(Long incId, Long userId, List<Long> userGroupIds, Long deptId) {
        ResultCode<String> result = new ResultCode<String>();
        result.setData("0");
        Long count = documentMapper2.getOrganizedFileCount(incId, userId, userGroupIds, deptId);
        result.setData(String.valueOf(count));
        return result;
    }

    @Override
    public ResultCode<List<DocumentExtend>> getAllChildren(Long incId, Long userId, List<Long> userGroupIds, Long deptId, Long parentId) {
        ResultCode<List<DocumentExtend>> result = new ResultCode<List<DocumentExtend>>();
        try {
            Document document = documentMapper.selectByPrimaryKey(parentId);
            String parentPath = null;
            if (document != null && document.getIdPath() != null) {
                parentPath = document.getIdPath();
            }
            List<DocumentExtend> documents = documentMapper2.getOrganizedFileChildren(parentId == 0 ? null : String.valueOf(parentId), incId, userId, userGroupIds, deptId);
            documents = this.getPermission(incId, userId, userGroupIds, deptId, true, documents, parentPath);
            result.setData(documents);
        } catch (Exception e) {
            logger.error(e.toString());
            throw e;
        }
        return result;
    }

    /**
     * 批量删除组织文件
     *
     * @param incId
     * @param userId
     * @param groupIds
     * @param deptId
     * @param docIds
     * @param isAdmin
     * @return true-全部删除 false-部分删除
     */
    @Transactional(readOnly = false)
    @Override
    public Boolean delDocBatch(Long incId, Long userId, String userName, List<Long> groupIds, Long deptId, String docIds, boolean isAdmin) {
        String[] docIdArr = docIds.split(",");
        List<OrganizedDocumentDeleteVO> documentsToBeDelete = new ArrayList<>();
        List<Long> ids_documentsToBeDelete = new ArrayList<>();
        List<Long> allVersionId = new ArrayList<>();
        List<Long> ids_to_be_recycle = new ArrayList<>();

        if (docIdArr != null && docIdArr.length > 0) {
            for (String docId : docIdArr) {
                Document document = this.documentMapper.selectByPrimaryKey(Long.parseLong(docId));
                if (document != null) {
                    List<OrganizedDocumentDeleteVO> childrenVO = null;
                    if (document.getDocumentType().equals(DocumentConstants.DOCUMENT_DOCUMENT_TYPE_DIR)) {
                        List<DocumentExtend> children = this.documentMapper2.getOrganizedFileChildren(docId, incId, userId, groupIds, deptId);
                        childrenVO = new ArrayList<>();
                        if (children != null && children.size() > 0) {
                            for (DocumentExtend doc : children) {
                                OrganizedDocumentDeleteVO docVO = new OrganizedDocumentDeleteVO();
                                BeanUtils.copyProperties(doc, docVO);
                                childrenVO.add(docVO);
                            }
                            for (OrganizedDocumentDeleteVO organizedDocumentDeleteVO : childrenVO) {
                                Boolean hasPermission = this.documentPermissionService.hasPermission(incId, userId, groupIds, deptId, organizedDocumentDeleteVO.getId(), DocumentPermissionConstants.PermissionIndex.DELETE);
                                if (hasPermission) {
                                    organizedDocumentDeleteVO.setCanBeDelete(true);
                                } else {
                                    organizedDocumentDeleteVO.setCanBeDelete(false);
                                }
                            }
                            labelA:
                            for (OrganizedDocumentDeleteVO organizedDocumentDeleteVO : childrenVO) {
                                if (organizedDocumentDeleteVO.getDocumentType().equals(DocumentConstants.DOCUMENT_DOCUMENT_TYPE_DIR)) {
                                    // 判断其子文件是否有不可删除的, 如果有将该文件夹修改为不可删除
                                    for (OrganizedDocumentDeleteVO orgDocDeleteVO : childrenVO) {
                                        if (orgDocDeleteVO.getIdPath().contains(organizedDocumentDeleteVO.getId().toString()) && orgDocDeleteVO.getCanBeDelete() == false) {
                                            organizedDocumentDeleteVO.setCanBeDelete(false);
                                            continue labelA;
                                        }
                                    }
                                }
                            }
                        }
                        if (CollectionUtils.isNotEmpty(childrenVO)) {
                            documentsToBeDelete.addAll(childrenVO);
                        }
                    } else {
                        OrganizedDocumentDeleteVO documentDeleteVO = new OrganizedDocumentDeleteVO();
                        BeanUtils.copyProperties(document, documentDeleteVO);
                        Boolean hasPermission = this.documentPermissionService.hasPermission(incId, userId, groupIds, deptId, documentDeleteVO.getId(), DocumentPermissionConstants.PermissionIndex.DELETE);
                        if (hasPermission) {
                            documentDeleteVO.setCanBeDelete(true);
                        } else {
                            documentDeleteVO.setCanBeDelete(false);
                        }
                        documentsToBeDelete.add(documentDeleteVO);
                    }
                }
            }
        }

        if (CollectionUtils.isNotEmpty(documentsToBeDelete)) {
            List<String> tempIds = new ArrayList<>();
            for (OrganizedDocumentDeleteVO organizedDocumentDeleteVO : documentsToBeDelete) {
                if (organizedDocumentDeleteVO.getCanBeDelete() == true) {
                    ids_documentsToBeDelete.add(organizedDocumentDeleteVO.getId());
                    String idPath = organizedDocumentDeleteVO.getIdPath();
                    String[] ids = idPath.split("/");
                    if (Collections.disjoint(tempIds, Arrays.asList(ids))) {
                        ids_to_be_recycle.add(organizedDocumentDeleteVO.getId());
                        tempIds.add(organizedDocumentDeleteVO.getId().toString());
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(ids_documentsToBeDelete)) {
            // 获取所有待删的版本文件id，用于更新document_version表
            for (Long docId : ids_documentsToBeDelete) {
                List<Long> allVersionRecordsOfCurrentDocument = this.documentVersionMapper2.getAllNotDelVersionIdByDocId(docId);
                if (CollectionUtils.isNotEmpty(allVersionRecordsOfCurrentDocument)) {
                    allVersionId.addAll(allVersionRecordsOfCurrentDocument);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(ids_documentsToBeDelete)) {
            logger.info("批量删除组织文档开始！");
            logger.info("document表操作开始");
            int num1 = documentMapper2.delDocByBatch(ids_documentsToBeDelete, userId, userName);
            logger.info("document表操作结束，数目：" + num1);

            logger.info("recycle表操作开始");
            List<Recycle> recycleList = this.commonFileService.getRecycleAddList(ids_to_be_recycle, userId, incId);
            int num3 = recycleMapper2.addRecycleByBatch(recycleList);
            logger.info("recycle表操作完成，数目：" + num3);

            if (!allVersionId.isEmpty()) {
                logger.info("document_version表操作开始");
                int num2 = documentVersionMapper2.delDocVersionByIds(allVersionId, userId);
                logger.info("document_version表操作结束，数目：" + num2);
            }
            logger.info("组织doc移入回收站操作完成！");

        }
        return documentsToBeDelete.size() >= ids_documentsToBeDelete.size();
    }

    @Override
    public ResultTwoDataCode<List<DocumentPermissionVO>, List<Long>> getDocumentPermissions(Long docId, Long incId) {
        ResultTwoDataCode<List<DocumentPermissionVO>, List<Long>> objectObjectResultTwoDataCode = new ResultTwoDataCode<>();
        List<DocumentPermissionVO> result = this.commonFileService.getDocumentPermissionsByDocId(docId, incId).getData();
        //获取系统管理员
        ResultCode<List<Long>> adminUserListByFilter = usersApi.getAdminUserListByFilter();
        //过滤权限中是否有系统管理员（除了文件创建者）
        List<DocumentPermissionVO> endResult = new ArrayList<>();
        /*if (CollectionUtils.isNotEmpty(result)) {
            for (DocumentPermissionVO vo : result) {
                if (!(adminUserListByFilter.getData().contains(vo.getId()) &&
                        !vo.getPermission().equals(DocumentConstants.DOCUMENT_PERMISSION_CREATE))) {
                    endResult.add(vo);
                }
            }
        }*/
        objectObjectResultTwoDataCode.setData(result);
        objectObjectResultTwoDataCode.setSecondData(adminUserListByFilter.getData());
        return objectObjectResultTwoDataCode;
    }

    @Override
    public List<DocumentExtend> getPermission(Long incId, Long userId, List<Long> userGroupIds, Long deptId,
                                              boolean isAdmin, List<DocumentExtend> documents, String parentPath) {
        if (CollectionUtils.isNotEmpty(documents)) {
            // 如果当前用户是系统管理员或组织空间创建者,返回所有文件
            if (isAdmin) {
                if (RoleConstants.SYSTEM_ADMIN_ROLE_ID.equals(UserInfoUtil.getUserInfoVO().getRole().getId())) {

                    return documents;
                } else if (StringUtils.isNotBlank(parentPath)) {
                    String organizedSpaceId = parentPath.substring(1, parentPath.indexOf("/", 1));
                    DocumentPermissionExample documentPermissionExample = new DocumentPermissionExample();
                    documentPermissionExample.createCriteria()
                            .andIncIdEqualTo(incId)
                            .andDocumentIdEqualTo(Long.parseLong(organizedSpaceId))
                            .andReceiverTypeEqualTo(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER)
                            .andReceiverIdEqualTo(userId)
                            .andPermissionEqualTo(DocumentConstants.DOCUMENT_PERMISSION_SUPER_MANAGE);
                    List<DocumentPermission> permissionsOfSuperManage = this.documentPermissionMapper.selectByExample(documentPermissionExample);
                    if (CollectionUtils.isNotEmpty(permissionsOfSuperManage)) {
                        for (DocumentExtend documentExtend : documents) {
                            documentExtend.setPermission(DocumentConstants.DOCUMENT_PERMISSION_SUPER_MANAGE);
                        }
                        return documents;
                    }
                }
            }

            ListIterator<DocumentExtend> iter = documents.listIterator();
            while (iter.hasNext()) {
                DocumentExtend documentExtend = iter.next();
                // 看当前文件是否在共享空间根目录,如果是,只对上传者可见
                if ((!documentExtend.getDocumentType().equals(DocumentConstants.DOCUMENT_DOCUMENT_TYPE_DIR)) && documentExtend.getParentId().equals(DocumentConstants.DOCUMENT_ROOT_PARENTID)) {
                    if (!documentExtend.getCreateUser().equals(userId)) {
                        iter.remove();
                    } else {
                        documentExtend.setPermission(DocumentConstants.DOCUMENT_PERMISSION_SUPER_MANAGE);
                    }
                } else {
                    String permission = documentPermissionService.getPermission(
                            documentExtend.getIdPath(), userId, userGroupIds, deptId);
                    if (StringUtils.isBlank(permission)) {
                        iter.remove();
                    } else {
                        if (!permission.equals("-1")) {
                            documentExtend.setPermission(permission);
                        } else {
                            // 判断该关联权限是否是直接与该文档关联的
                            List<DocumentPermission> documentPermissions = new ArrayList<>();

                            DocumentPermissionExample docPermissionExample_user = new DocumentPermissionExample();
                            List<String> types = new ArrayList<>();
                            types.add("0");
                            types.add("3");
                            docPermissionExample_user.createCriteria().andIncIdEqualTo(incId)
                                    .andDocumentIdEqualTo(documentExtend.getId())
                                    .andReceiverIdEqualTo(userId).andReceiverTypeIn(types);
                            List<DocumentPermission> documentPermissions_user = this.documentPermissionMapper.selectByExample(docPermissionExample_user);
                            if (!CollectionUtils.isEmpty(documentPermissions_user)) {
                                documentPermissions.addAll(documentPermissions_user);
                            }

                            if (CollectionUtils.isNotEmpty(userGroupIds)) {
                                DocumentPermissionExample docPermissionExample_group = new DocumentPermissionExample();
                                docPermissionExample_group.createCriteria().andIncIdEqualTo(incId)
                                        .andDocumentIdEqualTo(documentExtend.getId())
                                        .andReceiverIdIn(userGroupIds).andReceiverTypeEqualTo("1");
                                List<DocumentPermission> documentPermissions_group = this.documentPermissionMapper.selectByExample(docPermissionExample_group);
                                if (!CollectionUtils.isEmpty(documentPermissions_group)) {
                                    documentPermissions.addAll(documentPermissions_group);
                                }
                            }

                            DocumentPermissionExample docPermissionExample_dept = new DocumentPermissionExample();
                            docPermissionExample_dept.createCriteria().andIncIdEqualTo(incId)
                                    .andDocumentIdEqualTo(documentExtend.getId())
                                    .andReceiverIdEqualTo(deptId).andReceiverTypeEqualTo("2");
                            List<DocumentPermission> documentPermissions_dept = this.documentPermissionMapper.selectByExample(docPermissionExample_dept);
                            if (!CollectionUtils.isEmpty(documentPermissions_dept)) {
                                documentPermissions.addAll(documentPermissions_dept);
                            }

                            if (CollectionUtils.isEmpty(documentPermissions)) {
                                iter.remove();
                            } else {
                                documentExtend.setPermission(permission);
                            }
                        }
                    }
                }

            }
        }
        return documents;
    }

    @Transactional(readOnly = false)
    @Override
    public ResultCode<String> lockOrgDocument(HttpServletRequest request, String documentIds) {
        ResultCode<String> resultCode = new ResultCode<>();
        String[] documentIdArr = documentIds.split(",");
        Users user = UserInfoUtil.getUserInfoVO().getUser();
        for (String docId : documentIdArr) {
            Document document = this.documentMapper.selectByPrimaryKey(Long.parseLong(docId));
            if (DocumentConstants.DOCUMENT_TYPE_ORGANIZED.equals(document.getType())) {
                if (DocumentConstants.IS_LOCK_NO.equals(document.getIsLock())) {
                    if (DocumentConstants.DOCUMENT_DOCUMENT_TYPE_DIR.equals(document.getDocumentType())) {
                        // 锁定文件夹
                        Document record = new Document();
                        record.setIsLock(DocumentConstants.IS_LOCK_YES);
                        record.setLockTime(new Date());
                        record.setLockUser(user.getUserId().longValue());
                        record.setLockUsername(user.getUserName());
                        DocumentExample example = new DocumentExample();
                        example.createCriteria().andIncIdEqualTo(user.getIncid().longValue()).andIdPathLike(document.getIdPath() + "%");
                        this.documentMapper.updateByExampleSelective(record, example);
                    } else {
                        // 锁定单独文件
                        Document record = new Document();
                        record.setId(document.getId());
                        record.setIsLock(DocumentConstants.IS_LOCK_YES);
                        record.setLockTime(new Date());
                        record.setLockUser(user.getUserId().longValue());
                        record.setLockUsername(user.getUserName());
                        this.documentMapper.updateByPrimaryKeySelective(record);
                    }
                } else {
                    throw new YCException(Messages.LOCK_DOCUMENT_STATUS_ERROR_MSG, Messages.LOCK_DOCUMENT_STATUS_ERROR_CODE);
                }
            } else {
                throw new YCException(Messages.LOCK_DOCUMENT_TYPE_ERROR_MSG, Messages.LOCK_DOCUMENT_TYPE_ERROR_CODE);
            }
        }
        return resultCode;
    }

    @Transactional(readOnly = false)
    @Override
    public ResultCode<String> unLockOrgDocument(HttpServletRequest request, String documentIds) {
        ResultCode<String> resultCode = new ResultCode<>();
        String[] documentIdArr = documentIds.split(",");
        Users user = UserInfoUtil.getUserInfoVO().getUser();
        for (String docId : documentIdArr) {
            Document document = this.documentMapper.selectByPrimaryKey(Long.parseLong(docId));
            if (DocumentConstants.DOCUMENT_TYPE_ORGANIZED.equals(document.getType())) {
                if (DocumentConstants.IS_DELETE_YES.equals(document.getIsLock())) {
                    if (document.getLockUser().toString().equals(user.getUserId().toString()) || UserInfoUtil.isSystemAdmin()) {
                        if (DocumentConstants.DOCUMENT_DOCUMENT_TYPE_DIR.equals(document.getDocumentType())) {
                            // 解锁文件夹
                            this.documentMapper2.unLockOrgDocument(user.getIncid(), document.getIdPath(), null);
                        } else {
                            // 解锁单独文件
                            this.documentMapper2.unLockOrgDocument(user.getIncid(), null, document.getId());
                        }
                    } else {
                        throw new YCException(Messages.UNLOCK_DOCUMENT_NO_AUTH_ERROR_MSG, Messages.UNLOCK_DOCUMENT_NO_AUTH_ERROR_CODE);
                    }
                } else {
                    throw new YCException(Messages.UNLOCK_DOCUMENT_STATUS_ERROR_MSG, Messages.UNLOCK_DOCUMENT_STATUS_ERROR_CODE);
                }
            } else {
                throw new YCException(Messages.LOCK_DOCUMENT_TYPE_ERROR_MSG, Messages.LOCK_DOCUMENT_TYPE_ERROR_CODE);
            }
        }
        return resultCode;
    }

    @Override
    public ResultCode<List<DocumentExtend>> list(Long incId, Long userId, List<Long> groupIds, Long deptId, boolean systemAdmin, Long parentId, String documentName, Integer pageNum, Integer pageSize) {
        ResultCode<List<DocumentExtend>> resultCode = new ResultCode<>();
        List<DocumentExtend> data = null;
        if (pageNum == null || pageNum <= 0 || pageSize == null || pageSize <= 0) {
            pageNum = null;
            pageSize = null;
        }
        Document parent = this.documentMapper.selectByPrimaryKey(parentId);
        if (systemAdmin) {
            data = this.documentMapper2.getOrgFileListOfSystemAdmin(incId, userId, parentId, parent == null ? "/" : parent.getIdPath(), documentName, null, pageNum, pageSize, DocumentPermissionConstants.PermissionRole.ADMIN_OR_CREATOR);
        } else {
            data = this.documentMapper2.getOrgFileList(incId, userId, groupIds, deptId, parentId, parent == null ? "/" : parent.getIdPath(), documentName, null, pageNum, pageSize, DocumentPermissionConstants.PermissionRole.ASSOCIATED_VISIBLE, null);
        }
        resultCode.setData(data);
        return resultCode;
    }

    @Override
    public List<DocumentExtend> getDirectoryThatHasUploadPermission(Long incId, Long userId, List<Long> groupIds, Long deptId, boolean isSystemAdmin, Long parentId, String documentName) {
        List<DocumentExtend> data = null;
        Document parent = this.documentMapper.selectByPrimaryKey(parentId);
        if (isSystemAdmin) {
            data = this.documentMapper2.getOrgFileListOfSystemAdmin(incId, userId, parentId, parent == null ? "/" : parent.getIdPath(), documentName, DocumentConstants.DOCUMENT_DOCUMENT_TYPE_DIR, null, null, DocumentPermissionConstants.PermissionRole.ADMIN_OR_CREATOR);
        } else {
            data = this.documentMapper2.getOrgFileList(incId, userId, groupIds, deptId, parentId, parent == null ? "/" : parent.getIdPath(), documentName, DocumentConstants.DOCUMENT_DOCUMENT_TYPE_DIR, null, null, DocumentPermissionConstants.PermissionRole.ASSOCIATED_VISIBLE, DocumentPermissionConstants.PermissionIndex.UPLOAD);
        }
        return data;
    }
}
