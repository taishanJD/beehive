package com.quarkdata.yunpan.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.amazonaws.services.s3.AmazonS3;
import com.quarkdata.quark.share.model.dataobj.Department;
import com.quarkdata.quark.share.model.dataobj.Group;
import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.dal.dao.*;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.DepartmentApi;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.GroupApi;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.UsersApi;
import com.quarkdata.yunpan.api.model.bucket.BucketRotation;
import com.quarkdata.yunpan.api.model.bucket.BucketRotationCache;
import com.quarkdata.yunpan.api.model.common.*;
import com.quarkdata.yunpan.api.model.dataobj.*;
import com.quarkdata.yunpan.api.model.vo.DocumentExtend;
import com.quarkdata.yunpan.api.model.vo.DocumentPermissionVO;
import com.quarkdata.yunpan.api.model.vo.DocumentPrivilegeVO;
import com.quarkdata.yunpan.api.model.vo.DocumentPrivilegeVOfGenerateAccounts;
import com.quarkdata.yunpan.api.service.DocumentPermissionService;
import com.quarkdata.yunpan.api.service.FullTextService;
import com.quarkdata.yunpan.api.util.EsUtils;
import com.quarkdata.yunpan.api.util.FileUtils;
import com.quarkdata.yunpan.api.util.Md5Encrypt;
import com.quarkdata.yunpan.api.util.S3Utils;
import com.quarkdata.yunpan.api.util.common.config.Global;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class CommonFileServiceImpl extends BaseLogServiceImpl {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IncConfigMapper incConfigMapper;

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private DocumentMapper2 documentMapper2;

    @Autowired
    private DocumentVersionMapper documentVersionMapper;

    @Autowired
    private DocumentVersionMapper2 documentVersionMapper2;

    @Autowired
    private DocumentCephDeleteMapper documentCephDeleteMapper;

    @Autowired
    private DocumentPermissionMapper documentPermissionMapper;

    @Autowired
    private ExternalUserMapper externalUserMapper;

    @Autowired
    private FullTextService fullTextService;

    @Autowired
    private DocumentPermissionService documentPermissionService;

    @Autowired
    private UsersApi usersApi;

    @Autowired
    private GroupApi groupApi;

    @Autowired
    private DepartmentApi departmentApi;

    private LogMapper logMapper;

    private LogDocumentRelMapper logDocumentRelMapper;

    @Autowired
    private DocumentPermissionMapper2 documentPermissionMapper2;

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


    public String uploadIntoCeph(MultipartFile file, Long incId, String key)
            throws IOException {
        // 查询bucket
        AmazonS3 connect = getCephConnect(incId);

        String cephBucket = getCephBucket(incId, connect);

        File multipartToFile = FileUtils.multipartToFile(file);
        logger.info("upload into ceph,bucket is : {},key is : {}", cephBucket, key);
        // 上传到ceph
        S3Utils.uploadFile(cephBucket, key, multipartToFile, connect);
        logger.info("upload into ceph success");

        connect.shutdown();
        return cephBucket;
    }

    public AmazonS3 getCephConnect(Long incId) {
        IncConfigExample incConfigExample = new IncConfigExample();
        incConfigExample.createCriteria().andIncIdEqualTo(incId);
        IncConfig incConfig = incConfigMapper.selectByExample(incConfigExample)
                .get(0);
        String cephAccessKey = incConfig.getCephAccessKey();
        String cephSecretKey = incConfig.getCephSecretKey();
        String cephUrl = incConfig.getCephUrl();
        AmazonS3 connect = S3Utils.getConnect(cephAccessKey, cephSecretKey,
                cephUrl);
        return connect;
    }

    public String getCephBucket(Long incId, AmazonS3 connect) {
        BucketRotation bucketRotation = BucketRotationCache.get(incId);
        if (null == bucketRotation) {

            String[] bucketNames = S3Utils.getBucketNames(connect);
            logger.info("get bucket names is : {}",
                    JSON.toJSONString(bucketNames));
            bucketRotation = new BucketRotation(bucketNames);

            BucketRotationCache.put(incId, bucketRotation);
        }

        String cephBucket = bucketRotation.getBucket();
        return cephBucket;
    }


    public String getIdPath(Long parentId, Long documentId) {
        String idPath = null;
        if (parentId.equals(0L)) {
            idPath = "/" + documentId + "/";
        } else {
            Document document = documentMapper.selectByPrimaryKey(parentId);
            idPath = document.getIdPath() + documentId + "/";
        }
        return idPath;
    }

    public Document uploadNewDocument(HttpServletRequest request, MultipartFile file, Long parentId,
                                      Long userId, String userName, Long incId, Date now,
                                      String documentName, String docType) throws IOException {
        // 不存在就插入到document
        Document document = new Document();
        document.setIncId(incId);
        document.setParentId(parentId);
        // document.setDocumentVersionId(documentVersionId);
        document.setDocumentName(documentName);
        document.setType(docType);
        document.setDocumentType(FilenameUtils.getExtension(documentName));
        // document.setIdPath(idPath);
        document.setSize(file.getSize());
        document.setCreateTime(now);
        document.setCreateUser(userId);
        document.setCreateUsername(userName);
        document.setUpdateTime(now);
        document.setUpdateUser(userId);
        document.setUpdateUsername(userName);

        logger.info("insert into Document,obj is : {}",
                JSON.toJSONString(document));

        documentMapper.insertSelective(document);

        String uuid = UUID.randomUUID().toString();
        // 上传到ceph
        String cephBucket = uploadIntoCeph(file, incId, uuid);

        // 插入版本记录表
        DocumentVersion documentVersion = new DocumentVersion();

        documentVersion.setIncId(incId);
        documentVersion.setDocumentId(document.getId());
        documentVersion.setVersion(1);// 设置起始版本
        documentVersion
                .setOperateType(DocumentConstants.DOCUMENT_OPERATE_TYPE_UPLOAD);
        documentVersion.setCreateTime(now);
        documentVersion.setCreateUser(userId);
        documentVersion.setCreateUsername(userName);
        documentVersion.setUpdateTime(now);
        documentVersion.setUpdateUser(userId);
        documentVersion.setUpdateUsername(userName);
        documentVersion.setMd5(FileUtils.getBigFileMD5(file));
        documentVersion.setCephBucket(cephBucket);
        documentVersion.setCephBucketKey(uuid);
        documentVersion.setSize(file.getSize());

        logger.info("----------##################----------------------insert into DocumentVersion,obj is : {}",
                JSON.toJSONString(documentVersion));

        try {
            documentVersionMapper.insertSelective(documentVersion);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("上传新文件,版本信息插入失败");
        }

        // 修改之前插入的document，补全documentVersionId、idPath
        Document updateDoc = new Document();
        updateDoc.setId(document.getId());
        String idPath = getIdPath(parentId, document.getId());
        updateDoc.setIdPath(idPath);// /1/
        updateDoc.setDocumentVersionId(documentVersion.getId());

        document.setDocumentVersionId(documentVersion.getId());
        document.setIdPath(idPath);
        logger.info("update Document by id,update fields is : {}",
                JSON.toJSONString(updateDoc));

        documentMapper.updateByPrimaryKeySelective(updateDoc);
        try {
            //将文件添加到es索引中  先判断当前用户所在组织索引是否存在
            if (fullTextService.isExistsIndex(Global.getConfig("index.prefixion") + incId)) {
                fullTextService.createDocumentIndex(updateDoc.getDocumentVersionId().toString(), document.getId().toString(), document.getDocumentName(), document.getType(), document.getDocumentType(), EsUtils.readFile(file));
            } else {
                fullTextService.insertToFailList(document.getIncId(), userId, updateDoc.getDocumentVersionId().toString(), document.getDocumentType());
            }
        } catch (Exception e) {
            this.logger.error("<<<<<< create ES_index error <<<<<<", e);
        }

        // 记录操作日志
        if (docType.equals("1")) {
            this.addDocumentLog(request, document.getId().toString(), ActionType.UPLOAD, documentName, document.getCreateTime());
        }
        if (docType.equals("0")) {
            this.addDocumentLog(request, document.getId().toString(), ActionType.UPLOAD, documentName, document.getCreateTime());
        }
        if (docType.equals("3")) {
            this.addDocumentLog(request, document.getId().toString(), ActionType.UPLOAD, documentName, document.getCreateTime());
        }
        return document;
    }

    public ResultCode<DocumentExtend> uploadExistFile(MultipartFile file, int isOverwrite, Long userId, String userName, Long incId,
                                                      ResultCode<DocumentExtend> rc, Date now, List<Document> documents, String docType) throws IOException {
        // 是否覆盖
        if (isOverwrite != DocumentConstants.UPLOAD_IS_OVERWRITE_YES) {
            rc.setCode(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_CODE);
            rc.setMsg(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_MSG);
            return rc;
        }

        // 存在 就修改
        Document document = documents.get(0);
        // 修改版本记录表 -打上删除标记
        // 第一条记录就是版本最高
        DocumentVersionExample documentVersionExample = new DocumentVersionExample();
        documentVersionExample.setOrderByClause("version DESC");
        documentVersionExample.createCriteria()
                .andDocumentIdEqualTo(document.getId())
                .andIsDeleteEqualTo(DocumentConstants.IS_DELETE_NO);

        List<DocumentVersion> docVers = documentVersionMapper
                .selectByExample(documentVersionExample);

        Integer lastVersion = 1;

        if (CollectionUtils.isNotEmpty(docVers)) {
            lastVersion = docVers.get(0).getVersion();
            // 如果是个人文件上传，会将历史版本置为delete状态，并insert到删除ceph文件的队列表
            if (docType.equals(DocumentConstants.DOCUMENT_TYPE_PERSONAL)) {
                for (DocumentVersion docVer : docVers) {
                    logger.info("update DocumentVersion to delete status,before is : {}", JSON.toJSONString(docVer));
                    docVer.setIsDelete(DocumentConstants.IS_DELETE_YES);
                    logger.info("update DocumentVersion to delete status,after is : {}", JSON.toJSONString(docVer));
                    documentVersionMapper.updateByPrimaryKeySelective(docVer);
                    // 将需要删除的document数据添加到 删除ceph文件的队列表
                    DocumentCephDelete dcd = new DocumentCephDelete();

                    dcd.setIncId(incId);
                    dcd.setDocumentVersionId(docVer.getId());
                    dcd.setCreateTime(now);
                    dcd.setCreateUser(userId);

                    logger.info("insert into DocumentCephDelete,obj is : {}", JSON.toJSONString(dcd));

                    documentCephDeleteMapper.insertSelective(dcd);
                }
            }
        }

        String uuid = UUID.randomUUID().toString();
        String cephBucket = uploadIntoCeph(file, incId, uuid);

        // 插入一条新的版本记录
        DocumentVersion documentVersion = new DocumentVersion();

        documentVersion.setIncId(incId);
        documentVersion.setDocumentId(document.getId());
        documentVersion.setVersion(lastVersion + 1);
        documentVersion.setOperateType(DocumentConstants.DOCUMENT_OPERATE_TYPE_UPLOAD);
        documentVersion.setCreateTime(now);
        documentVersion.setCreateUser(userId);
        documentVersion.setCreateUsername(userName);
        documentVersion.setUpdateTime(now);
        documentVersion.setUpdateUser(userId);
        documentVersion.setUpdateUsername(userName);
        documentVersion.setSize(file.getSize());
        documentVersion.setMd5(FileUtils.getBigFileMD5(file));

        documentVersion.setCephBucket(cephBucket);

        documentVersion.setCephBucketKey(uuid);

        logger.info("insert into DocumentVersion,obj is : {}", JSON.toJSONString(documentVersion));

        try {
            documentVersionMapper.insertSelective(documentVersion);
        } catch (Exception e) {
            logger.error("覆盖上传,版本信息插入失败");
            IncConfigExample example = new IncConfigExample();
            example.createCriteria().andIncIdEqualTo(incId);
            IncConfig incConfig = this.incConfigMapper.selectByExample(example).get(0);
            AmazonS3 connect = S3Utils.getConnect(incConfig.getCephAccessKey(), incConfig.getCephSecretKey(), incConfig.getCephUrl());
            logger.info("从ceph删除失败版本文件, bucket is {}, bucketKey is {}", cephBucket, uuid);
            S3Utils.delObject(cephBucket, uuid, connect);
            logger.info("从ceph删除失败版本文件成功");
        }

        // 修改document表
        Document updateDoc = new Document();
        updateDoc.setId(document.getId());
        updateDoc.setDocumentVersionId(documentVersion.getId());
        updateDoc.setSize(file.getSize());
        updateDoc.setCreateTime(now);
        updateDoc.setUpdateTime(now);
        updateDoc.setUpdateUser(userId);
        updateDoc.setUpdateUsername(userName);
        updateDoc.setIsShare(DocumentConstants.DOCUMENT_IS_SHARE_NO);
        try {
            if (fullTextService.isExistsIndex(Global.getConfig("index.prefixion") + incId)) {
                fullTextService.createDocumentIndex(documentVersion.getId().toString(), updateDoc.getId().toString(), document.getDocumentName(), document.getType(), document.getDocumentType(), EsUtils.readFile(file));
            } else {
                fullTextService.insertToFailList(document.getIncId(), userId, documentVersion.getId().toString(), document.getDocumentType());
            }
        } catch (IOException e) {
            this.logger.error("<<<<<<< create ES_index error <<<<<<");
        }
        logger.info("update Document by id,update fields : {}", JSON.toJSONString(updateDoc));

        documentMapper.updateByPrimaryKeySelective(updateDoc);
        DocumentExtend documentExtend = new DocumentExtend();
        BeanUtils.copyProperties(updateDoc, documentExtend);
        documentExtend.setDocumentName(document.getDocumentName());
        rc.setData(documentExtend);
        return rc;
    }

    public ResultCode<Document> createOrganizedSpace(HttpServletRequest request, String documentName,
                                                     Long parentId, Long userId, String userName, Long incId,
                                                     String docType, List<DocumentPrivilegeVO> permissions, List<DocumentPrivilegeVOfGenerateAccounts> permissionsOfGenerateAccounts) throws YunPanApiException {
        ResultCode<Document> resultCode = new ResultCode<Document>();
        Document document = createDir_(documentName, parentId, userId, userName, incId, docType);
        if (CollectionUtils.isNotEmpty(permissions)) {

            // 过滤调用者传过来的创建者权限,避免出现重复数据
            ListIterator<DocumentPrivilegeVO> iterator = permissions.listIterator();
            while (iterator.hasNext()) {
                DocumentPrivilegeVO next = iterator.next();
                if (next.getPermission().equals(DocumentConstants.DOCUMENT_PERMISSION_CREATE) || next.getPermission().equals(DocumentConstants.DOCUMENT_PERMISSION_SUPER_MANAGE)) {
                    iterator.remove();
                }
            }

            // 添加权限
            for (DocumentPrivilegeVO dp : permissions) {
                DocumentPermission record = new DocumentPermission();
                record.setIncId(incId);
                record.setDocumentId(document.getId());
                BeanUtils.copyProperties(dp, record);

                logger.info("insert into DocumentPermission,obj is : {}",
                        JSON.toJSONString(record));

                documentPermissionMapper2.insert(record);
            }
        }
        // 处理单独生成账号赋权:
        if (CollectionUtils.isNotEmpty(permissionsOfGenerateAccounts)) {
            for (DocumentPrivilegeVOfGenerateAccounts docPrivilegeVOfGenerateAccounts : permissionsOfGenerateAccounts) {
                // 1.将生成的账户插入数据库
                if (StringUtils.isNotBlank(docPrivilegeVOfGenerateAccounts.getReceiverName())
                        && StringUtils.isNotBlank(docPrivilegeVOfGenerateAccounts.getReceiverPassword())) {
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
                    if (user_save != null) {
                        if (isExist) {
                            // 更新
                            // 2.更新该账户权限
                            DocumentPermission record_permission = new DocumentPermission();
                            record_permission.setPermission(docPrivilegeVOfGenerateAccounts.getPermission());

                            DocumentPermissionExample example_permission = new DocumentPermissionExample();
                            DocumentPermissionExample.Criteria criteria_permission = example_permission.createCriteria();
                            criteria_permission.andIncIdEqualTo(incId).andDocumentIdEqualTo(document.getId()).andReceiverTypeEqualTo("3").andReceiverIdEqualTo(user_save.getUserId().longValue());
                            this.documentPermissionMapper.updateByExampleSelective(record_permission, example_permission);
                            // 3.更新账号有效期数据
                            ExternalUser record_expiryDate = new ExternalUser();
                            record_expiryDate.setCreateUser(userId);
                            record_expiryDate.setCreateTime(new Date());
                            record_expiryDate.setExpiryDate(docPrivilegeVOfGenerateAccounts.getExpiryDate());

                            ExternalUserExample example_expiryDate = new ExternalUserExample();
                            ExternalUserExample.Criteria criteria_expiryDate = example_expiryDate.createCriteria();
                            criteria_expiryDate.andIncIdEqualTo(incId).andUserIdEqualTo(user_save.getUserId().longValue());
                            this.externalUserMapper.updateByExampleSelective(record_expiryDate, example_expiryDate);
                        } else {
                            // 新建
                            // 2.给该账户赋权
                            DocumentPermission record = new DocumentPermission();
                            record.setIncId(incId);
                            record.setDocumentId(document.getId());
                            record.setReceiverId(user_save.getUserId().longValue());
                            record.setReceiverType("3");
                            record.setPermission(docPrivilegeVOfGenerateAccounts.getPermission());

                            this.documentPermissionMapper2.insert(record);
                            this.logger.info("insert into DocumentPermission,obj is : {}",
                                    JSON.toJSONString(record));

                            // 3.保存账号有效期数据
                            ExternalUser externalUser = new ExternalUser();
                            externalUser.setIncId(incId);
                            externalUser.setUserId(user_save.getUserId().longValue());
                            externalUser.setCreateUser(userId);
                            externalUser.setCreateTime(new Date());
                            externalUser.setExpiryDate(docPrivilegeVOfGenerateAccounts.getExpiryDate());
                            this.externalUserMapper.insert(externalUser);
                        }
                    }
                }
            }
        }
        //将生成的文件id传回
        resultCode.setData(document);
        return resultCode;
    }

    public ResultCode<Document> createOrganizedDir(String documentName, Long parentId, Long userId, String userName, Long incId, String docType,
                                                         List<DocumentPrivilegeVO> permissions, List<DocumentPrivilegeVOfGenerateAccounts> permissionsOfGenerateAccounts) throws YunPanApiException {
        ResultCode<Document> resultCode = new ResultCode<Document>();

        Document document = createDir_(documentName, parentId, userId, userName, incId, docType);

        if (CollectionUtils.isNotEmpty(permissions)) {
            // 添加权限
            for (DocumentPrivilegeVO dp : permissions) {
                DocumentPermission record = new DocumentPermission();
                record.setIncId(incId);
                record.setDocumentId(document.getId());
                BeanUtils.copyProperties(dp, record);
                documentPermissionMapper2.insert(record);
            }
        }

        if (CollectionUtils.isNotEmpty(permissionsOfGenerateAccounts)) {
            for (DocumentPrivilegeVOfGenerateAccounts docPrivilegeVOfGenerateAccounts : permissionsOfGenerateAccounts) {
                if (StringUtils.isNotBlank(docPrivilegeVOfGenerateAccounts.getReceiverName())
                        && StringUtils.isNotBlank(docPrivilegeVOfGenerateAccounts.getReceiverPassword())) {

                    Users user = new Users();
                    user.setUserName(docPrivilegeVOfGenerateAccounts.getReceiverName());
                    user.setPassword(Md5Encrypt.md5(docPrivilegeVOfGenerateAccounts.getReceiverPassword()));
                    user.setSource(Constants.USER_SOURCE_GENERATE_ACCOUNT);
                    user.setIncid(incId.intValue());
                    user.setUserStatus(UserStatus.USER_STATUS_ACTIVATED);
                    user = this.usersApi.save(user).getData();

                    DocumentPermission record = new DocumentPermission();
                    record.setIncId(incId);
                    record.setDocumentId(document.getId());
                    record.setReceiverId(user.getUserId().longValue());
                    record.setReceiverType("3");
                    record.setPermission(docPrivilegeVOfGenerateAccounts.getPermission());
                    this.documentPermissionMapper2.insert(record);

                    ExternalUser externalUser = new ExternalUser();
                    externalUser.setIncId(incId);
                    externalUser.setUserId(user.getUserId().longValue());
                    externalUser.setCreateUser(userId);
                    externalUser.setCreateTime(new Date());
                    externalUser.setExpiryDate(docPrivilegeVOfGenerateAccounts.getExpiryDate());
                    this.externalUserMapper.insert(externalUser);

                }
            }
        }

        // 给组织文件夹创建者赋予创建者权限
        DocumentPermission permission_creator = new DocumentPermission();
        permission_creator.setIncId(incId);
        permission_creator.setDocumentId(document.getId());
        permission_creator.setReceiverType(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER);
        permission_creator.setReceiverId(userId);
        permission_creator.setPermission(DocumentPermissionConstants.PermissionRole.ADMIN_OR_CREATOR);
        this.documentPermissionMapper2.insert(permission_creator);

        // 给组织空间创建者赋予创建者权限
        if(!DocumentConstants.DOCUMENT_ROOT_PARENTID.equals(document.getParentId())) {
            Document rootDir = this.getRootDirectory(document.getIdPath());
            DocumentPermission permission_org_space_creator = new DocumentPermission();
            permission_org_space_creator.setIncId(incId);
            permission_org_space_creator.setDocumentId(document.getId());
            permission_org_space_creator.setReceiverType(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER);
            permission_org_space_creator.setReceiverId(rootDir.getCreateUser()); // 组织空间创建者
            permission_org_space_creator.setPermission(DocumentPermissionConstants.PermissionRole.ADMIN_OR_CREATOR);
            this.documentPermissionMapper2.insert(permission_org_space_creator);
        }

        resultCode.setData(document);
        return resultCode;
    }

    /**
     * 获取文件根目录id
     * @param idPath
     * @return
     */
    private Document getRootDirectory(String idPath) {
        String[] idArr = idPath.split("/");
        for(String docId: idArr) {
            if(StringUtils.isNotBlank(docId)) {
                return this.documentMapper.selectByPrimaryKey(Long.parseLong(docId));
            }
        }
        return null;
    }

    public ResultCode<Document> createDir(String documentName, Long parentId, Long userId, String userName, Long incId, String docType) throws YunPanApiException {

        ResultCode<Document> rc = new ResultCode<Document>();

        // 检查是否有同名文件夹存在
		/*rc = isExistDoc(incId, parentId, documentName,docType,rc);
		if (!rc.getCode().equals(Messages.SUCCESS_CODE)) {
			return rc;
		}*/

        Document document = createDir_(documentName, parentId, userId, userName, incId, docType);
        rc.setData(document);
        return rc;
    }

    public ResultCode<String> isExistDoc(Long incId, Long userId, Long parentId, String documentName, String type, ResultCode<String> rc) {
        List<Document> docs = null;
        if (DocumentConstants.DOCUMENT_TYPE_PERSONAL.equals(type)) {
            // 个人文件
            DocumentExample example = new DocumentExample();
            example.createCriteria().andIncIdEqualTo(incId)
                    .andTypeEqualTo(type)
                    .andCreateUserEqualTo(userId)
                    .andParentIdEqualTo(parentId)
                    .andDocumentNameEqualTo(documentName)
                    .andIsDeleteEqualTo(DocumentConstants.IS_DELETE_NO);
            docs = documentMapper.selectByExample(example);

        }
        if (DocumentConstants.DOCUMENT_TYPE_ORGANIZED.equals(type)) {
            // 组织文件
            DocumentExample example = new DocumentExample();
            example.createCriteria().andIncIdEqualTo(incId)
                    .andTypeEqualTo(type)
                    .andParentIdEqualTo(parentId)
                    .andDocumentNameEqualTo(documentName)
                    .andIsDeleteEqualTo(DocumentConstants.IS_DELETE_NO);
            docs = documentMapper.selectByExample(example);
        }

        if (CollectionUtils.isNotEmpty(docs)) {
            rc.setCode(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_CODE);
            rc.setMsg(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_MSG);
            rc.setData("false");
        }

        return rc;
    }

    public boolean isExistDirectory(Long incId, Long parentId, String documentName, String type) {
        DocumentExample example = new DocumentExample();

        example.createCriteria().andIncIdEqualTo(incId)
                .andTypeEqualTo(type)
                .andDocumentTypeEqualTo("dir")
                .andParentIdEqualTo(parentId)
                .andDocumentNameEqualTo(documentName)
                .andIsDeleteEqualTo(DocumentConstants.IS_DELETE_NO);

        List<Document> docs = documentMapper.selectByExample(example);

        if (CollectionUtils.isNotEmpty(docs)) {
            return true;
        }
        return false;
    }

    private Document createDir_(String documentName, Long parentId, Long userId, String userName, Long incId, String docType) throws YunPanApiException {
        // 检查数据库中是否已经存在，已经存在则提示 文件名称已经存在
        DocumentExample example = new DocumentExample();
        DocumentExample.Criteria criteria = example.createCriteria();
        criteria.andIncIdEqualTo(incId)
                .andParentIdEqualTo(parentId)
                .andTypeEqualTo(docType)
                .andDocumentNameEqualTo(documentName)
                .andIsDeleteEqualTo(DocumentConstants.DOCUMENT_IS_DELETE_NO);// 未删除的
        if (DocumentConstants.DOCUMENT_TYPE_PERSONAL.equals(docType)) {
            criteria.andCreateUserEqualTo(userId);
        }
        boolean isExist = documentMapper.countByExample(example) > 0 ? true : false;
        if (isExist) {
            throw new YunPanApiException(ApiMessages.CREATE_DIR_ALREADY_EXISTED_CODE,
                    ApiMessages.CREATE_DIR_ALREADY_EXISTED_MSG);
        }
        Document record = new Document();

        record.setIncId(incId);
        record.setParentId(parentId);
        record.setDocumentName(documentName);
        record.setType(docType);
        record.setDocumentType(DocumentConstants.DOCUMENT_DOCUMENT_TYPE_DIR);
        Date now = new Date();
        record.setCreateTime(now);
        record.setCreateUser(userId);
        record.setCreateUsername(userName);
        record.setUpdateTime(now);
        record.setUpdateUser(userId);
        record.setUpdateUsername(userName);
        record.setIsShare(DocumentConstants.DOCUMENT_IS_SHARE_NO);
        record.setIsDelete(DocumentConstants.IS_DELETE_NO);
        record.setIsLock(DocumentConstants.IS_LOCK_NO);

        logger.info("insert into Document,obj is : {}",
                JSON.toJSONString(record));

        documentMapper.insert(record);

        // 补全idPath
        String idPath = getIdPath(parentId, record.getId());
        Document updateDoc = new Document();
        updateDoc.setId(record.getId());
        updateDoc.setIdPath(idPath);
        record.setIdPath(idPath);
        documentMapper.updateByPrimaryKeySelective(updateDoc);

        try {
            //将文件添加es索引 先判断当前用户所在组织索引是否存在
            if (fullTextService.isExistsIndex(Global.getConfig("index.prefixion") + incId)) {
                fullTextService.createDocumentIndex("dir_" + record.getId().toString(), record.getId().toString(), record.getDocumentName(), record.getType(), record.getDocumentType(), null);
            } else {
                fullTextService.insertToFailList(incId, userId, record.getId().toString(), record.getDocumentType());
            }
        } catch (Exception e) {
            this.logger.error("create index error, obj is : {}", JSON.toJSONString(updateDoc));
        }
        logger.info("update Document,obj is : {}", JSON.toJSONString(updateDoc));

        return record;
    }

    // 递归检查上传权限
    public boolean recursionCheckUploadPrivilege(Long userId, Integer deptId,
                                                 List<Long> groupIds, Long parentId) {
        if (checkUploadPrivilege(userId, deptId, groupIds, parentId)) {
            return true;
        } else {
            if (parentId.equals(DocumentConstants.DOCUMENT_ROOT_PARENTID)) {
                // 已经是根目录
                return false;
            }
            // 查找父级id
            Long parentId2 = documentMapper.selectByPrimaryKey(parentId)
                    .getParentId();
            return recursionCheckUploadPrivilege(userId, deptId, groupIds,
                    parentId2);
        }
    }

    public boolean checkUploadPrivilege(Long userId, Integer deptId, List<Long> groupIds, Long parentId) {
        if (RoleConstants.SYSTEM_ADMIN_ROLE_ID.equals(UserInfoUtil.getUserInfoVO().getRole().getId())) {
            return true;
        }
        long hasPrivilege = documentMapper2.checkUploadPrivilege(userId,
                groupIds, deptId.longValue(), parentId);
        return hasPrivilege > 0 ? true : false;
    }

    public ResultCode<List<DocumentPermissionVO>> getDocumentPermissionsByDocId( Long docId, Long incId) {
        DocumentPermissionExample example = new DocumentPermissionExample();
        example.createCriteria()
                .andDocumentIdEqualTo(docId)
                .andIncIdEqualTo(incId)
                .andPermissionNotEqualTo(DocumentConstants.DOCUMENT_PERMISSION_ASSOCIATION_READ)
                .andPermissionNotEqualTo(DocumentConstants.DOCUMENT_PERMISSION_SUPER_MANAGE)
                .andPermissionNotEqualTo(DocumentPermissionConstants.PermissionRole.ASSOCIATED_VISIBLE);
        List<DocumentPermission> list = documentPermissionMapper.selectByExample(example);
        List<DocumentPermissionVO> data = this.encapsulateDocumentPermission(incId, docId, list);
        ResultCode<List<DocumentPermissionVO>> resultCode = new ResultCode<List<DocumentPermissionVO>>();
        resultCode.setData(data);
        return resultCode;
    }

    // 包装文档权限(设置显示名等...)
    public List<DocumentPermissionVO> encapsulateDocumentPermission(Long incId, Long docId, List<DocumentPermission> permissions) {
        List<DocumentPermissionVO> data = new ArrayList<>();
        List<String> userIds = new ArrayList<>();
        List<String> generateUserIds = new ArrayList<>();
        List<String> userGroupIds = new ArrayList<>();
        List<String> deptIds = new ArrayList<>();
        for (DocumentPermission dp : permissions) {
            DocumentPermissionVO vo = new DocumentPermissionVO();
            vo.setId(dp.getReceiverId());
            vo.set_type(dp.getReceiverType());
            vo.setPermission(dp.getPermission());
            if (dp.getDocumentId().toString().equals(docId + "")) {
                vo.setSource("self");
            } else {
                vo.setSource("father");
            }
            data.add(vo);
            if (dp.getReceiverType().equals(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER)) {
                userIds.add(dp.getReceiverId() + "");
            } else if (dp.getReceiverType().equals(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_GROUP)) {
                userGroupIds.add(dp.getReceiverId() + "");
            } else if (dp.getReceiverType().equals(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_DEPT)) {
                deptIds.add(dp.getReceiverId() + "");
            } else if (dp.getReceiverType().equals(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_GENERATE_ACCOUNT)) {
                generateUserIds.add(dp.getReceiverId() + "");
            }
        }
        // 获取用户
        List<Users> users = null;
        if (userIds.size() > 0) {
            String[] userIdsArr = new String[userIds.size()];
            userIdsArr = userIds.toArray(userIdsArr);
            users = usersApi.getUserDetails(userIdsArr, incId + "").getData();
        }
        // 获取系统生成账号
        List<Users> users_generate = null;
        if (generateUserIds.size() > 0) {
            String[] generateUserIdsArr = new String[generateUserIds.size()];
            generateUserIdsArr = generateUserIds.toArray(generateUserIdsArr);
            users_generate = usersApi.getUserDetails(generateUserIdsArr, incId + "").getData();
        }
        // 获取用户组
        List<Group> userGroups = null;
        if (userGroupIds.size() > 0) {
            String[] userGroupIdsArr = new String[userGroupIds.size()];
            userGroupIdsArr = userGroupIds.toArray(userGroupIdsArr);
            userGroups = groupApi.groupDetails(userGroupIdsArr, incId + "").getData();
        }
        // 获取部门
        List<Department> departments = null;
        if (deptIds.size() > 0) {
            String[] deptIdsArr = new String[deptIds.size()];
            deptIdsArr = deptIds.toArray(deptIdsArr);
            // 获取部门
            departments = departmentApi.departmentDetails(deptIdsArr, incId + "").getData();
        }

        if (CollectionUtils.isNotEmpty(data)) {
            ListIterator<DocumentPermissionVO> iterator = data.listIterator();
            labelA:
            while (iterator.hasNext()) {
                DocumentPermissionVO dp = iterator.next();
                if (dp.get_type().equals("0")) {
                    // 用户
                    if (CollectionUtils.isNotEmpty(users)) {
                        for (Users u : users) {
                            if (dp.getId().equals(u.getUserId().longValue())) {
                                String userName = u.getUserName();
                                String displayName = u.getDisplayName();
                                String email = u.getEmail();
                                String mobile = u.getMobile();
                                if (userName != null) {
                                    dp.setName(userName);
                                }
                                if (displayName != null) {
                                    dp.setDisplayName(displayName);
                                }
                                if (email != null) {
                                    dp.setEmail(email);
                                }
                                if (mobile != null) {
                                    dp.setMobile(mobile);
                                }
                                continue labelA;
                            }
                        }
                        iterator.remove();
                    } else {
                        iterator.remove();
                    }
                } else if (dp.get_type().equals("3")) {
                    // 系统生成账户
                    if (CollectionUtils.isNotEmpty(users_generate)) {
                        for (Users u : users_generate) {
                            if (dp.getId().equals(u.getUserId().longValue())) {
                                dp.setName(u.getUserName());
                                dp.setPassword("Bee12345");
                                // 获取账号有效期
                                ExternalUserExample externalUserExample = new ExternalUserExample();
                                externalUserExample.createCriteria().andIncIdEqualTo(incId).andUserIdEqualTo(u.getUserId().longValue());
                                List<ExternalUser> externalUsers = this.externalUserMapper.selectByExample(externalUserExample);
                                if (CollectionUtils.isNotEmpty(externalUsers)) {
                                    dp.setExpiryDate(externalUsers.get(0).getExpiryDate());
                                }
                                continue labelA;
                            }
                        }
                        iterator.remove();
                    } else {
                        iterator.remove();
                    }
                } else if (dp.get_type().equals("1")) {
                    // 用户组
                    if (CollectionUtils.isNotEmpty(userGroups)) {
                        for (Group g : userGroups) {
                            if (dp.getId().equals(g.getId().longValue())) {
                                dp.setName(g.getGroupName());
                                continue labelA;
                            }
                        }
                        iterator.remove();
                    } else {
                        iterator.remove();
                    }
                } else if (dp.get_type().equals("2")) {
                    // 部门
                    if (CollectionUtils.isNotEmpty(departments)) {
                        for (Department d : departments) {
                            if (dp.getId().equals(d.getId().longValue())) {
                                dp.setName(d.getName());
                                continue labelA;
                            }
                        }
                        iterator.remove();
                    } else {
                        iterator.remove();
                    }
                }
            }
        }
        return data;
    }

    /**
     * 根据组织文件判断当前用户是否是组织空间创建者
     *
     * @param incId
     * @param userId
     * @param orgDocumentId
     * @return
     */
    public Boolean isOrganizedSpaceCreator(Long incId, Long userId, Boolean isAdmin, Long orgDocumentId) {
        if (isAdmin) {
            Document document = this.documentMapper.selectByPrimaryKey(orgDocumentId);
            if (document != null) {
                String idPath = document.getIdPath();
                String organizedSpaceId = idPath.substring(1, idPath.indexOf("/", 1));
                List<String> permissionList = new ArrayList<>();
                permissionList.add(DocumentConstants.DOCUMENT_PERMISSION_SUPER_MANAGE);
                permissionList.add(DocumentConstants.DOCUMENT_PERMISSION_CREATE);
                DocumentPermissionExample documentPermissionExample = new DocumentPermissionExample();
                documentPermissionExample.createCriteria()
                        .andIncIdEqualTo(incId)
                        .andDocumentIdEqualTo(Long.parseLong(organizedSpaceId))
                        .andReceiverTypeEqualTo(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER)
                        .andReceiverIdEqualTo(userId)
                        .andPermissionIn(permissionList);
                List<DocumentPermission> permissionsOfSuperManage = this.documentPermissionMapper.selectByExample(documentPermissionExample);
                if (CollectionUtils.isNotEmpty(permissionsOfSuperManage)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断当前登录用户是否是组织管理员
     *
     * @return
     */
    public boolean isAdmin() {
        boolean isAdmin = false;
        if (UserInfoUtil.getUserInfoVO().getRole() != null && (UserInfoUtil.getUserInfoVO().getRole().getId().equals(RoleConstants.ORGANIZED_ADMIN_ROLE_ID)
                || UserInfoUtil.getUserInfoVO().getRole().getId().equals(RoleConstants.SYSTEM_ADMIN_ROLE_ID))) {
            // 组织管理员
            isAdmin = true;
        }
        return isAdmin;
    }

    private List<Long> getGroupIds(List<Group> groupsList) {
        List<Long> groupIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(groupsList)) {
            for (Group g : groupsList) {
                if (g != null) {
                    groupIds.add(g.getId().longValue());
                }
            }
        }
        return groupIds;
    }

    /**
     * 校验文档操作权限: 个人文件校验文档创建者, 组织文件校验是否拥有读写权限
     *
     * @param incId
     * @param userInfoVO
     * @param documentId
     * @return
     */
    public Boolean checkDocumentPermission(Integer incId, UserInfoVO userInfoVO, long documentId, String type, String basePermission) {
        Boolean flag = true;
        Integer userId = userInfoVO.getUser().getUserId();
        List<Group> groupsList = userInfoVO.getGroupsList();
        List<Long> groupIds = this.getGroupIds(groupsList);
        Integer deptId = userInfoVO.getDepartment().getId();

        Document document = this.documentMapper.selectByPrimaryKey(documentId);
        if (null != document) {
            if (document.getType().equals(DocumentConstants.DOCUMENT_TYPE_PERSONAL)) {
                // 个人文件
                if (!document.getCreateUser().toString().equals(userId.toString())) {
                    flag = false;
                }
            }
            if (document.getType().equals(DocumentConstants.DOCUMENT_TYPE_ORGANIZED)) {
                // 组织文件: 系统管理员和组织空间创建者不用鉴权
                if (!RoleConstants.SYSTEM_ADMIN_ROLE_ID.equals(UserInfoUtil.getUserInfoVO().getRole().getId())
                        && !this.isOrganizedSpaceCreator(Long.parseLong(incId.toString()), Long.parseLong(userId.toString()), this.isAdmin(), documentId)) {
                    String permission = this.documentPermissionService.getPermission(document.getIdPath(), Long.parseLong(userId.toString()), groupIds, Long.parseLong(deptId.toString()));
                    if (null == permission) {
                        flag = false;
                    } else {
                        if (permission.compareTo(basePermission) < 0) {
                            flag = false;
                        }
                    }
                }
            }
        } else {
            if (DocumentConstants.DOCUMENT_ROOT_PARENTID.equals(documentId) && DocumentConstants.DOCUMENT_TYPE_ORGANIZED.equals(type)) {
                if (!isAdmin()) {
                    flag = false;
                }
            }
        }
        return flag;
    }

    /**
     * 生成文件名
     *
     * @param parentId
     * @param documentName
     * @return
     */
    public String generateDirName(String documentType, Long parentId, Long incId, String documentName, String type, Long userId) {
        int i = 1;
        String docName = generateName(i, documentName, documentType);
        while (exists(parentId, incId, docName, type, userId)) {
            i++;
            docName = generateName(i, documentName, documentType);
        }
        return docName;
    }


    private String generateName(int i, String documentName, String documentType) {
        String docName = "";
        if (!"dir".equalsIgnoreCase(documentType) && documentName.contains(".")) {
            int l = documentName.lastIndexOf(".");
            String name = documentName.substring(0, l);
            String suffix = documentName.substring(l + 1, documentName.length());
            docName = name + "(" + i + ")." + suffix;
        } else if (!"dir".equalsIgnoreCase(documentType) && !documentName.contains(".")) {
            docName = documentName + "(" + i + ")";
        } else {
            docName = documentName + i;
        }
        return docName;
    }

    /**
     * 移动复制时判断是否有同名文件
     *
     * @param parentId
     * @param incId
     * @param documentName
     * @param userId
     * @return
     */
    public boolean exists(Long parentId, Long incId, String documentName, String type, Long userId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("incId", incId);
        params.put("parentId", parentId);
        params.put("type", type);
        if (DocumentConstants.DOCUMENT_ROOT_PARENTID.equals(parentId) && type.equals(DocumentConstants.DOCUMENT_TYPE_PERSONAL)) {
            params.put("userId", userId);
        }
        params.put("documentName", documentName);
        Integer result = documentMapper2.exists(params);
        if (result == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 根据parentId模糊查询文件夹下所有匹配文件
     *
     * @param incId
     * @param parentId
     * @param documentName
     * @param pageNum
     * @param pageSize
     * @return
     */
    public List<Document> getAllChildrenByParentIdAndDocumentName(Integer incId, Long parentId, String documentName, Integer pageNum, Integer pageSize) {
        Document parentDoc = this.documentMapper.selectByPrimaryKey(parentId);
        Map<String, Object> params = new HashMap<>();
        params.put("incId", incId);
        params.put("parentIdPath", parentDoc.getIdPath());
        params.put("documentName", documentName);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        return this.documentMapper2.getAllChildrenByParentIdAndDocumentName(params);
    }

    public List<Recycle> getRecycleAddList(List<Long> docIds, long updateUserId, long incId) {

        List<Recycle> list = new ArrayList<>();
        // 先统一加, 不添加某一文件夹下的
        for (Long docId : docIds) {
            Document document = documentMapper.selectByPrimaryKey(docId);
            String documentType = document.getDocumentType();
            Long docVersionId = document.getDocumentVersionId();
            if (!Constants.DOCUMENT_FOLDER_TYPE.equals(documentType) && null != docVersionId) {
                List<Long> versionIdList = documentVersionMapper2.getAllNotDelVersionIdByDocId(docId);
                for (long versionId : versionIdList) {
                    if (versionId != docVersionId) {
                        Recycle recycle = new Recycle();
                        recycle.setIncId(incId);
                        recycle.setCreateUser(updateUserId);
                        recycle.setCreateTime(new Date());
                        recycle.setIsDelete("0");
                        recycle.setDocumentId(docId);
                        recycle.setDocumentVersionId(versionId);
                        recycle.setIsVisiable("0");
                        recycle.setDocumentParentId(document.getParentId());
                        recycle.setDocumentIdPath(document.getIdPath());
                        list.add(recycle);
                    }
                }
            }
            Recycle recycle = new Recycle();
            recycle.setIncId(incId);
            recycle.setCreateUser(updateUserId);
            recycle.setCreateTime(new Date());
            recycle.setIsDelete("0");
            recycle.setDocumentId(docId);
            recycle.setDocumentVersionId(docVersionId);
            recycle.setIsVisiable("1");
            recycle.setDocumentParentId(document.getParentId());
            recycle.setDocumentIdPath(document.getIdPath());
            list.add(recycle);
        }
        return list;
    }
}
