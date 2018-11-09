package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.dal.dao.DocumentPermissionMapper;
import com.quarkdata.yunpan.api.dal.dao.LogMapper;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.DepartmentApi;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.UsersApi;
import com.quarkdata.yunpan.api.model.common.ActionType;
import com.quarkdata.yunpan.api.model.common.ListResult;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.common.YunPanApiException;
import com.quarkdata.yunpan.api.model.dataobj.DocumentPermission;
import com.quarkdata.yunpan.api.model.dataobj.DocumentPermissionExample;
import com.quarkdata.yunpan.api.service.AdminService;
import com.quarkdata.yunpan.api.util.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class AdminServiceImpl extends BaseLogServiceImpl implements AdminService {

    @Autowired
    private UsersApi usersApi;

    @Autowired
    private DepartmentApi departmentApi;

    @Autowired
    private DocumentPermissionMapper documentPermissionMapper;

    private LogMapper logMapper;

    @Resource
    public void setLogMapper(LogMapper logMapper) {
        this.logMapper = logMapper;
        super.setLogMapper(logMapper);
    }

    @Override
    public ResultCode<ListResult<UserInfoVO>> getAdminList(Integer incId, Integer pageNum, Integer pageSize, String username) {
        ResultCode<ListResult<UserInfoVO>> result = this.usersApi.getAdminList(incId, pageNum, pageSize, username);
        return result;
    }

    @Override
    public ResultCode<Object> addAdmin(HttpServletRequest request, Integer incId, String userIds, Long roleId) {
        ResultCode<Object> result = this.usersApi.addAdmin(incId, userIds, roleId);

        // 记录操作日志
        if(StringUtils.isNotBlank(userIds)) {
            for(String id : userIds.split(",")) {
                Users user = this.usersApi.getUserById(id).getData();
                if(1L == roleId) {
                    this.addOtherLog(request, ActionType.ADD_SYSTEM_ADMINISTRATOR, user.getUserName(), new Date());
                }
                if(2L == roleId) {
                    this.addOtherLog(request, ActionType.ADD_ORGANIZATION_ADMINISTRATOR, user.getUserName(), new Date());
                }
            }
        }

        return result;
    }

    @Override
    public ResultCode<Object> revokeSystemAdmin(HttpServletRequest request, Integer incId, Long systemAdminId) {
        ResultCode<Object> result = this.usersApi.revokeSystemAdmin(incId, systemAdminId);

        // 记录操作日志
        Users user = this.usersApi.getUserById(systemAdminId.toString()).getData();
        this.addOtherLog(request, ActionType.DELETE_THE_SYSTEM_ADMINISTRATOR, user.getUserName(), new Date());

        return result;
    }

    @Override
    public ResultCode<Object> revokeOrgAdmin(HttpServletRequest request, Long incId, Long orgAdminId, Long replacerId) throws YunPanApiException {
        ResultCode<Object> result = new ResultCode<>();
        // 1.修改文档权限记录表里面的receiver_id为新的组织管理员(管理权限)
        DocumentPermission record = new DocumentPermission();
        record.setReceiverId(replacerId);
        DocumentPermissionExample example = new DocumentPermissionExample();
        DocumentPermissionExample.Criteria criteria = example.createCriteria();
        criteria.andIncIdEqualTo(incId);
        criteria.andReceiverIdEqualTo(orgAdminId);
        //新的组织管理员继承原有组织管理员创建的组织文件
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("3");
        permissions.add("99");
        criteria.andPermissionIn(permissions);
        this.documentPermissionMapper.updateByExampleSelective(record, example);
        // 2.调用share-api
        result = this.usersApi.revokeOrgAdmin(incId, orgAdminId, replacerId);
        if(result.getCode() == 1) {
            throw new YunPanApiException(result.getCode(), result.getMsg());
        }

        // 3.记录操作日志
        Users oldAdmin = this.usersApi.getUserById(orgAdminId.toString()).getData();
        Users newAdmin = this.usersApi.getUserById(replacerId.toString()).getData();
        Date date = new Date();
        this.addOtherLog(request, ActionType.DELETE_THE_ORGANIZATION_ADMINISTRATOR, oldAdmin.getUserName(), date);
        this.addOtherLog(request, ActionType.ADD_ORGANIZATION_ADMINISTRATOR, newAdmin.getUserName(), date);

        return result;
    }

    @Override
    public ResultCode<Object> editSystemAdmin(HttpServletRequest request, Integer incId, Long systemAdminId) {
        ResultCode<Object> result = this.usersApi.editSystemAdmin(incId, systemAdminId);

        // 记录操作日志
        Users user = this.usersApi.getUserById(systemAdminId.toString()).getData();
        Date date = new Date();
        this.addOtherLog(request, ActionType.DELETE_THE_SYSTEM_ADMINISTRATOR, user.getUserName(), date);
        this.addOtherLog(request, ActionType.ADD_ORGANIZATION_ADMINISTRATOR, user.getUserName(), date);

        return result;
    }

    @Override
    public ResultCode<Object> editOrgAdmin(HttpServletRequest request, Long incId, Long orgAdminId, Long replacerId) throws YunPanApiException {
        ResultCode<Object> result = new ResultCode<>();
        // 1.修改文档权限记录表里面的receiver_id为新的组织管理员
        DocumentPermission record = new DocumentPermission();
        record.setReceiverId(replacerId);
        DocumentPermissionExample example = new DocumentPermissionExample();
        DocumentPermissionExample.Criteria criteria = example.createCriteria();
        criteria.andIncIdEqualTo(incId);
        criteria.andReceiverIdEqualTo(orgAdminId);
        //新的组织管理员继承原有组织管理员创建的组织文件
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("3");
        permissions.add("99");
        criteria.andPermissionIn(permissions);
        this.documentPermissionMapper.updateByExampleSelective(record, example);
        // 2.调用share-api
        result = this.usersApi.editOrgAdmin(incId, orgAdminId, replacerId);
        if(result.getCode() == 1) {
            throw new YunPanApiException(result.getCode(), result.getMsg());
        }

        // 3.记录操作日志
        Users orgAdmin = this.usersApi.getUserById(orgAdminId.toString()).getData();
        Users newOrgAdmin = this.usersApi.getUserById(replacerId.toString()).getData();
        Date date = new Date();
        this.addOtherLog(request, ActionType.DELETE_THE_ORGANIZATION_ADMINISTRATOR, orgAdmin.getUserName(), date);
        this.addOtherLog(request, ActionType.ADD_SYSTEM_ADMINISTRATOR, orgAdmin.getUserName(), date);
        this.addOtherLog(request, ActionType.ADD_ORGANIZATION_ADMINISTRATOR, newOrgAdmin.getUserName(), date);

        return result;
    }

    @Override
    public ResultCode<List<Users>> findUserByUserName(Integer incId, String userName, Integer pageNum, Integer pageSize) {
        ResultCode<List<Users>> result = this.usersApi.findUserByUserName(incId, userName, pageNum, pageSize);
        if(result != null && CollectionUtils.isNotEmpty(result.getData())) {
            for(Users u: result.getData()) {
                u.setUserName(UserUtils.getFinalUserNameFromUser(u));
            }
        }
        return result;
    }

}
