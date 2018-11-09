package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.quark.share.model.dataobj.Department;
import com.quarkdata.quark.share.model.dataobj.Group;
import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.quark.share.model.vo.DepartmentSearchVO;
import com.quarkdata.quark.share.model.vo.DepartmentVO;
import com.quarkdata.quark.share.model.vo.GroupSearchVO;
import com.quarkdata.quark.share.model.vo.UserSearchVO;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.DepartmentApi;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.GroupApi;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.UsersApi;
import com.quarkdata.yunpan.api.model.common.Constants;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.vo.MemberInfoVO;
import com.quarkdata.yunpan.api.service.DepartmentService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class DepartmentServiceImpl implements DepartmentService{

    @Autowired
    private DepartmentApi departmentApi;
    @Autowired
    private GroupApi groupApi;
    @Autowired
    private UsersApi usersApi;

    @Override
    public ResultCode<List<DepartmentVO>> getParentDepart(Integer incId, String name, String parentId) throws InvocationTargetException, IllegalAccessException {
        ResultCode<List<DepartmentVO>> resultCode = new ResultCode<>();
        List<DepartmentVO> data = new ArrayList<>();
        Department rootDepartment = null;
        ResultCode<List<Department>> departmentListResult = departmentApi.getDepartmentList(incId, name, parentId);
        if(departmentListResult.getData() != null && departmentListResult.getData().size() > 0) {
            for(Department department: departmentListResult.getData()) {
                if(!StringUtils.equals(department.getCode(),"999999")) {
                    DepartmentVO departmentVO = new DepartmentVO();
                    BeanUtils.copyProperties(department, departmentVO);
                    departmentVO.setChildren(null);
                    departmentVO.setLastNode(false);
                    departmentVO.set_type(Constants.TYPE_DEPARTMENT);
                    data.add(departmentVO);
                }else {
                    rootDepartment = department;
                }
            }
            if(rootDepartment != null){
                ResultCode<List<Department>> underRootDeptment = departmentApi.getDepartmentList(incId, name, rootDepartment.getId().toString());
                for(Department department: underRootDeptment.getData()) {
                    DepartmentVO departmentVO = new DepartmentVO();
                    BeanUtils.copyProperties(department, departmentVO);
                    departmentVO.setChildren(null);
                    departmentVO.setLastNode(false);
                    departmentVO.set_type(Constants.TYPE_DEPARTMENT);
                    data.add(departmentVO);
                }
            }
            resultCode.setCode(departmentListResult.getCode());
            resultCode.setMsg(departmentListResult.getMsg());
            resultCode.setData(data);
        }
        return resultCode;
    }

    @Override
    public ResultCode<List<Department>> getAllDepartment(Integer incId, String name, String parentId) {
        ResultCode<List<Department>> result=departmentApi.getAllDepartment(incId,name,parentId);
        return result;
    }

    @Override
    public ResultCode<MemberInfoVO> getAllChildren(Integer incId, String name, String parentId) {
        ResultCode<MemberInfoVO> result = new ResultCode<>();
        if(StringUtils.isNotBlank(parentId) && "0".equals(parentId)) {
            parentId = "";
        }
        // 部门
        List<Department> departments = departmentApi.getDeptmentsByDeptId(incId,name,parentId);
        List<DepartmentSearchVO> newDepartments = null;
        if(CollectionUtils.isNotEmpty(departments)) {
            newDepartments = new ArrayList<>();
            Department rootDepartment = null;
            for(Department department: departments) {
                if(!StringUtils.equals(department.getCode(),"999999")) {
                    DepartmentSearchVO departmentSearchVO = new DepartmentSearchVO();
                    BeanUtils.copyProperties(department, departmentSearchVO);
                    departmentSearchVO.set_type(Constants.TYPE_DEPARTMENT);
                    newDepartments.add(departmentSearchVO);
                }else {
                    rootDepartment = department;
                }
            }
            if(rootDepartment != null){
                String rootId = rootDepartment.getId().toString();
                List<Department> deptUnderRoot = departmentApi.getDeptmentsByDeptId(incId,name,rootId);
                if(CollectionUtils.isNotEmpty(departments)) {

                    for (Department department : deptUnderRoot) {
                        if (!StringUtils.equals(department.getCode(), "999999")) {
                            DepartmentSearchVO departmentSearchVO = new DepartmentSearchVO();
                            BeanUtils.copyProperties(department, departmentSearchVO);
                            departmentSearchVO.set_type(Constants.TYPE_DEPARTMENT);
                            newDepartments.add(departmentSearchVO);
                        }
                    }
                }
            }
        }
        // 用户组
        List<Group> groups = groupApi.getGroupByDeptId(incId,parentId, name);
        List<GroupSearchVO> newGroups = null;
        if(CollectionUtils.isNotEmpty(groups)) {
            newGroups = new ArrayList<>();
            for(Group g: groups) {
                GroupSearchVO groupSearchVO = new GroupSearchVO();
                BeanUtils.copyProperties(g, groupSearchVO);
                groupSearchVO.set_type(Constants.TYPE_GROUP);
                newGroups.add(groupSearchVO);
            }
        }
        // 用户
        List<Users> users = usersApi.getUsersByDeptId(incId,parentId, name);
        List<UserSearchVO> newUsers = null;
        if(CollectionUtils.isNotEmpty(users)) {
            newUsers = new ArrayList<>();
            for(Users u: users) {
                UserSearchVO userSearchVO = new UserSearchVO();
                BeanUtils.copyProperties(u, userSearchVO);
                userSearchVO.setId(u.getUserId().longValue());
                userSearchVO.set_type(Constants.TYPE_USER);
                newUsers.add(userSearchVO);
            }
        }
        MemberInfoVO temp = new MemberInfoVO(newUsers, newGroups, newDepartments);
        result.setData(temp);
        return  result;
    }

    @Override
    public List<Department> getDepartmentByParentId(Integer incId, String parentId, String name) {
        List<Department> result = this.departmentApi.getDeptmentsByDeptId(incId, name, parentId);
        Department rootDept = null;
        for(int i = 0 ;i < result.size(); i++){
            if(StringUtils.equals(result.get(i).getCode(), "999999")){
                rootDept = result.remove(i);
                break;
            }
        }
        if(rootDept != null){
            List<Department> deptUnderRoot = this.departmentApi.getDeptmentsByDeptId(incId, name, rootDept.getId().toString());
            result.addAll(deptUnderRoot);
        }
        return result;
    }

    @Override
    public ResultCode<List<DepartmentSearchVO>> getDepartmentListStructureLessness(Integer incId, String name) {
        ResultCode<List<DepartmentSearchVO>> resultCode = new ResultCode<>();
        List<DepartmentSearchVO> departmentSearchVOList = new ArrayList<>();
        List<Department> result=departmentApi.getDepartmentListStructureLessness(incId,name);
        if(CollectionUtils.isNotEmpty(result)) {
            for(Department department: result) {
                if(!StringUtils.equals(department.getCode(),"999999")) {
                    DepartmentSearchVO departmentSearchVO = new DepartmentSearchVO();
                    BeanUtils.copyProperties(department, departmentSearchVO);
                    departmentSearchVO.set_type(Constants.TYPE_DEPARTMENT);
                    departmentSearchVOList.add(departmentSearchVO);
                }
            }
        }

        resultCode.setData(departmentSearchVOList);
        return resultCode;
    }
}
