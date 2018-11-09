package com.quarkdata.yunpan.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.amazonaws.services.s3.AmazonS3;
import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.dal.dao.*;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.DepartmentApi;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.GroupApi;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.UsersApi;
import com.quarkdata.yunpan.api.model.common.*;
import com.quarkdata.yunpan.api.model.dataobj.*;
import com.quarkdata.yunpan.api.model.request.Receiver;
import com.quarkdata.yunpan.api.model.request.ShowDocumentType;
import com.quarkdata.yunpan.api.model.vo.*;
import com.quarkdata.yunpan.api.service.ArchivalFileService;
import com.quarkdata.yunpan.api.service.DocumentPermissionService;
import com.quarkdata.yunpan.api.service.FullTextService;
import com.quarkdata.yunpan.api.util.ResultUtil;
import com.quarkdata.yunpan.api.util.S3Utils;
import com.quarkdata.yunpan.api.util.StringUtils;
import com.quarkdata.yunpan.api.util.common.Exception.YCException;
import com.quarkdata.yunpan.api.util.common.config.Global;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class ArchivalFileServiceImpl extends BaseLogServiceImpl implements ArchivalFileService {
    private static Logger logger = LoggerFactory.getLogger(ShareServiceImpl.class);
    @Autowired
    private FullTextService fullTextService;

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private DocumentMapper2 documentMapper2;

    @Autowired
    private DocumentPermissionMapper documentPermissionMapper;

    @Autowired
    private DocumentPermissionMapper2 documentPermissionMapper2;

    @Autowired
    private DocumentVersionMapper2 documentVersionMapper2;

    @Autowired
    private DocumentVersionMapper documentVersionMapper;

    @Autowired
    private DocumentShareMapper2 documentShareMapper2;

    @Autowired
    CommonFileServiceImpl commonFileService;

    @Autowired
    private DocumentPermissionService documentPermissionService;

    @Autowired
    private UsersApi usersApi;

    @Autowired
    private GroupApi groupApi;

    @Autowired
    private DepartmentApi departmentApi;

    @Autowired
    private ExternalUserMapper externalUserMapper;

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

    /**
     * 查看用户归档文件信息 携带标签信息
     *
     * @param documentType
     * @param documentName
     * @param editDay
     * @return
     */
    @Override
    public ResultCode<List<DocumentExtend>> getArchivalFile(Long incId, Long userId, Long parentId, String documentType, String documentName, String editDay, List<Long> userGroupIds, Long deptId, Long roleId) {
        ResultCode<List<DocumentExtend>> rc = new ResultCode<>();
        List<DocumentExtend> archivalFileList = new ArrayList<>();
        //查找父路径
        Document parentDocument = documentMapper.selectByPrimaryKey(parentId);
        String parentPath = null;
        if (parentId == 0) {
            if (UserInfoUtil.isSystemAdmin()) {
                archivalFileList = documentMapper2.getNotTopArvhivalFile(incId, userId, parentId, documentType, documentName, editDay);
            } else {
                if (parentDocument != null && parentDocument.getIdPath() != null) {
                    parentPath = parentDocument.getIdPath();
                }
                //如果有条件查询，则选带条件查询的sql
                if (!StringUtils.isBlank(documentName) || !StringUtils.isBlank(documentType) || !StringUtils.isBlank(editDay)) {
                    if (parentPath == null) {
                        parentPath = "/";
                    }
                    archivalFileList = documentMapper2.getArchivalFileByMap(incId, userId, parentId, documentType, documentName, editDay, userGroupIds, deptId, parentPath);
                } else {
                    archivalFileList = documentMapper2.getArchivalFile(incId, userId, parentId, documentType, documentName, editDay, userGroupIds, deptId);

                }
            }
        } else {
            archivalFileList = documentMapper2.getNotTopArvhivalFile(incId, userId, parentId, documentType, documentName, editDay);
        }
        //验证权限
        if (archivalFileList != null && archivalFileList.size() > 0) {
            for (DocumentExtend document : archivalFileList) {
                String permission = this.documentPermissionService.getPermissionByDocumentId(document.getId(), incId, userId, userGroupIds, deptId, UserInfoUtil.isSystemAdmin());
                document.setPermission(permission);
                //去除空标签
                if (document.getTags().get(0).getId() == null) {
                    document.setTags(new ArrayList<Tag>());
                }
            }

        }
        rc.setData(archivalFileList);
        return rc;
    }


    /**
     * 获取自动归档预览列表
     *
     * @param docIdList      需要筛选的id集合
     * @param lastUpdateTime 最后修改时间
     * @param documentType   文件的类型集合 dir，txt等
     * @param tagIdList      标签集合
     * @return
     */
    @Override
    public ResultCode autoCreateArchivalFile(List<Long> docIdList, String lastUpdateTime, List<String> documentType, List<Long> tagIdList) {
        ResultCode resultCode;
        try {
            //定义预览文档集合
            List<Document> autoDocumentList = new ArrayList<>();
            //定义最终归档集合
            List<Long> autoDocIDList = new ArrayList<>();
            //查找可以归档的列表
            String path;
            if (docIdList != null && docIdList.size() > 0) {
                for (Long i : docIdList) {
                    Document document1 = documentMapper.selectByPrimaryKey(i);
                    path = document1.getIdPath();
                    /*List<Document> allDocList = documentMapper2.selectByLastTimeAndDocumentAndTagID(lastUpdateTime, documentType, tagIdList, path);
                    autoDocumentList.addAll(allDocList);*/
                    List<Long> longs = documentMapper2.selectByLastTimeToListId(lastUpdateTime, documentType, tagIdList, path);
                    autoDocIDList.addAll(longs);
                    List<Document> documentList = documentMapper2.selectByLastTimeAndDocumentAndTagID(lastUpdateTime, documentType, tagIdList, path);
                    if (documentList != null && documentList.size() > 0) {
                        for (Document document :
                                documentList) {
                            if (!i.equals(document.getId())) {
                                if ("dir".equals(document.getDocumentType())) {
                                    List<Long> allChildDocIdByFolderId = documentMapper2.getAllChildDocIdByFolderId(document.getId().toString());
                                    autoDocIDList.addAll(allChildDocIdByFolderId);
                                }
                            }

                        }
                    }
                }
            }
            List<Long> docIdLists = new ArrayList<>();
            //根据父id寻找最顶层id集合
            getautoDocList(docIdList, autoDocIDList, docIdLists);
            if (docIdLists != null && docIdLists.size() > 0) {
                for (Long i :
                        docIdLists) {
                    List<Document> documents = documentMapper2.selectDocumentByPath(i.toString());
                    autoDocumentList.addAll(documents);
                }
            }
            //去重
            HashSet hashSet = new HashSet(autoDocumentList);
            List tempList = new ArrayList(hashSet);
            //返回预览目录和可以最终归档的集合
            ReturnList returnList = new ReturnList();
            returnList.setAllDocList(tempList);
            returnList.setArchivalList(docIdLists);
            resultCode = ResultUtil.success();
            resultCode.setData(returnList);
        } catch (Exception e) {
            logger.info("获取自动归档预览列表失败");
            return ResultUtil.error(1, "获取自动归档预览列表失败");
        }

        return resultCode;
    }

    /**
     * 手动归档获取预览列表
     */
    @Override
    public ResultCode manualProList(List<Long> docIdList) {
        ResultCode resultCode;
        try {
            Document document;
            List<Document> documents;
            List<Document> allDocList = new ArrayList<>();
            if (docIdList != null && docIdList.size() > 0) {
                for (Long i : docIdList) {
                    document = documentMapper.selectByPrimaryKey(i);
                    documents = documentMapper2.selectManualProDocList(document.getIdPath());
                    allDocList.addAll(documents);
                }
            }
            ReturnList returnList = new ReturnList();
            returnList.setAllDocList(allDocList);
            returnList.setArchivalList(docIdList);
            resultCode = ResultUtil.success();
            resultCode.setData(returnList);
        } catch (Exception e) {
            logger.info("获取手动归档预览列表失败");
            return ResultUtil.error(1, "获取手动归档预览列表失败");
        }
        return resultCode;
    }

    /**
     * 归档
     *
     * @param docIdList    归档文档id列表
     * @param isKeepSource 是否保留原文件
     * @return
     */
    @Transactional(readOnly = false)
    @Override
    public ResultCode manualCreateArchivalFile(HttpServletRequest request, List<Receiver> receiverList, String docName, String docDes, Users user, List<Long> docIdList, String isKeepSource) {
        ResultCode resultCode;
        try {
            reName(docIdList);
            //创建一个归档文件夹的操作
            Document document1 = new Document();
            document1.setCreateTime(new Date());
            document1.setUpdateTime(new Date());
            document1.setUpdateUser(user.getUserId().longValue());
            document1.setCreateUser(user.getUserId().longValue());
            document1.setCreateUsername(user.getUserName());
            document1.setUpdateUsername(user.getUserName());
            document1.setDocumentName(docName);
            document1.setDescription(docDes);
            document1.setDocumentType("dir");
            document1.setSize(0L);
            document1.setParentId(0L);
            document1.setType("2");
            document1.setIncId(user.getIncid().longValue());
            documentMapper.insertSelective(document1);
            String dirPath = "/" + document1.getId() + "/";
            document1.setIdPath(dirPath);
            documentMapper.updateByPrimaryKeySelective(document1);
            try {
                //将新建的归档文件夹存入es中 先判断当前用户所在组织索引是否存在
                if (fullTextService.isExistsIndex(Global.getConfig("index.prefixion") + document1.getIncId())) {
                    fullTextService.createDocumentIndex("dir_" + document1.getId().toString(), document1.getId().toString(), document1.getDocumentName(), document1.getType(), document1.getDocumentType(), null);
                }
            } catch (Exception e) {
                this.logger.error("<<<<<< create ES_index error <<<<<<", e);
            }
            //新建归档文件夹添加归档权限
            insertArchivePermission(document1.getId(), receiverList, user);

            //需要归档的文档文档
            Document document;
            //document 的路径
            String idPath;
            //根据路径查出一个文件夹下的所有文档
            List<Long> idPathList;
            //一个文件夹下的任意一个文档
            Document cdocument;
            //一个文件夹下的任意一个文档的路径，document文档的父路径
            String cidPath, dpidPath;
            //归档后文档的路径
            String newPath;
            //判断是否需要保存原文件 0不需要保存
            if ("0".equals(isKeepSource)) {
                if (docIdList != null && docIdList.size() > 0) {
                    for (Long i : docIdList) {
                        document = documentMapper.selectByPrimaryKey(i);
                        idPath = document.getIdPath();
                        if (document.getParentId() != 0) {
                            dpidPath = documentMapper.selectByPrimaryKey(document.getParentId()).getIdPath();
                        } else {
                            dpidPath = "";
                        }
                        idPathList = documentMapper2.selectByPath(idPath);
//                        //删除原有文件所有权限
//                        documentPermissionMapper2.deleteByDocumentIdList(idPathList);
//                        //删除原有文件所有共享信息
//                        documentShareMapper2.deleteByDocumentIdList(idPathList);
//                        //新建归档权限
//                        if (idPathList != null && idPathList.size() > 0) {
//                            for (Long o : idPathList) {
//                                insertArchivePermission(o, receiverList, user);
//                            }
//                        }
                        //shareService.addShare(idPathList, receiverList, user);
                        if ("dir".equals(document.getDocumentType())) {
                            document.setParentId(document1.getId());
                            documentMapper.updateByPrimaryKeySelective(document);
                            if (idPathList != null && idPathList.size() > 0) {
                                for (Long j : idPathList) {
                                    cdocument = documentMapper.selectByPrimaryKey(j);
                                    if (document.getParentId() == 0) {
                                        newPath = dirPath + cdocument.getIdPath();
                                    } else {
                                        cidPath = cdocument.getIdPath();
                                        newPath = cidPath.substring(dpidPath.length());
                                        if (!"/".equals(newPath.substring(0, 1))) {
                                            newPath = dirPath + newPath;
                                        } else {
                                            newPath = dirPath + newPath.substring(1, newPath.length());
                                        }
                                    }
                                    cdocument.setIdPath(newPath);
                                    cdocument.setType("2");
                                    documentMapper.updateByPrimaryKeySelective(cdocument);
                                    try {
                                        //修改索引库相应字段
                                        fullTextService.updateDocumentIndex(Global.getConfig("index.prefixion") + cdocument.getIncId(), "type", "dir_" + cdocument.getId().toString(), cdocument.getType());
                                    } catch (Exception e) {
                                        this.logger.error("<<<<<< update ES_index error <<<<<<", e);
                                    }

//                                    if ((!("dir".equals(cdocument.getDocumentType()))) && cdocument.getSize() != null) {
//                                        document1.setSize(document1.getSize() + cdocument.getSize());
//                                    }
                                }
                            }
                        } else {
                            if (0 == document.getParentId()) {
                                newPath = dirPath + idPath.substring(1, idPath.length());
                                document.setType("2");
                                document.setIdPath(newPath);
                                document.setParentId(document1.getId());
                                documentMapper.updateByPrimaryKeySelective(document);
                                try {
                                    //修改索引库相应字段
                                    fullTextService.updateDocumentIndex(Global.getConfig("index.prefixion") + document.getIncId(), "type", document.getDocumentVersionId().toString(), document.getType());
                                } catch (Exception e) {
                                    this.logger.error("<<<<<< update ES_index error <<<<<<", e);
                                }
//                                if (document.getSize() != null) {
//                                    document1.setSize(document1.getSize() + document.getSize());
//                                }
                            } else {

                                newPath = idPath.substring(dpidPath.length());
                                if (!"/".equals(newPath.substring(0, 1))) {
                                    newPath = dirPath + newPath;
                                } else {
                                    newPath = dirPath + newPath.substring(1, newPath.length());
                                }
                                document.setType("2");
                                document.setIdPath(newPath);
                                document.setParentId(document1.getId());
                                documentMapper.updateByPrimaryKeySelective(document);
                                try {
                                    //修改索引库相应字段
                                    fullTextService.updateDocumentIndex(Global.getConfig("index.prefixion") + document.getIncId(), "type", document.getDocumentVersionId().toString(), document.getType());
                                } catch (Exception e) {
                                    this.logger.error("<<<<<< update ES_index error <<<<<<", e);
                                }
//                                if (document.getSize()!=null){
//                                    document1.setSize(document1.getSize()+document.getSize());
//                                }
                            }
                        }
                    }
                    List<Long> alldir = documentMapper2.getAlldir(document1.getId());
                    for (Long i : alldir) {
                        documentMapper2.sumSize(i);
                    }
                }
            } else {
                updateArchival(receiverList, docIdList, document1, document1, user, document1);
            }

            //统计归档文件大小
            documentMapper.updateByPrimaryKeySelective(document1);
            documentMapper2.sumSize(document1.getId());
            resultCode = ResultUtil.success();
            resultCode.setData(document1);


            // 记录文档操作日志
            this.addDocumentLog(request, document1.getId().toString(), ActionType.CREATE_FOLDER, document1.getDocumentName(), document1.getCreateTime());

        } catch (NumberFormatException e) {
            logger.error("Manual new filing failure ：" + e);
            return ResultUtil.error(Messages.API_ADD_ManualARCHIVAL_FAILED_CODE, Messages.API_ADD_ManualARCHIVAL_FAILED_MSG);
        }

        return resultCode;
    }

    /**
     * 验证同组织下的同名归档文件
     *
     * @param incId       组织id
     * @param archiveName 需要验证的文件名
     * @return
     */
    @Override
    public ResultCode checkArchiveName(Long incId, String archiveName) {
        ResultCode<Boolean> resultCode = new ResultCode<>();
        Integer integer = documentMapper2.checkArchiveName(incId, archiveName);
        if (integer > 0) {
            resultCode.setData(false);
        } else {
            resultCode.setData(true);
        }
        return resultCode;
    }

    /**
     * @return
     */
    @Override
    public ResultCode selectAllDocumentType() {
        ResultCode resultCode;
        try {
            List<String> typeList = documentMapper2.selectAllDocumentType();
            List<ShowDocumentType> showDocumentTypes = assembleResult(typeList);
            resultCode = ResultUtil.success(showDocumentTypes);
            logger.info("查询所有文件类型成功");
        } catch (Exception e) {
            resultCode = ResultUtil.error(null, "查询所有文件类型失败");
            logger.info("查询所有文件类型失败");
            e.printStackTrace();
        }
        return resultCode;
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void updateArchiveFile(Long id, String docName, String des, HttpServletRequest request, List<Long> userGroupIds, Long deptId) {
        Long incId = UserInfoUtil.getIncId();
        Long userId = UserInfoUtil.getUserId();
        String userName = UserInfoUtil.getUserInfoVO().getUser().getUserName();
        Document doc = documentMapper.selectByPrimaryKey(id);
        //判断是否重名
        if (!doc.getDocumentName().equalsIgnoreCase(docName)) {
            if (exists(doc.getDocumentType(), doc.getParentId(), incId, docName)) {
                throw new YCException(Messages.API_DOCUMENT_EXITS_MSG, Messages.API_DOCUMENT_EXITS_CODE);
            }
            Document document = new Document();
            document.setId(id);
            document.setDocumentName(docName);
            document.setDescription(des);
            document.setUpdateTime(new Date());
            document.setUpdateUser(userId);
            document.setUpdateUsername(userName);
            documentMapper.updateByPrimaryKeySelective(document);
            //修改索引库相应字段
            fullTextService.updateDocumentIndex(Global.getConfig("index.prefixion") + incId, "title", "dir_" + doc.getId().toString(), docName);
            // 记录操作日志
            this.addDocumentLog(request, id.toString(), ActionType.RENAME, doc.getDocumentName() + "重命名为" + docName + "," + doc.getDescription() + "修改为" + des, doc.getUpdateTime());
        } else {
            // 单独更新文档描述即可
            doc.setDescription(des);
            doc.setUpdateTime(new Date());
            doc.setUpdateUser(userId);
            doc.setUpdateUsername(userName);
            this.documentMapper.updateByPrimaryKeySelective(doc);
        }
    }

    /**
     * 设置归档文件权限
     *
     * @param request
     * @param documentId
     * @param permissions
     * @param permissionsOfGenerateAccounts
     * @param incId
     * @param isOrganizedAdmin
     * @param userId
     * @param userGroupIds
     * @param deptId
     * @return
     */
    @Override
    public ResultCode<String> setPermissionOfArchivalFile(HttpServletRequest request, Long documentId, List<DocumentPrivilegeVO> permissions, List<DocumentPrivilegeVOfGenerateAccounts> permissionsOfGenerateAccounts, Long incId, boolean isOrganizedAdmin, Long userId, List<Long> userGroupIds, Long deptId) {
        ResultCode<String> rc = new ResultCode<String>();
        // 校验前端传递的权限是否合法: 只能是预览者,下载者, 或编辑者
        if (CollectionUtils.isNotEmpty(permissions)) {
            for (DocumentPrivilegeVO receiver : permissions) {
                String permission = receiver.getPermission();
                if (!DocumentPermissionConstants.PermissionRole.PREVIEW.equals(permission)
                        && !DocumentPermissionConstants.PermissionRole.DOWNLOAD.equals(permission)
                        && !DocumentPermissionConstants.PermissionRole.EDIT.equals(permission)) {
                    throw new YCException(Messages.Archive.PERMISSION_ILLEGAL_MSG, Messages.Archive.PERMISSION_ILLEGAL_CODE);
                }
            }
        }
        // 设置文档权限，先将之前的权限清除，再赋予新的权限
        DocumentPermissionExample example = new DocumentPermissionExample();
        example.createCriteria().andDocumentIdEqualTo(documentId).andPermissionNotEqualTo(DocumentPermissionConstants.PermissionRole.ADMIN_OR_CREATOR);

        logger.info("delete DocumentPermission,condition is --> documentId : {}", documentId);

        documentPermissionMapper.deleteByExample(example);

        for (DocumentPrivilegeVO dp : permissions) {

            DocumentPermission record = new DocumentPermission();
            record.setIncId(incId);
            record.setDocumentId(documentId);
            BeanUtils.copyProperties(dp, record);

            logger.info("insert into DocumentPermission,obj is : {}",
                    JSON.toJSONString(record));

            documentPermissionMapper2.insert(record);
        }

        // 记录操作日志
        Document document = this.documentMapper.selectByPrimaryKey(documentId);
        this.addDocumentLog(request, documentId.toString(), ActionType.SET_DOCUMENT_PRIVILEGE, document.getDocumentName(), new Date());

        return rc;
    }

    /**
     * 查询归档文件权限
     *
     * @param docId
     * @param incId
     * @return
     */
    @Override
    public ResultCode<List<DocumentPermissionVO>> getArchivalDocumentPermissions(Long docId, Long incId) {
        return this.commonFileService.getDocumentPermissionsByDocId(docId, incId);
    }

    /**
     * 组装前端需要的文件类型及是否可显示的参数
     *
     * @param typeList
     */
    private List<ShowDocumentType> assembleResult(List<String> typeList) {
        ArrayList<ShowDocumentType> showDocumentTypes = new ArrayList<>();
        showDocumentTypes.add(new ShowDocumentType("全部", "all", true));
        if (!"null".equals(typeList) && !typeList.isEmpty()) {
            for (String s :
                    typeList) {
                if ("dir".equals(s)) {
                    showDocumentTypes.add(new ShowDocumentType("文件夹", "dir", false));
                } else {
                    showDocumentTypes.add(new ShowDocumentType(s, s, false));
                }
            }
        }
        return showDocumentTypes;
    }


    /**
     * 递归复制文件
     *
     * @param receiverList 接受者集合
     * @param docIdList    传入归档集合
     * @param pdocument    每一层的父文档
     * @param newDocument  新复制的文档
     * @param user         用户
     * @param allDocument  新建的文档文件夹
     */
    public void updateArchival(List<Receiver> receiverList, List<Long> docIdList, Document pdocument, Document newDocument, Users user, Document allDocument) {
        if (docIdList != null && docIdList.size() > 0) {
            for (Long i : docIdList) {
                Document document = documentMapper.selectByPrimaryKey(i);
                if ("dir".equals(document.getDocumentType())) {
                    Document document2 = new Document();
                    document2.setCreateTime(new Date());
                    document2.setCreateUser(user.getUserId().longValue());
                    document2.setCreateUsername(user.getUserName());
                    document2.setUpdateUser(user.getUserId().longValue());
                    document2.setUpdateUsername(user.getUserName());
                    document2.setUpdateTime(new Date());
                    document2.setDocumentName(document.getDocumentName());
                    document2.setDocumentType(document.getDocumentType());
                    document2.setType("2");
                    //统计出原文件大小并赋予新文件
                    document2.setSize(documentMapper2.getSumSize(document.getId()));
                    document2.setParentId(newDocument.getId());
                    document2.setIncId(user.getIncid().longValue());
                    documentMapper.insertSelective(document2);
                    document2.setIdPath(newDocument.getIdPath() + document2.getId() + "/");
                    documentMapper.updateByPrimaryKeySelective(document2);
                    docIdList = documentMapper2.getNotDelDocParentIdInDocument(i);
                    insertArchivePermission(document2.getId(), receiverList, user);
                    try {
                        //复制文件索引 先判断当前用户所在组织索引是否建立
                        if (fullTextService.isExistsIndex(Global.getConfig("index.prefixion") + user.getIncid())) {
                            //type代表文件类型变为归档文件
                            fullTextService.copyEsFile(Global.getConfig("index.prefixion") + user.getIncid(), "dir_" + document.getId().toString(), "dir_" + document2.getId().toString(), document2.getId().toString(), document2.getType());
                        }
                    } catch (Exception e) {
                        this.logger.error("<<<<<< create ES_index error <<<<<<", e);
                    }

                    updateArchival(receiverList, docIdList, document, document2, user, allDocument);
                } else {
                    //复制对象
                    AmazonS3 connection = commonFileService.getCephConnect(Long.valueOf(user.getIncid()));
                    String destinationBucketName = commonFileService.getCephBucket(Long.valueOf(user.getIncid()), connection);
                    String destinationKey = UUID.randomUUID().toString();
                    DocumentVersion documentVersion = documentVersionMapper2.selectDocumentVersionById(i);
                    String bucketName = documentVersion.getCephBucket();
                    String sourceKey = documentVersion.getCephBucketKey();
                    S3Utils.copyObject(bucketName, sourceKey, destinationBucketName, destinationKey, connection);

                    Document document2 = new Document();
                    document2.setCreateTime(new Date());
                    document2.setCreateUser(user.getUserId().longValue());
                    document2.setCreateUsername(user.getUserName());
                    document2.setUpdateTime(new Date());
                    document2.setUpdateUser(user.getUserId().longValue());
                    document2.setUpdateUsername(user.getUserName());
                    document2.setDocumentName(document.getDocumentName());
                    document2.setDocumentType(document.getDocumentType());
                    document2.setDescription(document.getDescription());
                    document2.setSize(document.getSize());
                    document2.setType("2");
                    document2.setParentId(newDocument.getId());
                    document2.setIncId(user.getIncid().longValue());
                    documentMapper.insertSelective(document2);
                    document2.setIdPath(newDocument.getIdPath() + document2.getId() + "/");
                    documentMapper.updateByPrimaryKeySelective(document2);
                    insertArchivePermission(document2.getId(), receiverList, user);
//                    if (document.getSize() != null && allDocument.getSize() != null) {
//                        allDocument.setSize(allDocument.getSize() + document.getSize());
//                    }
//                    if (document.getSize() != null && newDocument.getSize() != null) {
//                        newDocument.setSize(newDocument.getSize() + document.getSize());
//                        documentMapper.updateByPrimaryKeySelective(newDocument);
//                    }
                    // 插入版本记录表
                    DocumentVersion documentVersion1 = new DocumentVersion();
                    documentVersion1.setIncId(user.getIncid().longValue());
                    documentVersion1.setDocumentId(document2.getId());
                    documentVersion1.setVersion(1);// 设置起始版本
                    documentVersion1
                            .setOperateType(DocumentConstants.DOCUMENT_OPERATE_TYPE_UPLOAD);
                    documentVersion1.setCreateTime(new Date());
                    documentVersion1.setCreateUser(Long.valueOf(user.getUserId()));
                    documentVersion1.setUpdateTime(new Date());
                    documentVersion1.setUpdateUser(Long.valueOf(user.getUserId()));
                    documentVersion1.setMd5(documentVersion.getMd5());
                    documentVersion1.setHash(documentVersion.getHash());
                    documentVersion1.setSize(document2.getSize());
                    documentVersion1.setUpdateUsername(user.getUserName());
                    documentVersion1.setCreateUsername(user.getUserName());
                    documentVersion1.setCephBucket(destinationBucketName);
                    documentVersion1.setCephBucketKey(destinationKey);
                    documentVersionMapper.insertSelective(documentVersion1);

                    //补全document2
                    document2.setDocumentVersionId(documentVersion1.getId());
                    documentMapper.updateByPrimaryKeySelective(document2);
                    try {
                        //复制文件索引 先判断当前用户所在组织索引是否建立
                        if (fullTextService.isExistsIndex(Global.getConfig("index.prefixion") + user.getIncid())) {
                            //type代表文件类型变为归档文件
                            fullTextService.copyEsFile(Global.getConfig("index.prefixion") + user.getIncid(), document.getDocumentVersionId().toString(), document2.getDocumentVersionId().toString(), document2.getId().toString(), document2.getType());
                        }
                    } catch (Exception e) {
                        this.logger.error("<<<<<< create ES_index error <<<<<<", e);
                    }
                }
            }
        }
    }

    /**
     * 插入归档权限
     *
     * @param id           docid
     * @param receiverList 接收者
     * @param users        用户
     * @return resultCode
     */
    public ResultCode insertArchivePermission(Long id, List<Receiver> receiverList, Users users) {
        logger.info("插入归档权限");
        Long incId = UserInfoUtil.getIncId();
        Long userId = UserInfoUtil.getUserId();
        // 校验前端传递的权限是否合法: 只能是预览者,下载者, 或编辑者
        if (CollectionUtils.isNotEmpty(receiverList)) {
            for (Receiver receiver : receiverList) {
                String permission = receiver.getPermission();
                if (!DocumentPermissionConstants.PermissionRole.PREVIEW.equals(permission)
                        && !DocumentPermissionConstants.PermissionRole.DOWNLOAD.equals(permission)
                        && !DocumentPermissionConstants.PermissionRole.EDIT.equals(permission)) {
                    throw new YCException(Messages.Archive.PERMISSION_ILLEGAL_MSG + ": " + permission, Messages.Archive.PERMISSION_ILLEGAL_CODE);
                }
            }
        }
        // 插入创建归档者权限
        if (receiverList == null) {
            receiverList = new ArrayList<>();
        }
        Receiver receiver_creator = new Receiver();
        receiver_creator.setRecId(userId.toString());
        receiver_creator.setRecType("user");
        receiver_creator.setPermission(DocumentPermissionConstants.PermissionRole.ADMIN_OR_CREATOR);
        receiverList.add(receiver_creator);
        try {
            for (Receiver r : receiverList) {
                DocumentPermission documentPermission = new DocumentPermission();
                documentPermission.setDocumentId(id);
                documentPermission.setIncId(incId);
                documentPermission.setPermission(r.getPermission());
                documentPermission.setReceiverId(Long.valueOf(r.getRecId()));
                if ("user".equals(r.getRecType())) {
                    documentPermission.setReceiverType("0");
                } else if ("group".equals(r.getRecType())) {
                    documentPermission.setReceiverType("1");
                } else if ("dept".equals(r.getRecType())) {
                    documentPermission.setReceiverType("2");
                }
                documentPermissionMapper2.insert(documentPermission);
            }
            logger.info("插入归档权限成功");
        } catch (NumberFormatException e) {
            logger.error("insert share record failed：" + e);
            return ResultUtil.error(Messages.API_ADD_SHARE_FAILED_CODE, Messages.API_ADD_SHARE_FAILED_MSG);
        }
        return ResultUtil.success();
    }

    /**
     * 自动归档选择最上层idlist
     *
     * @param docIdList
     * @param autoDocIDList
     * @param docIdLists
     */
    public void getautoDocList(List<Long> docIdList, List<Long> autoDocIDList, List<Long> docIdLists) {
        if (docIdList != null && docIdList.size() > 0) {
            for (Long i : docIdList) {
                Document document = documentMapper.selectByPrimaryKey(i);
                if ("dir".equals(document.getDocumentType())) {
                    List<Long> allChildList = documentMapper2.getAllNotDelByFolderId(i.toString());
                    if (autoDocIDList.containsAll(allChildList)) {
                        docIdLists.add(i);
                    } else {
                        //子一级列表
                        List<Long> docByParentList = documentMapper2.getNotDelDocParentIdInDocument(i);
                        getautoDocList(docByParentList, autoDocIDList, docIdLists);
                    }
                } else {
                    if (autoDocIDList.contains(i)) {
                        docIdLists.add(i);
                    }
                }
            }
        }
    }

    public void reName(List<Long> docIdList) {
        if (docIdList != null && docIdList.size() > 0) {
            Document document;
            HashMap<String, Integer> map = new HashMap<>();
            for (Long i : docIdList) {
                document = documentMapper.selectByPrimaryKey(i);
                if (map.get(document.getDocumentName()) == null) {
                    map.put(document.getDocumentName(), 0);
                } else {
                    map.put(document.getDocumentName(), map.get(document.getDocumentName()) + 1);
                    document.setDocumentName(document.getDocumentName() + map.get(document.getDocumentName()));
                    documentMapper.updateByPrimaryKeySelective(document);
                }
            }
        }
    }

    //判断重名
    public boolean exists(String documentType, Long parentId, Long incId, String documentName) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("documentType", documentType);
        if (null != parentId) {
            params.put("parentId", parentId);
        }
        if (null != incId) {
            params.put("incId", incId);
        }
        params.put("documentName", documentName);
        Integer result = documentMapper2.exists(params);
        if (result == 0) {
            return false;
        } else {
            return true;
        }
    }


}
