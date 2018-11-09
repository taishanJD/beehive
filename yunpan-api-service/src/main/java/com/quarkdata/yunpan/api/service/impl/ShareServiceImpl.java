package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.quark.share.model.dataobj.Department;
import com.quarkdata.quark.share.model.dataobj.Group;
import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.dal.dao.*;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.DepartmentApi;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.GroupApi;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.UsersApi;
import com.quarkdata.yunpan.api.model.common.ActionType;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.*;
import com.quarkdata.yunpan.api.model.request.Receiver;
import com.quarkdata.yunpan.api.model.vo.DocumentExtend;
import com.quarkdata.yunpan.api.model.vo.DocumentPermissionVO;
import com.quarkdata.yunpan.api.model.vo.DocumentShareExtend;
import com.quarkdata.yunpan.api.model.vo.ShareAndLinkVO;
import com.quarkdata.yunpan.api.service.DocumentPermissionService;
import com.quarkdata.yunpan.api.service.ShareService;
import com.quarkdata.yunpan.api.util.ResultUtil;
import com.quarkdata.yunpan.api.util.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author maorl
 * @date 12/6/17.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ShareServiceImpl extends BaseLogServiceImpl implements ShareService {
    private static Logger logger = LoggerFactory.getLogger(ShareServiceImpl.class);

    @Autowired
    DocumentShareMapper documentShareMapper;

    @Autowired
    DocumentPermissionMapper documentPermissionMapper;

    @Autowired
    DocumentPermissionMapper2 documentPermissionMapper2;

    @Autowired
    private CommonFileServiceImpl commonFileService;

    @Autowired
    DocumentMapper documentMapper;

    @Autowired
    DocumentMapper2 documentMapper2;

    @Autowired
    DocumentShareMapper2 documentShareMapper2;

    @Autowired
    TagMapper2 tagMapper2;

    @Autowired
    CollectDocumentRelMapper collectDocumentRelMapper;

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
     * 删除与我共享1
     * @param incId
     * @param user
     * @param request
     * @param ids
     * @return
     */
   /* @Override
    public ResultCode deleteShare(long incId, Users user, HttpServletRequest request, List<String> ids) {
        ResultCode resultCode;
        try {
            for (String id :
                    ids) {
                long idVal = Long.valueOf(id);
                //Get document id
                DocumentShare documentShare = documentShareMapper.selectByPrimaryKey(idVal);
                long docId = documentShare.getDocumentId();
                //Delete share records
                //TODO  这有个巨大的bug  下版本修复  20180116
                Long createUser = documentShare.getCreateUser();
                DocumentShareExample example = new DocumentShareExample();
                example.createCriteria().andDocumentIdEqualTo(docId)
                        .andCreateUserEqualTo(createUser);
                documentShareMapper.deleteByExample(example);
                //update or not?
                DocumentShareExample shareExample = new DocumentShareExample();
                shareExample.createCriteria().andDocumentIdEqualTo(docId);
                List<DocumentShare> documentShares = documentShareMapper.selectByExample(shareExample);
                if (documentShares != null && documentShares.size() > 0) {
                    continue;
                }
                //update document table
                Document document = new Document();
                document.setId(docId);
                document.setIsShare("0");
                document.setUpdateTime(new Date());
                documentMapper.updateByPrimaryKeySelective(document);

                // 记录操作日志
                Document doc = this.documentMapper.selectByPrimaryKey(docId);
                this.addDocumentLog(request, doc.getId().toString(), ActionType.DELETE_SHARE, doc.getDocumentName(), doc.getUpdateTime());

            }
            resultCode = ResultUtil.success();
        } catch (Exception e) {
            logger.error("delete share record failed:", e);
            resultCode = ResultUtil.error(Messages.API_DEL_SHARE_FAILED_CODE, Messages.API_DEL_SHARE_FAILED_MSG);
        }
        return resultCode;
    }*/

    /**
     * 删除与我共享2: --- 取消共享给用户组或部门共享时在共享表里面添加一条个人取消记录
     *
     * @param incId
     * @param user
     * @param request
     * @param ids document_share id
     * @return
     */
    @Override
    public ResultCode deleteShare(long incId, Users user, HttpServletRequest request, List<String> ids) {
        ResultCode resultCode;
        try {
            for (String id: ids) {
                long idVal = Long.valueOf(id);
                DocumentShare documentShare = documentShareMapper.selectByPrimaryKey(idVal);
                long docId = documentShare.getDocumentId();
                Long createUser = documentShare.getCreateUser();
                // 判断该文档是分享给个人的还是部门或用户组的;
                String receiverType = documentShare.getReceiverType();
                if("0".equals(receiverType)) {
                    // 如果是分享给个人的直接将is_delete置为 1;
                    DocumentShare record = new DocumentShare();
                    record.setIsDelete("1");
                    record.setUpdateUser(Long.parseLong(user.getUserId().toString()));
                    record.setUpdateUsername(user.getUserName());
                    record.setUpdateTime(new Date());

                    DocumentShareExample example = new DocumentShareExample();
                    DocumentShareExample.Criteria criteria = example.createCriteria();
                    criteria.andIncIdEqualTo(incId);
                    criteria.andIdEqualTo(documentShare.getId());
                    this.documentShareMapper.updateByExampleSelective(record, example);
                } else {
                    // 如果是分享给部门或用户组的,插入一条is_delete为 1 与当前用户关联的记录, 查询当前用户共享列表时排除这条记录
                    // 插入之前先查询是否有与该用户关联的记录
                    DocumentShareExample example = new DocumentShareExample();
                    example.createCriteria().andIncIdEqualTo(incId).andReceiverTypeEqualTo("0")
                            .andReceiverIdEqualTo(Long.parseLong(user.getUserId().toString())).andDocumentIdEqualTo(docId);
                    List<DocumentShare> documentShares = this.documentShareMapper.selectByExample(example);
                    if(documentShares != null && documentShares.size() > 0) {
                        // 说明有记录,直接修改
                        for(DocumentShare docShare: documentShares) {
                            docShare.setIsDelete("1");
                            docShare.setUpdateUser(Long.parseLong(user.getUserId().toString()));
                            docShare.setUpdateUsername(user.getUserName());
                            docShare.setUpdateTime(new Date());
                            this.documentShareMapper.updateByPrimaryKeySelective(docShare);
                        }
                    } else {
                        DocumentShare record = new DocumentShare();
                        record.setIncId(incId);
                        record.setIsDelete("1");
                        record.setDocumentId(documentShare.getDocumentId());
                        record.setReceiverId(Long.parseLong(user.getUserId().toString()));
                        record.setReceiverType("0");
                        record.setCreateUser(documentShare.getCreateUser());
                        record.setCreateUsername(documentShare.getCreateUsername());
                        record.setCreateTime(new Date());
                        record.setUpdateUser(Long.parseLong(user.getUserId().toString()));
                        record.setUpdateUsername(user.getUserName());
                        record.setUpdateTime(record.getCreateTime());
                        this.documentShareMapper.insert(record);
                    }
                }

               /* //update or not?
                DocumentShareExample shareExample = new DocumentShareExample();
                shareExample.createCriteria().andDocumentIdEqualTo(docId).andIsDeleteEqualTo("0");
                List<DocumentShare> documentShares = documentShareMapper.selectByExample(shareExample);
                if (documentShares != null && documentShares.size() > 0) {
                    continue;
                }
                //update document table
                Document document = new Document();
                document.setId(docId);
                document.setIsShare("0");
                document.setUpdateTime(new Date());
                documentMapper.updateByPrimaryKeySelective(document);*/

                // 记录操作日志
                Document doc = this.documentMapper.selectByPrimaryKey(docId);
                this.addDocumentLog(request, doc.getId().toString(), ActionType.DELETE_SHARE, doc.getDocumentName(), doc.getUpdateTime());

            }
            resultCode = ResultUtil.success();
        } catch (Exception e) {
            logger.error("delete share record failed:", e);
            resultCode = ResultUtil.error(Messages.API_DEL_SHARE_FAILED_CODE, Messages.API_DEL_SHARE_FAILED_MSG);
        }
        return resultCode;
    }


    /**
     * 新建分享
     *
     *
     * @param request
     * @param documentIds
     * @param receiverList
     * @param excludeList
     * @param users
     * @return
     */
    @Override
    public ResultCode addShare(HttpServletRequest request, List<String> documentIds, List<Receiver> receiverList, List<Receiver> excludeList, Users users) {
        logger.info("插入分享信息");
        Long incId = Long.valueOf(users.getIncid());
        Long userId = Long.valueOf(users.getUserId());
        try {
            for (String did : documentIds) {
                Long didVal = Long.valueOf(did);
                Document doc = documentMapper.selectByPrimaryKey(didVal);
                if (!"1".equals(doc.getType())) {
                    logger.info("插入非个人文件，本次插入失败");
                    return ResultUtil.error(Messages.API_SHARE_LEGAL_CODE, Messages.API_SHARE_LEGAL_MSG);
                }
            }
            for (String id : documentIds) {
                // 1.分享之前删除所有与该文档相关的document_share记录和document_permission记录
                DocumentShareExample example = new DocumentShareExample();
                example.createCriteria().andIncIdEqualTo(incId).andDocumentIdEqualTo(Long.parseLong(id));
                List<DocumentShare> documentShares = this.documentShareMapper.selectByExample(example);
                if(documentShares != null && documentShares.size() > 0) {
                    for(DocumentShare documentShare: documentShares) {
                        // 查询与该share记录相关的权限记录并删除
                        DocumentPermissionExample example_permission = new DocumentPermissionExample();
                        example_permission.createCriteria().andIncIdEqualTo(incId)
                                .andShareIdEqualTo(documentShare.getId()).andDocumentIdEqualTo(Long.parseLong(id));
                        this.documentPermissionMapper.deleteByExample(example_permission);
                        this.documentShareMapper.deleteByPrimaryKey(documentShare.getId());
                    }
                }

                // 2.插入分享记录
                for (Receiver r : receiverList) {
                    if(r.getRecType().equals("0") && Long.parseLong(r.getRecId()) == userId) {
                        continue;
                    }
                    Long idVal = Long.valueOf(id);
                    //判断是否已经分享过
                    HashMap map = getPermission(idVal,r);
                    if (isShared(r, map))  {
                        continue;
                    }
                    //Insert  table document_share
                    DocumentShare documentShare = insertShare(users, incId, userId, r, idVal);
                    //Insert table document_permission
                    insertPermission(incId, r, idVal, documentShare);
                    // 如果是分享给部门,同时分享给部门下所属的所有用户组
                    if(r.getRecType().equals("2")) {
                        List<Group> groups = this.groupApi.getGroupByDeptId(Integer.parseInt(incId.toString()), r.getRecId(), null);
                        if(groups != null && groups.size() > 0) {
                            for(Group g: groups) {
                                Receiver r_dept_of_group = new Receiver();
                                r_dept_of_group.setRecId(g.getId().toString());
                                r_dept_of_group.setRecType("1");
                                r_dept_of_group.setPermission(r.getPermission());
                                DocumentShare documentShare_dept_of_group = insertShare(users, incId, userId, r_dept_of_group, idVal);
                                insertPermission(incId, r_dept_of_group, idVal, documentShare_dept_of_group);
                                // 递归分享给所有相关用户组
                                this.recursiveAddShareToGroups(incId, users, idVal, r_dept_of_group);
                            }
                        }
                    }
                    //Update table document
                    Document document = new Document();
                    document.setId(idVal);
                    document.setIsShare("1");
                    document.setUpdateTime(new Date());
                    documentMapper.updateByPrimaryKeySelective(document);
                }
                // 3.插入排除分享单位(用户 or 用户组 or 部门)
                if(excludeList != null && excludeList.size() > 0) {
                    for(Receiver exclusion: excludeList) {
                        // 查询之前是否有该文档与该接受者的排除分享记录, 如果有,将is_delete置为 1
                        DocumentShareExample example_exclude = new DocumentShareExample();
                        example_exclude.createCriteria().andIncIdEqualTo(incId).andDocumentIdEqualTo(Long.parseLong(id))
                                .andReceiverTypeEqualTo(exclusion.getRecType()).andReceiverIdEqualTo(Long.parseLong(exclusion.getRecId()));
                        List<DocumentShare> excluded_shares = this.documentShareMapper.selectByExample(example_exclude);
                        if(excluded_shares != null && excluded_shares.size() > 0) {
                            for(DocumentShare documentShare: excluded_shares) {
                                documentShare.setIsDelete("1");
                                this.documentShareMapper.updateByPrimaryKeySelective(documentShare);
                            }
                        } else{
                            //如果没有,插入新的is_delete为1的排除分享记录
                            DocumentShare excluded_share = new DocumentShare();
                            excluded_share.setIncId(incId);
                            excluded_share.setReceiverId(Long.parseLong(exclusion.getRecId()));
                            excluded_share.setReceiverType(exclusion.getRecType());
                            excluded_share.setIsDelete("1");
                            excluded_share.setDocumentId(Long.parseLong(id));
                            excluded_share.setCreateUser(userId);
                            excluded_share.setCreateUsername(users.getUserName());
                            excluded_share.setCreateTime(new Date());
                            this.documentShareMapper.insert(excluded_share);
                        }
                        // 如果是部门,同时排除部门下的所有用户组
                        if(exclusion.getRecType().equals("2")) {
                            List<Group> groups = this.groupApi.getGroupByDeptId(Integer.parseInt(incId.toString()), exclusion.getRecId(), null);
                            if(groups != null && groups.size() > 0) {
                                for(Group g: groups) {
                                    // 查询之前是否有关联记录,如果有,修改is_delete为1
                                    DocumentShareExample example_new = new DocumentShareExample();
                                    example_new.createCriteria().andIncIdEqualTo(incId)
                                            .andDocumentIdEqualTo(Long.parseLong(id))
                                            .andReceiverTypeEqualTo("1").andReceiverIdEqualTo(Long.parseLong(g.getId().toString()));
                                    List<DocumentShare> shares = this.documentShareMapper.selectByExample(example_new);
                                    if(shares != null && shares.size() > 0) {
                                        for(DocumentShare ds: shares) {
                                            ds.setIsDelete("1");
                                            this.documentShareMapper.updateByPrimaryKeySelective(ds);
                                        }
                                    } else{
                                        //如果没有,插入新的is_delete为1的排除分享记录
                                        DocumentShare excluded_share_new = new DocumentShare();
                                        excluded_share_new.setIncId(incId);
                                        excluded_share_new.setReceiverId(Long.parseLong(g.getId().toString()));
                                        excluded_share_new.setReceiverType("1");
                                        excluded_share_new.setIsDelete("1");
                                        excluded_share_new.setDocumentId(Long.parseLong(id));
                                        excluded_share_new.setCreateUser(userId);
                                        excluded_share_new.setCreateUsername(users.getUserName());
                                        excluded_share_new.setCreateTime(new Date());
                                        this.documentShareMapper.insert(excluded_share_new);
                                    }
                                    // 递归插入子用户组排除分享记录
                                    this.recursiveExcludeShareToGroups(incId, users, Long.parseLong(id), g);
                                }
                            }
                        }
                    }
                }
                // 记录操作日志
                Document doc = this.documentMapper.selectByPrimaryKey(Long.parseLong(id));
                this.addDocumentLog(request, doc.getId().toString(), ActionType.SHARE, doc.getDocumentName(), doc.getUpdateTime());
            }
            logger.info("插入分享信息成功");
        } catch (Exception e) {
            logger.error("插入分享信息失败：" + e);
            return ResultUtil.error(Messages.API_ADD_SHARE_FAILED_CODE, Messages.API_ADD_SHARE_FAILED_MSG);
        }
        return ResultUtil.success();
    }

    /**
     * 递归排除分享至所有用户组
     * @param incId
     * @param users
     * @param docId
     * @param g
     */
    private void recursiveExcludeShareToGroups(Long incId, Users users, long docId, Group g) {
        List<Group> groups = this.groupApi.getGroupByGroupId(Integer.parseInt(incId.toString()), g.getId().toString(), null, null, null);
            if(groups != null && groups.size() > 0) {
                for (Group group : groups) {
                    // 查询之前是否有关联记录,如果有,修改is_delete为1
                    DocumentShareExample example = new DocumentShareExample();
                    example.createCriteria().andIncIdEqualTo(incId)
                            .andDocumentIdEqualTo(docId)
                            .andReceiverTypeEqualTo("1").andReceiverIdEqualTo(Long.parseLong(group.getId().toString()));
                    List<DocumentShare> shares = this.documentShareMapper.selectByExample(example);
                    if (shares != null && shares.size() > 0) {
                        for (DocumentShare documentShare : shares) {
                            documentShare.setIsDelete("1");
                            this.documentShareMapper.updateByPrimaryKeySelective(documentShare);
                        }
                    } else {
                        //如果没有,插入新的is_delete为1的排除分享记录
                        DocumentShare excluded_share = new DocumentShare();
                        excluded_share.setIncId(incId);
                        excluded_share.setReceiverId(Long.parseLong(group.getId().toString()));
                        excluded_share.setReceiverType("1");
                        excluded_share.setIsDelete("1");
                        excluded_share.setDocumentId(docId);
                        excluded_share.setCreateUser(Long.parseLong(users.getUserId().toString()));
                        excluded_share.setCreateUsername(users.getUserName());
                        excluded_share.setCreateTime(new Date());
                        this.documentShareMapper.insert(excluded_share);
                    }
                    // 递归插入子用户组排除分享记录
                    this.recursiveExcludeShareToGroups(incId, users, docId, group);
                }
            }
    }

    /**
     * 递归分享给所有子用户组
     * @param incId
     * @param users
     * @param docId
     * @param r
     */
    private void recursiveAddShareToGroups(Long incId, Users users, Long docId, Receiver r) {
        List<Group> groups = this.groupApi.getGroupByGroupId(Integer.parseInt(incId.toString()), r.getRecId(), null, null, null);
        if(groups != null && groups.size() > 0) {
            for(Group group: groups) {
                Receiver newReceiver = new Receiver();
                newReceiver.setRecId(group.getId().toString());
                newReceiver.setRecType("1");
                newReceiver.setPermission(r.getPermission());
                DocumentShare documentShare = insertShare(users, incId, Long.parseLong(users.getUserId().toString()), newReceiver, docId);
                insertPermission(incId, newReceiver, docId, documentShare);
                // 递归分享给所有相关用户组
                this.recursiveAddShareToGroups(incId, users, docId, newReceiver);
            }
        }

    }

    private boolean isShared(Receiver r, HashMap map) {
        if (map!=null) {
            String permission = (String) map.get("permission");
            if ("1".equals(permission)) {
                return true;
            }
            if ("0".equals(permission) && "1".equals(r.getPermission())) {
                Long permissionId = (Long) map.get("id");
                DocumentPermission dp = new DocumentPermission();
                dp.setId(permissionId);
                dp.setPermission("1");
                documentPermissionMapper.updateByPrimaryKeySelective(dp);
                return true;
            }
        }
        return false;
    }

    /** 插入权限数据
     * @param incId
     * @param r
     * @param idVal
     * @param documentShare
     */
    private void insertPermission(Long incId, Receiver r, Long idVal, DocumentShare documentShare) {
        // 插入之前查看是否已经有对应的权限记录,如果有修改此条权限记录,如果没有插入新的权限记录
        DocumentPermissionExample example = new DocumentPermissionExample();
        DocumentPermissionExample.Criteria criteria = example.createCriteria();
        criteria.andIncIdEqualTo(incId);
        criteria.andDocumentIdEqualTo(idVal);
        criteria.andReceiverTypeEqualTo(r.getRecType());
        criteria.andReceiverIdEqualTo(Long.parseLong(r.getRecId()));
        List<DocumentPermission> permissions = this.documentPermissionMapper.selectByExample(example);

        DocumentPermission documentPermission = new DocumentPermission();
        documentPermission.setDocumentId(idVal);
        documentPermission.setIncId(incId);
        documentPermission.setPermission(r.getPermission());
        documentPermission.setReceiverType(r.getRecType());
        documentPermission.setReceiverId(Long.valueOf(r.getRecId()));
        documentPermission.setShareId(documentShare.getId());

        if(permissions != null && permissions.size() > 0) {
            // 修改记录
            this.documentPermissionMapper.updateByExampleSelective(documentPermission, example);
        } else {
            // 插入新记录
            documentPermissionMapper2.insert(documentPermission);
        }

    }

    /**
     * 插入分享数据
     * @param users
     * @param incId
     * @param userId
     * @param r
     * @param idVal
     * @return
     */
    private DocumentShare insertShare(Users users, Long incId, Long userId, Receiver r, Long idVal) {
        DocumentShare documentShare = new DocumentShare();
        documentShare.setIncId(incId);
        documentShare.setDocumentId(idVal);
        documentShare.setCreateUser(userId);
        documentShare.setReceiverType(r.getRecType());
        documentShare.setReceiverId(Long.valueOf(r.getRecId()));
        documentShare.setCreateTime(new Date());
        documentShare.setCreateUsername(UserUtils.getFinalUserNameFromUser(users));
        documentShare.setIsDelete("0");
        documentShareMapper.insert(documentShare);
        return documentShare;
    }

    /**
     * 获取分享权限
     * @param docId
     * @param r
     * @return
     */
    private HashMap getPermission(Long docId, Receiver r) {
        String recId = r.getRecId();
        String recType = r.getRecType();
        return documentShareMapper2.isShared(docId, recType, recId);
    }

    /**
     * 获取与我共享列表
     *
     * @return
     */
    @Override
    public ResultCode<List<DocumentShareExtend>> getShareList(UserInfoVO users, Long parentId, String documentName, Integer pageNum, Integer pageSize) {
        ResultCode resultCode;
        List<DocumentShareExtend> documentShareExtends = new ArrayList<>();
        try {
            Integer incId = users.getUser().getIncid();
            Integer depId = users.getDepartment().getId();
            List<Group> groupsList = users.getGroupsList();
            List<Integer> groupIds = new ArrayList<>();
            for (Group group :
                    groupsList) {
                if (group == null) {
                    continue;
                }
                groupIds.add(group.getId());
            }
            Integer userId = users.getUser().getUserId();
            if (groupIds.size() == 0) {
                groupIds = null;
            }
            if(parentId != null && parentId == 0) {
                parentId = null;
            }
            if(StringUtils.isBlank(documentName)) {
                documentName = null;
            }
            Integer startNum = null;
            if(pageNum != null && pageNum > 0 && pageSize != null && pageSize > 0 ) {
                startNum = (pageNum - 1) * pageSize;
            } else {
                pageSize = null;
            }

            documentShareExtends = documentShareMapper2.selectShareList(userId, groupIds, depId, documentName, incId, parentId, startNum, pageSize);
            /*if(documentName == null) {
                // 按文件夹查询，非模糊查询
                if(parentId == null) {
                    // 最外层与我共享
                    documentShareExtends = documentShareMapper2.selectShareList(userId, groupIds, depId, documentName,users.getUser().getIncid(), parentId);
                } else {
                    // 获取与我共享子文件
                    documentShareExtends = this.getSubFileList(Long.parseLong(users.getUser().getIncid().toString()), users, documentName, parentId).getData();
                }
            } else {
                // 模糊查询
                if(parentId == null) {
                    // 模糊递归查询最外层与我共享
                    List<Long> parentIds = new ArrayList<>();
                    List<DocumentShareExtend> documentShareExtends1 = this.documentShareMapper2.selectShareList(userId, groupIds, depId, documentName, users.getUser().getIncid(), parentId);
                    if(CollectionUtils.isNotEmpty(documentShareExtends1)) {
                        documentShareExtends.addAll(documentShareExtends1);
                    }
                    List<DocumentShareExtend> documentShareExtends2 = this.documentShareMapper2.selectShareList(userId, groupIds, depId, null, users.getUser().getIncid(), null);
                    if(CollectionUtils.isNotEmpty(documentShareExtends2)) {
                        for(DocumentShareExtend documentShareExtend: documentShareExtends2) {
                            if(DocumentConstants.DOCUMENT_DOCUMENT_TYPE_DIR.equalsIgnoreCase(documentShareExtend.getDocumentType())) {
                                parentIds.add(documentShareExtend.getDocumentId());
                            }
                        }
                    }
                    if(CollectionUtils.isNotEmpty(parentIds)) {
                        for(Long id: parentIds) {
                            List<Document> data = this.commonFileService.getAllChildrenByParentIdAndDocumentName(incId, id, documentName, pageNum, pageSize);
                            if(CollectionUtils.isNotEmpty(data)) {
                                for(Document document: data) {
                                    DocumentShareExtend documentShareExtend = new DocumentShareExtend();
                                    BeanUtils.copyProperties(document, documentShareExtend);
                                    documentShareExtend.setDocumentId(document.getId());
                                    documentShareExtend.setPermission("0");
                                    documentShareExtends.add(documentShareExtend);
                                }
                            }
                        }
                    }
                } else {
                    // 指定文件夹递归查询所有文件
                    List<Document> children = this.commonFileService.getAllChildrenByParentIdAndDocumentName(incId, parentId, documentName, pageNum, pageSize);;
                    if(CollectionUtils.isNotEmpty(children)) {
                        for(Document document: children) {
                            DocumentShareExtend documentShareExtend = new DocumentShareExtend();
                            BeanUtils.copyProperties(document, documentShareExtend);
                            documentShareExtend.setDocumentId(document.getId());
                            documentShareExtend.setPermission("0");
                            documentShareExtends.add(documentShareExtend);
                        }
                    }
                }
            }*/
            Set<DocumentShareExtend> set = new HashSet<>();
            List<DocumentShareExtend> newList = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(documentShareExtends)) {
                for (DocumentShareExtend cd:documentShareExtends) {
                    if(set.add(cd)){
                        newList.add(cd);
                    }
                }
            }
            /*if(CollectionUtils.isNotEmpty(newList)) {
                Collections.sort(newList, new Comparator<DocumentShareExtend>() {
                    @Override
                    public int compare(DocumentShareExtend o1, DocumentShareExtend o2) {
                        if("dir".equals(o1.getDocumentType())) {
                            return -1;
                        } else if("dir".equals(o2.getDocumentType())) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                });
            }*/
            resultCode = ResultUtil.success(newList);
        } catch (Exception e) {
            logger.error("获取与我共享列表失败", e);
            resultCode = ResultUtil.error(Messages.API_SHARE_LIST_FAILED_CODE, Messages.API_SHARE_LIST_FAILED_MSG);
        }
        return resultCode;
    }
    /**
     * 获取与我共享文件的子文件
     *
     * @return
     */
    @Override
    public ResultCode<List<DocumentShareExtend>> getSubFileList(Long incId, UserInfoVO users, String documentName, Long parentId) {
        ResultCode resultCode;
        List<DocumentExtend> documentExtends = null;
        try {
            Integer depId = users.getDepartment().getId();
            List<Group> groupsList = users.getGroupsList();
            List<Long> groupIds = new ArrayList<>();
            for (Group group :
                    groupsList) {
                if (group == null) {
                    continue;
                }
                groupIds.add(Long.parseLong(group.getId().toString()));
            }
            Integer userId = users.getUser().getUserId();
            if (groupIds.size() == 0) {
                groupIds = null;
            }
            Map<String, Object> params = new HashMap<>();
            params.put("incId", incId);
            params.put("userId", Long.parseLong(userId.toString()));
            params.put("parentId", parentId);
            params.put("documentName", documentName);
            documentExtends = this.documentMapper2.getSubShareFileList(params);
            //判断权限
            if(documentExtends != null && documentExtends.size() > 0) {
                for(DocumentExtend documentExtend : documentExtends) {
//                    String permission = this.documentPermissionService.getPermission(documentExtend.getIdPath(), Long.parseLong(userId.toString()), groupIds, Long.parseLong(depId.toString()));
                    documentExtend.setPermission("0");
                    /*if(permission != null) {
                        documentExtend.setPermission(permission);
                    } else {
                    }*/
                }
            }

            /*documentIdExtendVO = documentMapper2.getSubFileList(userId, groupIds, depId, documentName,parentId,users.getUser().getIncid());
            Set<DocumentIdExtendVO> set = new HashSet<>();
            List<DocumentIdExtendVO> newList = new  ArrayList<>();
            for (DocumentIdExtendVO cd:documentIdExtendVO) {
                if(set.add(cd)){
                    newList.add(cd);
                }
            }*/
            resultCode = ResultUtil.success(documentExtends);
        } catch (Exception e) {
            logger.error("获取与我共享列表失败", e);
            resultCode = ResultUtil.error(Messages.API_DOCUMENT_SHARESUB_CODE, Messages.API_DOCUMENT_SHARESUB_MSG);
        }
        return resultCode;
    }

    @Override
    public ResultCode<String> getFileCount(UserInfoVO users) {
        ResultCode resultCode = new ResultCode<String>();
        try {
            Integer depId = users.getDepartment().getId();
            List<Group> groupsList = users.getGroupsList();
            List<Integer> groupIds = new ArrayList<>();
            for (Group group :
                    groupsList) {
                if (group == null) {
                    continue;
                }
                groupIds.add(group.getId());
            }
            Integer userId = users.getUser().getUserId();
            if (groupIds.size() == 0) {
                groupIds = null;
            }
            long count = documentMapper2.getSharedFilesCount(users.getUser().getIncid(), userId, groupIds, depId);
            resultCode.setData(String.valueOf(count));
        }catch (Exception e) {
            logger.error("获取与我共享列表失败", e);
            resultCode = ResultUtil.error(Messages.API_DOCUMENT_SHARESUB_CODE, Messages.API_DOCUMENT_SHARESUB_MSG);
        }
        return resultCode;
    }

    /**
     * 共享人删除自己的文档共享记录
     * @param request
     * @param incId
     * @param userInfoVO
     * @param shareIds
     * @return
     */
    @Override
    public Map<Long, List<DocumentPermissionVO>> deleteMyShare(HttpServletRequest request, Integer incId, UserInfoVO userInfoVO, String shareIds) {
        Map<Long, List<DocumentPermissionVO>> messageData = new HashMap<>();
        String[] ids = shareIds.split(",");
        for(String id: ids) {
            DocumentShareExample documentShareExample = new DocumentShareExample();
            DocumentShareExample.Criteria criteria = documentShareExample.createCriteria();
            criteria.andIncIdEqualTo(Long.parseLong(incId.toString()));
            criteria.andDocumentIdEqualTo(Long.parseLong(id));

            List<DocumentShare> documentShares = this.documentShareMapper.selectByExample(documentShareExample);
            // 查询我的共享后消息接收者
            List<DocumentPermissionVO> data = this.getShareRecordsByDocumentIdSimple(incId, userInfoVO, Long.parseLong(id)).getData();
            if(CollectionUtils.isNotEmpty(data)) {
                messageData.put(Long.parseLong(id), data);
            }

            for(DocumentShare documentShare: documentShares) {
                // 删除document_permission 表中share_id 和document_id为指定值的记录
                DocumentPermissionExample documentPermissionExample = new DocumentPermissionExample();
                DocumentPermissionExample.Criteria documentPermissionExampleCriteria = documentPermissionExample.createCriteria();
                documentPermissionExampleCriteria.andIncIdEqualTo(Long.parseLong(incId.toString()));
                documentPermissionExampleCriteria.andShareIdEqualTo(documentShare.getId());
                documentPermissionExampleCriteria.andDocumentIdEqualTo(Long.parseLong(id));
                this.documentPermissionMapper.deleteByExample(documentPermissionExample);
                // 删除 document_share表中document_id 为指定值的记录
                this.documentShareMapper.deleteByPrimaryKey(documentShare.getId());
            }
            // 将 document 表中 id 为指定值的记录的 ishare 修改为 0
            Document documentRecord = this.documentMapper.selectByPrimaryKey(Long.parseLong(id));
            documentRecord.setIsShare("0");
            this.documentMapper.updateByPrimaryKeySelective(documentRecord);
            // 记录操作日志
            this.addDocumentLog(request, documentRecord.getId().toString(), ActionType.DELETE_SHARE, documentRecord.getDocumentName(), new Date());
        }
        return messageData;
    }

    /**
     * 根据文档id获取文档共享记录
     * @param incId
     * @param userInfoVO
     * @param docId
     * @return
     */
    @Override
    public ResultCode<List<DocumentPermissionVO>> getShareRecordsByDocumentId(Integer incId, UserInfoVO userInfoVO, long docId) {
        ResultCode<List<DocumentPermissionVO>> result = new ResultCode<>();
        Map<String, Object> params = new HashMap<>();
        params.put("incId", incId);
        params.put("userId", userInfoVO.getUser().getUserId());
        params.put("docId", docId);
        List<DocumentPermissionVO> list = this.documentShareMapper2.getShareRecordsByDocumentId(params);

        // 设置接收者name
        List<String> userIds = new ArrayList<>();
        List<String> groupIds = new ArrayList<>();
        List<String> departmentIds = new ArrayList<>();
        if(list != null && list.size() > 0) {
            for(DocumentPermissionVO documentPermissionVO: list) {
                if(documentPermissionVO.get_type().equals("0")) {
                    userIds.add(documentPermissionVO.getId().toString());
                } else if(documentPermissionVO.get_type().equals("1")) {
                    groupIds.add(documentPermissionVO.getId().toString());
                } else if(documentPermissionVO.get_type().equals("2")) {
                    departmentIds.add(documentPermissionVO.getId().toString());
                }
            }
        }

        // 1.设置用户name
        if(userIds.size() > 0) {
            List<Users> users = this.usersApi.getUserDetails(userIds.toArray(new String[userIds.size()]), incId.toString()).getData();
            if(users != null && users.size() > 0) {
                for(Users user: users) {
                    for(DocumentPermissionVO documentPermissionVO: list) {
                        if(documentPermissionVO.get_type().equals("0") && documentPermissionVO.getId() == user.getUserId().longValue()) {
                            documentPermissionVO.setName(UserUtils.getFinalDisplayNameFromUser(user));
                        }
                    }
                }
            }

        }
        // 2.设置用户组name
        if(groupIds.size() > 0) {
            List<Group> groups = this.groupApi.groupDetails(groupIds.toArray(new String[groupIds.size()]), incId.toString()).getData();
            if(groups != null && groups.size() > 0) {
                for(Group group: groups) {
                    for(DocumentPermissionVO documentPermissionVO: list) {
                        if(documentPermissionVO.get_type().equals("1") && documentPermissionVO.getId() == group.getId().longValue()) {
                            documentPermissionVO.setName(group.getGroupName());
                        }
                    }
                }
            }
        }
        // 3.设置部门name
        if(departmentIds.size() > 0) {
            List<Department> departments = this.departmentApi.departmentDetails(departmentIds.toArray(new String[departmentIds.size()]), incId.toString()).getData();
            if(departments != null && departments.size() > 0) {
                for(Department department: departments) {
                    for(DocumentPermissionVO documentPermissionVO: list) {
                        if(documentPermissionVO.get_type().equals("2") && documentPermissionVO.getId() == department.getId().longValue()) {
                            documentPermissionVO.setName(department.getName());
                        }
                    }
                }
            }
        }
        result.setData(list);
        return result;
    }

    /**
     * 根据文档id获取文档共享记录(不包装被共享单位)
     * @param incId
     * @param userInfoVO
     * @param docId
     * @return
     */
    @Override
    public ResultCode<List<DocumentPermissionVO>> getShareRecordsByDocumentIdSimple(Integer incId, UserInfoVO userInfoVO, long docId) {
        ResultCode<List<DocumentPermissionVO>> result = new ResultCode<>();
        Map<String, Object> params = new HashMap<>();
        params.put("incId", incId);
        params.put("userId", userInfoVO.getUser().getUserId());
        params.put("docId", docId);
        List<DocumentPermissionVO> list = this.documentShareMapper2.getShareRecordsByDocumentId(params);
        result.setData(list);
        return result;
    }

    /**
     * 查询我的共享列表
     * @param incId
     * @param userInfoVO
     * @param parentId
     * @param documentName
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ResultCode<List<ShareAndLinkVO>> getMyShare(Integer incId, UserInfoVO userInfoVO, Long parentId, String documentName, Integer pageNum, Integer pageSize) {
        ResultCode<List<ShareAndLinkVO>> resultCode = new ResultCode<>();
        Integer userId = userInfoVO.getUser().getUserId();
        Map<String, Object> params = new HashMap<>();
        params.put("incId", incId);
        params.put("userId", userId);
        params.put("parentId", 0 == parentId ? null : parentId);
        params.put("documentName", StringUtils.isBlank(documentName) ? null : documentName);
        if(pageNum != null && pageNum > 0 && pageSize != null && pageSize > 0 ) {
            params.put("startNum", (pageNum - 1) * pageSize);
            params.put("pageSize", pageSize);
        } else {
            params.put("startNum", null);
            params.put("pageSize", null);
        }
        List<ShareAndLinkVO> shareList = this.documentShareMapper2.getShareVOList(params);
        resultCode.setData(shareList);
        return resultCode;
    }

    @Override
    public ResultCode<List<Document>> getAllChildren(UserInfoVO users, Long parentId) {
        ResultCode resultCode = new ResultCode<List<Document>>();
        try {
            Integer depId = users.getDepartment().getId();
            List<Group> groupsList = users.getGroupsList();
            List<Integer> groupIds = new ArrayList<>();
            for (Group group :
                    groupsList) {
                if (group == null) {
                    continue;
                }
                groupIds.add(group.getId());
            }
            Integer userId = users.getUser().getUserId();
            if (groupIds.size() == 0) {
                groupIds = null;
            }
            List<Document> documents = documentMapper2.getShareFileChildren(parentId == 0 ? null : String.valueOf(parentId),  users.getUser().getIncid(), userId, groupIds, depId);
            resultCode.setData(documents);
        }catch (Exception e) {
            logger.error("获取与我共享列表失败", e);
            resultCode = ResultUtil.error(Messages.API_DOCUMENT_SHARESUB_CODE, Messages.API_DOCUMENT_SHARESUB_MSG);
        }
        return resultCode;
    }

    @Override
    public DocumentShare getShareById(long shareId) {
        return this.documentShareMapper.selectByPrimaryKey(shareId);
    }

    @Override
    public Boolean getIsShareToMeByDocId(Integer incId, Integer userId, List<Long> groupIds, Integer deptId, Long docId) {
        Map<String, Object> params = new HashMap<>();
        params.put("incId", incId);
        params.put("userId", userId);
        params.put("groupIds", groupIds);
        params.put("deptId", deptId);
        params.put("docId", docId);
        Integer count_share = this.documentShareMapper2.isSharedToMe(params);
        if(count_share == null || count_share == 0) {
            return false;
        } else {
            Integer count_exclude = this.documentShareMapper2.isExcludeShare(incId, userId, docId);
            if(count_exclude != null && count_exclude > 0) {
                return false;
            }
        }
        return true;
    }
}
