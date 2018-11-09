package com.quarkdata.yunpan.api.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.dal.dao.*;
import com.quarkdata.yunpan.api.model.common.*;
import com.quarkdata.yunpan.api.model.dataobj.*;
import com.quarkdata.yunpan.api.model.vo.DocumentRecentBrowseVO;
import com.quarkdata.yunpan.api.service.DocumentPermissionService;
import com.quarkdata.yunpan.api.service.DocumentService;
import com.quarkdata.yunpan.api.service.FullTextService;
import com.quarkdata.yunpan.api.util.DownloadUtil;
import com.quarkdata.yunpan.api.util.S3Utils;
import com.quarkdata.yunpan.api.util.common.Exception.YCAuthorizationException;
import com.quarkdata.yunpan.api.util.common.Exception.YCException;
import com.quarkdata.yunpan.api.util.common.config.Global;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by xiexl on 2018/1/11.
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class DocumentServiceImpl extends BaseLogServiceImpl implements DocumentService {


    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private DocumentMapper2 documentMapper2;

    @Autowired
    private DocumentVersionMapper documentVersionMapper;

    @Autowired
    private DocumentVersionMapper2 documentVersionMapper2;

    @Autowired
    private IncConfigMapper incConfigMapper;

    @Autowired
    CommonFileServiceImpl commonFileService;

    @Autowired
    private DocumentPermissionService documentPermissionService;

    @Autowired
    private DocumentPermissionMapper documentPermissionMapper;

    @Autowired
    private DocumentPermissionMapper2 documentPermissionMapper2;

    @Autowired
    private DocumentShareMapper2 documentShareMapper2;

    @Autowired
    private FullTextService fullTextService;

    private LogMapper logMapper;

    private LogDocumentRelMapper logDocumentRelMapper;

    @Value("${RECENT_BROWSE_ACTION_TYPE}")
    private String RECENT_BROWSE_ACTION_TYPE;

    @Value("${RECENT_BROWSE_TIME}")
    private String RECENT_BROWSE_TIME;

    @Value("${RECENT_BROWSE_TIME_UNIT}")
    private String RECENT_BROWSE_TIME_UNIT;

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

    static Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Override
    public ResultCode<String> rename(HttpServletRequest req, Long id, String documentName, Long userId, List<Long> userGroupIds, Long deptId) {
        ResultCode<String> result = new ResultCode<>();
        Long incId = UserInfoUtil.getIncorporation().getId().longValue();
        Document t_doc = documentMapper.selectByPrimaryKey(id);
        if (t_doc != null) {
            // 鉴权: 读写权限
            /*Boolean permission_flag = this.commonFileService.checkDocumentPermission(UserInfoUtil.getIncorporation().getId(), UserInfoUtil.getUserInfoVO(), id, null, DocumentConstants.DOCUMENT_PERMISSION_READANDWRITE);
            if(!permission_flag) {
                result.setCode(Messages.API_AUTHEXCEPTION_CODE);
                result.setMsg(Messages.API_AUTHEXCEPTION_MSG);
                return result;
            }*/

            if(!t_doc.getDocumentName().equals(documentName))
            {
                if (exists(t_doc.getType(), t_doc.getParentId(), incId, documentName, userId)) {
                    result.setCode(Messages.API_DOCUMENT_EXITS_CODE);
                    result.setMsg(Messages.API_DOCUMENT_EXITS_MSG);
                    return result;
                }
                UserInfoVO userInfoVO = UserInfoUtil.getUserInfoVO();
                Users user = userInfoVO.getUser();
                Document doc = new Document();
                doc.setId(id);
                // 判断是否缺失后缀
                if(!DocumentConstants.DOCUMENT_DOCUMENT_TYPE_DIR.equals(t_doc.getDocumentType()) && StringUtils.isNotBlank(t_doc.getDocumentType()) && !documentName.contains(".")) {
                    documentName = documentName + "." + t_doc.getDocumentType();
                }
                doc.setDocumentName(documentName);
                doc.setUpdateTime(new Date());
                doc.setUpdateUser(user.getUserId().longValue());
                doc.setUpdateUsername(user.getUserName());
                documentMapper.updateByPrimaryKeySelective(doc);
                fullTextService.updateDocumentIndex(Global.getConfig("index.prefixion")+user.getIncid(),"title","dir_" + doc.getId().toString(),doc.getDocumentName());

                // 记录操作日志
                this.addDocumentLog(req, id.toString(), ActionType.RENAME, t_doc.getDocumentName() + "重命名为" + documentName, doc.getUpdateTime());
            }
        } else {
            result.setCode(Messages.API_OBJECT_NOTFOUND_CODE);
            result.setMsg(Messages.API_OBJECT_NOTFOUND_MSG);
            return result;
        }
        return result;
    }

    @Override
    public ResultCode<String> move(HttpServletRequest req, String sourceDocumentIds, Long targetDocumentId, String  targetDocumentType) {
        ResultCode<String> result = new ResultCode<>();
        // 鉴权
        Long incId = UserInfoUtil.getIncId();
        Long userId = UserInfoUtil.getUserId();
        List<Long> groupIds = UserInfoUtil.getGroupIds();
        Long deptId = UserInfoUtil.getDeptId();
        for(String sourceDocumentId: sourceDocumentIds.split(",")) {
            Boolean hasPermission = this.documentPermissionService.hasPermission(incId, userId, groupIds, deptId, Long.parseLong(sourceDocumentId), DocumentPermissionConstants.PermissionIndex.MOVE);
            if(!hasPermission) {
                throw new YCAuthorizationException();
            }
        }
        Document tarDoc = documentMapper.selectByPrimaryKey(targetDocumentId);
        if(tarDoc != null) {
            Boolean hasPermission = this.documentPermissionService.hasPermission(incId, userId, groupIds, deptId, targetDocumentId, DocumentPermissionConstants.PermissionIndex.UPLOAD);
            if(!hasPermission) {
                throw new YCAuthorizationException();
            }
        } else {
            if(DocumentConstants.DOCUMENT_TYPE_ORGANIZED.equals(targetDocumentType)) {
                if(!UserInfoUtil.isAdmin()) {
                    throw new YCAuthorizationException();
                }
            }
        }

        if (sourceDocumentIds != null) {
            Document t_doc = documentMapper.selectByPrimaryKey(targetDocumentId);
            if(t_doc == null && targetDocumentId == 0) {
                String[] docids = sourceDocumentIds.split(",");
                UserInfoVO userInfoVO = UserInfoUtil.getUserInfoVO();
                Users user = userInfoVO.getUser();

                for (int i = 0; i < docids.length; i++) {
                    Document s_doc = documentMapper.selectByPrimaryKey(Long.parseLong(docids[i]));
                    if (s_doc.getParentId() != targetDocumentId || !s_doc.getType().equals(targetDocumentType)) {

                        String docName = s_doc.getDocumentName();
                        if (exists(targetDocumentType, targetDocumentId, incId, s_doc.getDocumentName(), Long.parseLong(UserInfoUtil.getUserInfoVO().getUser().getUserId().toString()))) {
                            docName = this.commonFileService.generateDirName(s_doc.getDocumentType(), targetDocumentId, incId, s_doc.getDocumentName(), targetDocumentType, Long.parseLong(UserInfoUtil.getUserInfoVO().getUser().getUserId().toString()));
                        }
                        Document doc = new Document();
                        doc.setId(Long.parseLong(docids[i]));
                        doc.setDocumentName(docName);
                        doc.setParentId(targetDocumentId);
                        doc.setIdPath("/" + docids[i] + "/");
                        doc.setUpdateTime(new Date());
                        doc.setUpdateUser(user.getUserId().longValue());
                        doc.setUpdateUsername(user.getUserName());
                        doc.setCreateUser(user.getUserId().longValue());
                        doc.setCreateUsername(user.getUserName());
                        doc.setCreateTime(new Date());

                        doc.setType(targetDocumentType);

                        documentMapper.updateByPrimaryKeySelective(doc);

                        Document new_s_doc = documentMapper.selectByPrimaryKey(Long.parseLong(docids[i]));
                        // 移动的同时递归修改文件的 type
                        if(s_doc.getDocumentType().equals("dir")) {
                            this.recursionMove(incId, new_s_doc, targetDocumentType, user);
                        }

                        // 如果源文件是组织文件,删除原有权限记录
                        if(s_doc.getType().equals("0")) {
                            DocumentPermissionExample documentPermissionExample = new DocumentPermissionExample();
                            DocumentPermissionExample.Criteria documentPermissionExampleCriteria = documentPermissionExample.createCriteria();
                            documentPermissionExampleCriteria.andDocumentIdEqualTo(s_doc.getId());
                            this.documentPermissionMapper.deleteByExample(documentPermissionExample);
                        }
                        // 如果目的端是组织文件根目录，添加权限
                        if(targetDocumentType.equals("0")){
                            DocumentPermission dPermission = new DocumentPermission();
                            dPermission.setDocumentId(doc.getId());
                            dPermission.setIncId(incId);
                            dPermission.setReceiverId(user.getUserId().longValue());
                            dPermission.setReceiverType(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER);
                            // 给组织空间创建者赋予超级管理权限,可以看见组织空间下所有文件
                            dPermission.setPermission(DocumentConstants.DOCUMENT_PERMISSION_SUPER_MANAGE);
                            documentPermissionMapper2.insert(dPermission);
                            dPermission.setPermission(DocumentConstants.DOCUMENT_PERMISSION_CREATE);
                            documentPermissionMapper2.insert(dPermission);
                        }

                        // 记录操作日志
                        this.addDocumentLog(req, s_doc.getId().toString(), ActionType.MOVE, s_doc.getDocumentName() + "移动到个人文件根目录", new_s_doc.getUpdateTime());

                    }
                }

            }

            if (t_doc != null) {
                String[] docids = sourceDocumentIds.split(",");
                UserInfoVO userInfoVO = UserInfoUtil.getUserInfoVO();
                Users user = userInfoVO.getUser();

                // 获取目标文件夹权限
                DocumentPermissionExample example = new DocumentPermissionExample();
                DocumentPermissionExample.Criteria criteria = example.createCriteria();
                criteria.andIncIdEqualTo(incId);
                criteria.andDocumentIdEqualTo(t_doc.getId());
                List<DocumentPermission> documentPermissions = this.documentPermissionMapper.selectByExample(example);

                for (int i = 0; i < docids.length; i++) {
                    Document s_doc = documentMapper.selectByPrimaryKey(Long.parseLong(docids[i]));
                    if (s_doc.getParentId() != targetDocumentId) {

                        String docName = s_doc.getDocumentName();
                        if (exists(targetDocumentType, targetDocumentId, incId, s_doc.getDocumentName(), null)) {
                            docName = this.commonFileService.generateDirName(s_doc.getDocumentType(), targetDocumentId, incId, s_doc.getDocumentName(), targetDocumentType, null);
                        }
                        Document doc = new Document();
                        doc.setId(Long.parseLong(docids[i]));
                        doc.setDocumentName(docName);
                        doc.setParentId(targetDocumentId);
                        String doc_idPath = "";
                        if(t_doc.getIdPath().endsWith("/")) {
                            doc_idPath = t_doc.getIdPath() + docids[i] + "/";
                        } else {
                            doc_idPath = t_doc.getIdPath() + "/" + docids[i] + "/";
                        }
                        doc.setIdPath(doc_idPath);
                        doc.setUpdateTime(new Date());
                        doc.setUpdateUser(user.getUserId().longValue());
                        doc.setUpdateUsername(user.getUserName());
                        doc.setCreateUser(user.getUserId().longValue());
                        doc.setCreateUsername(user.getUserName());
                        doc.setCreateTime(new Date());

                        doc.setType(t_doc.getType());

                        documentMapper.updateByPrimaryKeySelective(doc);

                        // 移动的同时递归修改文件的 type
                        Document new_s_doc = documentMapper.selectByPrimaryKey(Long.parseLong(docids[i]));
                        if(s_doc.getDocumentType().equals("dir")) {
                            this.recursionMove(incId, new_s_doc, t_doc.getType(), user);
                        }

                        // 如果源文件是组织文件,删除原有权限记录
                        if(s_doc.getType().equals("0")) {
                            DocumentPermissionExample documentPermissionExample = new DocumentPermissionExample();
                            DocumentPermissionExample.Criteria documentPermissionExampleCriteria = documentPermissionExample.createCriteria();
                            documentPermissionExampleCriteria.andDocumentIdEqualTo(s_doc.getId());
                            this.documentPermissionMapper.deleteByExample(documentPermissionExample);
                        }
                        // 判断目标文件夹是否是组织文件夹,如果是,继承父级权限
                        if(t_doc.getType().equals("0")) {
                            // 插入新的权限记录
                            for (int j = 0; j < documentPermissions.size(); j++) {
                                DocumentPermission documentPermission = new DocumentPermission();
                                documentPermission.setIncId(incId);
                                documentPermission.setDocumentId(s_doc.getId());
                                documentPermission.setPermission(documentPermissions.get(j).getPermission());
                                documentPermission.setReceiverId(documentPermissions.get(j).getReceiverId());
                                documentPermission.setReceiverType(documentPermissions.get(j).getReceiverType());
                                documentPermission.setShareId(documentPermissions.get(j).getShareId());
                                this.documentPermissionMapper2.insert(documentPermission);
                            }
                        }

                        // 记录操作日志
                        this.addDocumentLog(req, s_doc.getId().toString(), ActionType.MOVE, s_doc.getDocumentName() + "移动到" + t_doc.getDocumentName(), new_s_doc.getUpdateTime());

                    }
                }
            } else if(t_doc == null && targetDocumentId != 0) {
                throw new YCException(Messages.API_OBJECT_NOTFOUND_MSG, Messages.API_OBJECT_NOTFOUND_CODE);
            }

        }
        return result;
    }

    private void recursionMove(Long incId, Document sourceDoc, String type, Users user) {
        // 获取源文件的子文件
        DocumentExample example = new DocumentExample();
        DocumentExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(sourceDoc.getId());
        criteria.andIncIdEqualTo(incId);
        List<Document> documents = this.documentMapper.selectByExample(example);
        for(Document doc : documents) {
            // 修改子文件type
            Document newDoc = new Document();
            newDoc.setId(doc.getId());
            String doc_idPath = "";
            if(sourceDoc.getIdPath().endsWith("/")) {
                doc_idPath = sourceDoc.getIdPath() + doc.getId() + "/";
            } else {
                doc_idPath = sourceDoc.getIdPath() + "/" + doc.getId() + "/";
            }
            newDoc.setIdPath(doc_idPath);
            newDoc.setUpdateUser(user.getUserId().longValue());
            newDoc.setUpdateUsername(user.getUserName());
            newDoc.setUpdateTime(new Date());
            newDoc.setCreateUser(user.getUserId().longValue());
            newDoc.setCreateUsername(user.getUserName());
            newDoc.setCreateTime(newDoc.getUpdateTime());
            newDoc.setType(type);
            this.documentMapper.updateByPrimaryKeySelective(newDoc);
            if(doc.getDocumentType().equals("dir")) {
                this.recursionMove(incId, doc, type, user);
            }
        }
    }

    @Override
    public ResultCode<String> copy(HttpServletRequest req, String sourceDocumentIds, Long targetDocumentId, String targetDocumentType) {
        ResultCode<String> result = new ResultCode<>();
        // 鉴权
        Long incId = UserInfoUtil.getIncId();
        Long userId = UserInfoUtil.getUserId();
        String userName = UserInfoUtil.getUserName();
        List<Long> groupIds = UserInfoUtil.getGroupIds();
        Long deptId = UserInfoUtil.getDeptId();
        for(String sourceDocumentId: sourceDocumentIds.split(",")) {
            Boolean hasPermission = this.documentPermissionService.hasPermission(incId, userId, groupIds, deptId, Long.parseLong(sourceDocumentId), DocumentPermissionConstants.PermissionIndex.COPY);
            if(!hasPermission) {
                throw new YCAuthorizationException();
            }
        }

        Document tarDoc = documentMapper.selectByPrimaryKey(targetDocumentId);
        if(tarDoc != null) {
            Boolean hasPermission = this.documentPermissionService.hasPermission(incId, userId, groupIds, deptId, targetDocumentId, DocumentPermissionConstants.PermissionIndex.UPLOAD);
            if(!hasPermission) {
                throw new YCAuthorizationException();
            }
        } else {
            if(DocumentConstants.DOCUMENT_TYPE_ORGANIZED.equals(targetDocumentType)) {
                if(!UserInfoUtil.isAdmin()) {
                    throw new YCAuthorizationException();
                }
            }
        }

        if (sourceDocumentIds != null) {
            Document t_doc = documentMapper.selectByPrimaryKey(targetDocumentId);

            if(t_doc == null && targetDocumentId == 0) {
                // 复制到个人文件根目录
                String[] docids = sourceDocumentIds.split(",");

                AmazonS3 connection = commonFileService.getCephConnect(incId);
                if (connection == null) {
                    throw new YCException(Messages.API_FILE_UPLOAD_PARAM_MSG, Messages.API_FILE_UPLOAD_PARAM_CODE);
                }

                for (int i = 0; i < docids.length; i++) {
                    Document new_t_doc = new Document();
                    new_t_doc.setId(0L);
                    new_t_doc.setType(targetDocumentType);
                    new_t_doc.setIdPath("/");
                    recursionCopy(req, Long.parseLong(docids[i]), new_t_doc, incId, userId, userName, targetDocumentType, connection);
                    logger.info("copy success!");
                }
            }

            if (t_doc != null) {
                String[] docids = sourceDocumentIds.split(",");

                AmazonS3 connection = commonFileService.getCephConnect(incId);
                if (connection == null) {
                    throw new YCException(Messages.API_FILE_UPLOAD_PARAM_MSG, Messages.API_FILE_UPLOAD_PARAM_CODE);
                }
                UserInfoVO userInfoVO = UserInfoUtil.getUserInfoVO();
                Users user = userInfoVO.getUser();
                for (int i = 0; i < docids.length; i++) {
                    recursionCopy(req, Long.parseLong(docids[i]), t_doc, incId, userId, userName, targetDocumentType, connection);
                    logger.info("copy success!");
                }
            } else if(t_doc == null && targetDocumentId != 0) {
                throw new YCException(Messages.API_OBJECT_NOTFOUND_MSG, Messages.API_OBJECT_NOTFOUND_CODE);
            }


        }
        return result;
    }


    /**
     * 递归拷贝文件
     *
     * @param req
     * @param sourceId
     * @param t_doc
     * @param incId
     * @param userId
     * @param userName
     * @param connection
     */
    private void recursionCopy(HttpServletRequest req, Long sourceId, Document t_doc, Long incId, Long userId, String userName, String type, AmazonS3 connection) {
        Document doc = documentMapper.selectByPrimaryKey(sourceId);

        // 记录操作日志
        // 源文件
        if(t_doc.getId() == 0) {
            this.addDocumentLog(req, sourceId.toString(), ActionType.COPY, doc.getDocumentName() + "复制到个人文件根目录", doc.getCreateTime());
        } else {
            this.addDocumentLog(req, sourceId.toString(), ActionType.COPY, doc.getDocumentName() + "复制到" + t_doc.getDocumentName(), doc.getCreateTime());
        }

        // 获取目标文件夹权限
        DocumentPermissionExample example = new DocumentPermissionExample();
        DocumentPermissionExample.Criteria criteria = example.createCriteria();
        criteria.andIncIdEqualTo(incId);
        criteria.andDocumentIdEqualTo(t_doc.getId());
        List<DocumentPermission> documentPermissions = this.documentPermissionMapper.selectByExample(example);

        try {
            //判断目录或文件
            String docName = doc.getDocumentName();
            if(DocumentConstants.DOCUMENT_TYPE_ORGANIZED.equals(t_doc.getType())) {
                if (exists(type, t_doc.getId(), incId, doc.getDocumentName(), null)) {
                    docName = this.commonFileService.generateDirName(doc.getDocumentType(), t_doc.getId(), incId, doc.getDocumentName(), type, userId);
                }
            }
            if(DocumentConstants.DOCUMENT_TYPE_PERSONAL.equals(t_doc.getType())) {
                if (exists(type, t_doc.getId(), incId, doc.getDocumentName(), null)) {
                    docName = this.commonFileService.generateDirName(doc.getDocumentType(), t_doc.getId(), incId, doc.getDocumentName(),type, userId);
                }
            }

            doc.setId(null);
            doc.setParentId(t_doc.getId());
            doc.setDocumentName(docName);
            doc.setCreateTime(new Date());
            doc.setCreateUser(userId);
            doc.setCreateUsername(userName);
            doc.setUpdateUser(userId);
            doc.setUpdateUsername(userName);
            doc.setUpdateTime(doc.getCreateTime());
            doc.setType(type);
            // 添加文档时设置文档类型 type
            doc.setType(t_doc.getType());

            documentMapper.insert(doc);
            String doc_idPath = "";
            if(t_doc.getIdPath().endsWith("/")) {
                doc_idPath = t_doc.getIdPath() + doc.getId() + "/";
            } else {
                doc_idPath = t_doc.getIdPath() + "/" + doc.getId() + "/";
            }
            doc.setIdPath(doc_idPath);
            documentMapper.updateByPrimaryKeySelective(doc);
            if (!"dir".equalsIgnoreCase(doc.getDocumentType())) {
                DocumentVersion sourceDocVersion = documentVersionMapper2.selectDocumentVersionById(sourceId);
                DocumentVersion descDocVersion = new DocumentVersion();
                BeanUtils.copyProperties(sourceDocVersion, descDocVersion);
                descDocVersion.setId(null);
                descDocVersion.setDocumentId(doc.getId());
                descDocVersion.setCephBucketKey(UUID.randomUUID().toString());
                descDocVersion.setVersion(1);
                descDocVersion.setIsDelete("0");
                descDocVersion.setOperateType("3");//3 复制文件 、 4移动文件
                descDocVersion.setCreateTime(new Date());
                descDocVersion.setCreateUser(userId);
                descDocVersion.setCreateUsername(userName);
                descDocVersion.setUpdateTime(null);
                descDocVersion.setUpdateUser(null);
                descDocVersion.setUpdateUsername(null);
                //ceph中复制文件
                S3copy(sourceDocVersion.getCephBucket(), sourceDocVersion.getCephBucketKey(), descDocVersion.getCephBucket(), descDocVersion.getCephBucketKey(), connection);
                documentVersionMapper.insert(descDocVersion);

                // 更新文档version_id
                doc.setDocumentVersionId(descDocVersion.getId());
                documentMapper.updateByPrimaryKeySelective(doc);

                // 判断目标文件夹是否是组织文件夹,如果是,继承父级权限
                if(t_doc.getType().equals("0")) {
                    // 插入新的权限记录
                    for (int i = 0; i < documentPermissions.size(); i++) {
                        DocumentPermission documentPermission = new DocumentPermission();
                        documentPermission.setIncId(incId);
                        documentPermission.setDocumentId(doc.getId());
                        documentPermission.setPermission(documentPermissions.get(i).getPermission());
                        documentPermission.setReceiverId(documentPermissions.get(i).getReceiverId());
                        documentPermission.setReceiverType(documentPermissions.get(i).getReceiverType());
                        documentPermission.setShareId(documentPermissions.get(i).getShareId());
                        this.documentPermissionMapper2.insert(documentPermission);
                    }
                }

                // 记录操作日志
                // 新文件
                this.addDocumentLog(req, doc.getId().toString(), ActionType.COPY, doc.getDocumentName(), doc.getCreateTime());

            } else {
                if (DocumentConstants.DOCUMENT_TYPE_ORGANIZED.equals(t_doc.getType()) &&
                         DocumentConstants.DOCUMENT_ROOT_PARENTID.equals(t_doc.getId())){
                    DocumentPermission documentPermission = new DocumentPermission();
                    documentPermission.setIncId(incId);
                    documentPermission.setDocumentId(doc.getId());
                    documentPermission.setPermission(DocumentConstants.DOCUMENT_PERMISSION_CREATE);
                    documentPermission.setReceiverId(userId);
                    documentPermission.setReceiverType(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER);
                    this.documentPermissionMapper2.insert(documentPermission);
                }
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("parentId", sourceId);
                List<Document> subDocuments = documentMapper2.getDocumentByParentId(params);
                for (Document sdoc : subDocuments) {
                    recursionCopy(req, sdoc.getId(), doc, incId, userId, userName, type, connection);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }


    private void S3copy(String bucketName, String sourceKey, String destinationBucketName,
                        String destinationKey, AmazonS3 connection) {
        S3Utils.copyObject(bucketName, sourceKey, destinationBucketName, destinationKey, connection);
    }

    @Override
    public boolean exists(String type, Long parentId, Long incId, String documentName, Long userId) {
        return this.commonFileService.exists(parentId, incId, documentName, type, userId);
    }

    @Override
    public Double statisticsZoneSize(Long id) {
        Integer size = documentMapper2.statisticsZoneSize(id);
        return size / 1024.00 / 1024.00;
    }

    @Override
    public Double getZoneByUserId(Long incId) {
        IncConfigExample incConfigExample = new IncConfigExample();
        incConfigExample.createCriteria().andIncIdEqualTo(incId);
        IncConfig incConfig = incConfigMapper.selectByExample(incConfigExample)
                .get(0);
        if (incConfig == null) {
            throw new YCException(Messages.API_OBJECT_NOTFOUND_MSG, Messages.API_OBJECT_NOTFOUND_CODE);
        }
        Integer size = incConfig.getPerUserQuota();
        return size / 1024.00;

    }

    @Override
    public String calculateZoneUtilization(Long userId, Long incId) {
        DecimalFormat df = new DecimalFormat("0.000");
        Double usedSize = statisticsZoneSize(userId);
        Double size = getZoneByUserId(incId);

        return df.format(usedSize) + "G" + "/" + df.format(size) + "G";

    }

    @Override
    public S3Object getDocumentByVersionId(Long docVersionId) {
        DocumentVersion documentVersion = this.documentVersionMapper.selectByPrimaryKey(docVersionId);
        String cephBucket = documentVersion.getCephBucket();
        String cephBucketKey = documentVersion.getCephBucketKey();
        IncConfigExample example = new IncConfigExample();
        example.createCriteria().andIncIdEqualTo(documentVersion.getIncId());
        IncConfig incConfig = this.incConfigMapper.selectByExample(example).get(0);
        AmazonS3 connect = S3Utils.getConnect(incConfig.getCephAccessKey(), incConfig.getCephSecretKey(), incConfig.getCephUrl());

        GetObjectRequest objRequest = new GetObjectRequest(cephBucket, cephBucketKey);
        S3Object object = connect.getObject(objRequest);

        connect.shutdown();

        return object;
    }

    @Override
    public Document getDocumentByVersion(Long docVersionId) {
        DocumentVersion documentVersion = this.documentVersionMapper.selectByPrimaryKey(docVersionId);
        Long documentId = documentVersion.getDocumentId();
        return this.documentMapper.selectByPrimaryKey(documentId);
    }

    @Override
    public String getFilePathByVersionId(Long docVersionId) {
        DocumentVersion documentVersion = this.documentVersionMapper.selectByPrimaryKey(docVersionId);
        String cephBucket = documentVersion.getCephBucket();
        String cephBucketKey = documentVersion.getCephBucketKey();
        IncConfigExample example = new IncConfigExample();
        example.createCriteria().andIncIdEqualTo(documentVersion.getIncId());
        IncConfig incConfig = this.incConfigMapper.selectByExample(example).get(0);
        AmazonS3 connect = S3Utils.getConnect(incConfig.getCephAccessKey(), incConfig.getCephSecretKey(), incConfig.getCephUrl());

        String url = singleDownload(documentVersion, connect);

        return url;
    }

    @Override
    public void updateDocumentTypeByIdBatch(Integer incId, Users user, String[] documentIds, String type) {
        Document doc = new Document();
        doc.setDocumentType(type);
        doc.setUpdateTime(new Date());
        doc.setUpdateUser(user.getUserId().longValue());
        doc.setUpdateUsername(user.getUserName());
        DocumentExample example = new DocumentExample();
        DocumentExample.Criteria criteria = example.createCriteria();
        criteria.andIncIdEqualTo(Long.parseLong(incId.toString()));
        List<Long> docIds = new ArrayList<>();
        for (String docId: documentIds) {
            docIds.add(Long.parseLong(docId));
        }
        criteria.andIdIn(docIds);
        this.documentMapper.updateByExampleSelective(doc, example);
    }

    @Override
    public boolean checkDirectoryName(Integer incId, Users user, String parentId, String directoryName, String type) {
        return this.commonFileService.isExistDirectory(Long.parseLong(incId.toString()), Long.parseLong(parentId), directoryName, type);
    }

    @Override
    public Document getDocumentById(Long id) {
        return this.documentMapper.selectByPrimaryKey(id);
    }

    @Override
    public DocumentVersion getDocumentVsersionByVersion(long versionId) {
        return this.documentVersionMapper.selectByPrimaryKey(versionId);
    }

    /**
     * 获取最近浏览历史文件
     * @param incId
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ResultCode<List<DocumentRecentBrowseVO>> getRecentBrowseDocuments(Integer incId, Integer userId, List<Long> userGroupIds, Long deptId, Integer pageNum, Integer pageSize) {
        ResultCode<List<DocumentRecentBrowseVO>> result = new ResultCode<List<DocumentRecentBrowseVO>>();
        String[] actionTypeArray = this.RECENT_BROWSE_ACTION_TYPE.split("\\|");
        List<String> actionTypeList = new ArrayList<>();
        for(String actionType: actionTypeArray) {
            actionTypeList.add(actionType.trim());
        }
        Map<String, Object> params = new HashMap<>();
        params.put("incId", Long.parseLong(incId.toString()));
        params.put("userId", Long.parseLong(userId.toString()));
        if(userGroupIds != null && userGroupIds.size() > 0) {
            params.put("groupIds", userGroupIds);
        } else {
            params.put("groupIds", null);
        }
        params.put("departmentId", deptId);
        Integer startNum = null;
        if(pageNum != null && pageNum > 0 && pageSize != null && pageSize > 0 ) {
            startNum = (pageNum - 1) * pageSize;
        } else {
            pageSize = null;
        }
        params.put("actionType", actionTypeList);                                   // 最近浏览记录统计依据: 预览 . 上传 . 下载 等
        params.put("time", Integer.parseInt(this.RECENT_BROWSE_TIME.trim()));       // 最近浏览记录统计时间段: 几天前,几周前,几个月前
        params.put("timeUnit", this.RECENT_BROWSE_TIME_UNIT.trim().toUpperCase());  // 最近浏览记录统计时间单元
        List<DocumentRecentBrowseVO> documents = this.documentMapper2.getRecentBrowseDocuments(params);
        List<DocumentRecentBrowseVO> newDocuments = new ArrayList<>();
        // 判断权限 & 添加 namePath
        labelA:
        for (DocumentRecentBrowseVO documentRecentBrowseVO: documents) {
            // 设置权限(组织文档)
            if(!DocumentConstants.DOCUMENT_TYPE_PERSONAL.equals(documentRecentBrowseVO.getType())) {
                String permission = this.documentPermissionService.getPermission(documentRecentBrowseVO.getIdPath(), Long.parseLong(userId.toString()), userGroupIds, deptId);
                if(null == permission) {
                    continue;
                }
                documentRecentBrowseVO.setPermission(permission);
            }

            // 设置namePath
           /* String[] idPath = documentRecentBrowseVO.getIdPath().split("/");
            StringBuffer sb = new StringBuffer();
            for(String id: idPath) {
                if(StringUtils.isNotBlank(id)) {
                    Document document = this.documentMapper.selectByPrimaryKey(Long.parseLong(id));
                    if(document != null) {
                        sb.append("/" + document.getDocumentName());
                    } else {
                        continue labelA;
                    }
                }
            }
            Map<String, Object> params2 = new HashMap<>();
            params.put("incId", incId);
            params.put("userId", userId);
            params.put("groupIds", userGroupIds);
            params.put("deptId", deptId);
            params.put("docId", documentRecentBrowseVO.getId());
            Integer isShared = this.documentShareMapper2.isSharedToMe(params2);
            String type = documentRecentBrowseVO.getType();
            if ("0".equals(type) && 0 == isShared) {
                documentRecentBrowseVO.setNamePath("/组织文件" + sb);
                documentRecentBrowseVO.setSource("0");
            } else if ("1".equals(type) && 0 == isShared) {
                documentRecentBrowseVO.setNamePath("/个人文件" + sb);
                documentRecentBrowseVO.setSource("1");
            } else if ("2".equals(type) && 0 == isShared) {
                documentRecentBrowseVO.setNamePath("/归档文件" + sb);
                documentRecentBrowseVO.setSource("2");
            } else if (0 != isShared) {
                documentRecentBrowseVO.setNamePath("/与我共享" + sb);
                documentRecentBrowseVO.setSource("3");
            }*/
            newDocuments.add(documentRecentBrowseVO);
        }
        result.setData(newDocuments);
        return result;
    }

    @Override
    public void previewFile(HttpServletRequest req, String filePath, HttpServletResponse resp, List<String> documentIds) {
        DownloadUtil.download(filePath, resp);

        // 记录文档预览日志
        for (String docId: documentIds) {
            Document document = this.documentMapper.selectByPrimaryKey(Long.parseLong(docId));
            this.addDocumentLog(req, docId, ActionType.PREVIEW, document.getDocumentName(), new Date());
        }
    }

    @Override
    public void removeRecentBrowseRecords(Integer incId, Integer userId, String logIds) {
        for(String logId: logIds.split(",")) {
            if(StringUtils.isNotBlank(logId)) {
                Log record = new Log();
                record.setIsDelete("1");
                LogExample example = new LogExample();
                example.createCriteria()
                        .andIncIdEqualTo(Long.parseLong(incId.toString()))
                        .andCreateUserIdEqualTo(Long.parseLong(userId.toString()))
                        .andIdEqualTo(Long.parseLong(logId));
                this.logMapper.updateByExampleSelective(record, example);
            }

        }
    }

    @Override
    public Boolean checkExist(String docVersionId) {
        String is_delete = this.documentMapper2.checkFileIsDeleteByDocumentVersionId(docVersionId);
        if(DocumentConstants.DOCUMENT_IS_DELETE_NO.equals(is_delete)) {
            return true;
        }
        return false;
    }

    @Override
    public ResultCode<Document> getDocumentByIdWithPermission(Integer incId, Integer userId, List<Long> groupIds, Long deptId, Long docId) {
        ResultCode<Document> result = new ResultCode<>();
        Document document = this.documentMapper.selectByPrimaryKey(docId);
        if(document != null) {
            if(DocumentConstants.DOCUMENT_TYPE_PERSONAL.equals(document.getType())) {
                if(userId.toString().equals(document.getCreateUser().toString())) {
                    result.setData(document);
                } else {
                    result.setCode(Messages.API_AUTHEXCEPTION_CODE);
                    result.setMsg(Messages.API_AUTHEXCEPTION_MSG);
                }
            } else {
                String permission = this.documentPermissionService.getPermission(document.getIdPath(), Long.parseLong(userId.toString()), groupIds, deptId);
                if(permission == null) {
                    result.setCode(Messages.API_AUTHEXCEPTION_CODE);
                    result.setMsg(Messages.API_AUTHEXCEPTION_MSG);
                }
            }
        } else {
            result.setCode(Messages.FILE_NOT_EXIST_CODE);
            result.setMsg(Messages.FILE_NOT_EXIST_MSG);
        }
        return result;
    }

    @Override
    public ResultCode<Map<String, String>> getNamePathByIdPath(Integer incId, String idPath) {
        ResultCode<Map<String, String>> resultCode = new ResultCode<>();
        Map<String, String> data = new HashMap<>();
        StringBuffer sb = new StringBuffer();

        DocumentExample example = new DocumentExample();
        example.createCriteria().andIncIdEqualTo(Long.parseLong(incId.toString())).andIdPathEqualTo(idPath);
        List<Document> documents = this.documentMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(documents)) {
            Document document = documents.get(0);
            if(DocumentConstants.DOCUMENT_TYPE_PERSONAL.equals(document.getType())) {
                data.put("source", "个人文件");
            }
            if(DocumentConstants.DOCUMENT_TYPE_ORGANIZED.equals(document.getType())) {
                data.put("source", "组织文件");
            }
            if(DocumentConstants.DOCUMENT_TYPE_ARCHIVE.equals(document.getType())) {
                data.put("source", "归档文件");
            }
            String[] ids = idPath.split("/");
            for(String id: ids) {
                if(StringUtils.isNotBlank(id)) {
                    Document doc = this.documentMapper.selectByPrimaryKey(Long.parseLong(id));
                    if(doc != null) {
                        sb.append("/" + doc.getDocumentName());
                    } else {
                        resultCode.setCode(Messages.FILE_NOT_EXIST_CODE);
                        resultCode.setMsg(Messages.FILE_NOT_EXIST_MSG);
                        return resultCode;
                    }
                }
            }
            data.put("namePath", sb.toString());
        } else {
            resultCode.setCode(Messages.FILE_NOT_EXIST_CODE);
            resultCode.setMsg(Messages.FILE_NOT_EXIST_MSG);
        }
        resultCode.setData(data);
        return resultCode;
    }

    @Override
    public ResultCode<Map<String, Date>> getLastUpdateTime(Integer incId, Integer userId) {
        ResultCode<Map<String, Date>> result = new ResultCode<>();
        Map<String, Date> data = this.documentMapper2.getLastUpdateTime(incId, userId);
        result.setData(data);
        return result;
    }

    @Override
    public Long timingUnlock(Integer unlockDay) {
        Long count = this.documentMapper2.timingUnlock(unlockDay);
        return count;
    }


    private String singleDownload(DocumentVersion version, AmazonS3 connect) {
        String path;
        String temFolder = UUID.randomUUID().toString().substring(0, 13);
        String fileName = "/" + "(V-" + version.getVersion() + ".0)" + documentVersionMapper2.getFileNameByVersionId(version.getId());
        path = downloadToServer(version, "/" + temFolder + fileName, connect);
        return path;

    }




    @Value("${tempPath}")
    private String serverDownloadPath;

    private String downloadToServer(DocumentVersion versions, String path, AmazonS3 connect) {
        String bucket = versions.getCephBucket();
        String fileKey = versions.getCephBucketKey();
        String realPath = serverDownloadPath + path;
        try {
            S3Utils.downByS3(bucket, fileKey, realPath, connect);
        } catch (Exception e) {
            logger.error("S3下载异常", e);
            throw e;
        }
        return realPath;
    }
}
