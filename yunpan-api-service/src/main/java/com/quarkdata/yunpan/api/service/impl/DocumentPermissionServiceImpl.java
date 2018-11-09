package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.dal.dao.DocumentMapper;
import com.quarkdata.yunpan.api.dal.dao.DocumentPermissionMapper;
import com.quarkdata.yunpan.api.dal.dao.DocumentPermissionMapper2;
import com.quarkdata.yunpan.api.model.common.DocumentConstants;
import com.quarkdata.yunpan.api.model.common.DocumentPermissionConstants;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.RoleConstants;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.dataobj.DocumentPermission;
import com.quarkdata.yunpan.api.model.dataobj.DocumentPermissionExample;
import com.quarkdata.yunpan.api.model.dataobj.DocumentPermissionExample.Criteria;
import com.quarkdata.yunpan.api.service.DocumentPermissionService;
import com.quarkdata.yunpan.api.util.common.Exception.YCException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class DocumentPermissionServiceImpl implements DocumentPermissionService {

    @Autowired
    private DocumentPermissionMapper documentPermissionMapper;

    @Autowired
    private DocumentPermissionMapper2 documentPermissionMapper2;

    @Autowired
    private DocumentMapper documentMapper;


    @Override
    public String getPermission(String idPath, Long userId, List<Long> userGroupIds, Long deptId) {

        if(RoleConstants.SYSTEM_ADMIN_ROLE_ID.equals(UserInfoUtil.getUserInfoVO().getRole().getId())) {
            return DocumentConstants.DOCUMENT_PERMISSION_CREATE;
        }

        // 如果是组织空间根目录下的文件(非文件夹),只对文件上传者有权限
        List<String> idList = new ArrayList<>();
        String[] idArr = idPath.split("/");
        for(String id: idArr) {
            if(StringUtils.isNotBlank(id)) {
                idList.add(id);
            }
        }
        if(idList.size() == 1) {
            Document document = this.documentMapper.selectByPrimaryKey(Long.valueOf(idList.get(0)));
            if(!document.getDocumentType().equals(DocumentConstants.DOCUMENT_DOCUMENT_TYPE_DIR)) {
                if(document.getCreateUser().equals(userId)) {
                    return DocumentConstants.DOCUMENT_PERMISSION_CREATE;
                } else {
                    return null;
                }
            }
        }

        String organizedSpaceId = StringUtils.split(idPath, "/")[0];
        List<String> permissionList = new ArrayList<>();
        permissionList.add(DocumentConstants.DOCUMENT_PERMISSION_SUPER_MANAGE);
        permissionList.add(DocumentConstants.DOCUMENT_PERMISSION_CREATE);
        DocumentPermissionExample documentPermissionExample = new DocumentPermissionExample();
        documentPermissionExample.createCriteria()
                .andIncIdEqualTo(UserInfoUtil.getIncorporation().getId().longValue())
                .andDocumentIdEqualTo(Long.parseLong(organizedSpaceId))
                .andReceiverTypeEqualTo(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER)
                .andReceiverIdEqualTo(userId)
                .andPermissionIn(permissionList);
        List<DocumentPermission> permissionsOfSuperManage = this.documentPermissionMapper.selectByExample(documentPermissionExample);
        if(CollectionUtils.isNotEmpty(permissionsOfSuperManage)) {
            for(DocumentPermission documentPermission: permissionsOfSuperManage) {
                if(DocumentConstants.DOCUMENT_PERMISSION_SUPER_MANAGE.equals(documentPermission.getPermission())) {
                    return DocumentConstants.DOCUMENT_PERMISSION_SUPER_MANAGE;
                }
                if(DocumentConstants.DOCUMENT_PERMISSION_CREATE.equals(documentPermission.getPermission())) {
                    return DocumentConstants.DOCUMENT_PERMISSION_CREATE;
                }
            }
        }

        String[] docIds = StringUtils.split(idPath, "/");
        String retPermission = null;

        //将路径层级反向
        ArrayUtils.reverse(docIds);
        if (ArrayUtils.isNotEmpty(docIds)) {
            for (String docIdStr : docIds) {
                //查找当前文档的权限设置
                long docId = Long.parseLong(docIdStr);
                DocumentPermissionExample example = new DocumentPermissionExample();
                Criteria criteria = example.createCriteria();
                criteria.andDocumentIdEqualTo(docId);

                List<DocumentPermission> permissions = documentPermissionMapper.selectByExample(example);

                // 判断是否是真正单独设置了权限
                List<String> realPermissions = new ArrayList<>();
                if(CollectionUtils.isNotEmpty(permissions)) {
                    for(DocumentPermission permission : permissions) {
                        realPermissions.add(permission.getPermission());
                    }
                }
                if(!(realPermissions.contains("0") || realPermissions.contains("1") ||
                        realPermissions.contains("2") || realPermissions.contains("3"))) {
                    permissions = null;
                }

                //如果当前文档没有单独设置的权限，则继续查找父级的权限
                //否则，往下处理
                if (CollectionUtils.isNotEmpty(permissions)) {
                    String currentMaxPermission = null;
                    for (DocumentPermission permission : permissions) {
                        String receiverType = permission.getReceiverType();
                        Long receiverId = permission.getReceiverId();
                        String currentPermission = permission.getPermission();

                        //以用户权限为准，用户拥有权限直接返回
                        if ((receiverType.equals(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER) && receiverId.equals(userId))){
                            currentMaxPermission = currentPermission;
                            break;
                        }
                        //三种授权方式： 用户、用户组、部门
                        if ((receiverType.equals(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER) && receiverId.equals(userId))
                                || (receiverType.equals(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_GROUP) && userGroupIds.contains(receiverId))
                                || (receiverType.equals(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_DEPT) && receiverId.equals(deptId))
                                || (receiverType.equals(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_GENERATE_ACCOUNT) && receiverId.equals(userId))) {
                            //取得用户拥有的当前文档的最大权限
                            if (currentMaxPermission == null || currentMaxPermission.compareTo(currentPermission) < 0) {
                                currentMaxPermission = currentPermission;
                            }
                        }
                    }

                    //在文档（和祖先文档）单独设置了权限的情况下，用户没权限，说明用户对文档没权限
                    if (currentMaxPermission == null) {
                        retPermission = null;
                        break;
                    }

                    //在文档（和祖先文档）单独设置了权限的情况下，用户有权限
                    //此时如果需要返回的权限没有初始化过，则设置为当前层级的权限，否则保持原有权限
                    if (retPermission == null) {
                        retPermission = currentMaxPermission;
                    }
                }
                //继续循环判断各祖先文档的权限
            }
        }

        return retPermission;
    }

    @Override
    public void save(DocumentPermission documentPermission) {
        documentPermissionMapper2.insert(documentPermission);
    }


    @Override
    public DocumentPermission getDocPermissionByRidAndType(Long rId, String type) {
        return documentPermissionMapper2.selectByRidAndReceiveType(rId,type);
    }

    @Override
    public List<DocumentPermission> getDirectPermission(Long incId, Long receiverId, List<Long> dpGroupIds, Long deptId, Long docId) {
        List<Integer> groupIds = new ArrayList<>();
        if(dpGroupIds != null && dpGroupIds.size() > 0) {
            for(Long groupId: dpGroupIds) {
                groupIds.add(Integer.parseInt(groupId.toString()));
            }
        }
        return documentPermissionMapper2.getPermission(Integer.parseInt(receiverId.toString()), groupIds, deptId == null ? null : Integer.parseInt(deptId.toString()),
                Integer.parseInt(incId.toString()), docId);
    }

    @Override
    public Boolean hasPermission(Long incId, Long userId, List<Long> groupIds, Long deptId, Long docId, String permissionIndex) {
        Document document = this.documentMapper.selectByPrimaryKey(docId);
        if(document != null) {
            if(DocumentConstants.DOCUMENT_TYPE_PERSONAL.equals(document.getType())) {
                return document.getCreateUser().equals(userId);
            } else {
                if(UserInfoUtil.isSystemAdmin()) {
                    return true;
                }
                String has_permission = this.documentPermissionMapper2.hasPermission(incId, userId, groupIds, deptId, docId, permissionIndex);
                return DocumentPermissionConstants.HAS_PERMISSION.equals(has_permission);
            }
        } else {
            if(DocumentConstants.DOCUMENT_ROOT_PARENTID.equals(docId)) {
                if(UserInfoUtil.isAdmin()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                throw new YCException(Messages.FILE_NOT_EXIST_MSG, Messages.FILE_NOT_EXIST_CODE);
            }
        }
    }

    @Override
    public String getPermissionByDocumentId(Long docId, Long incId, Long userId, List<Long> userGroupIds, Long deptId, boolean systemAdmin) {
        if(DocumentConstants.DOCUMENT_ROOT_PARENTID.equals(docId)) {
            if(UserInfoUtil.isAdmin()) {
                return DocumentPermissionConstants.PermissionRole.ADMIN_OR_CREATOR;
            } else {
                return DocumentPermissionConstants.PermissionRole.ASSOCIATED_VISIBLE;
            }
        } else {
            if(systemAdmin) {
                return DocumentPermissionConstants.PermissionRole.ADMIN_OR_CREATOR;
            } else {
                return this.documentPermissionMapper2.getPermissionByDocumentId(docId, incId, userId, userGroupIds, deptId);
            }
        }
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public void deleteByReceiveId(Long receiveId) {
        documentPermissionMapper2.deleteByReceiveId(receiveId);
    }
}
