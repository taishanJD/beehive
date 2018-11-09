package com.quarkdata.yunpan.api.service;

import com.quarkdata.quark.share.model.dataobj.Department;
import com.quarkdata.quark.share.model.vo.DepartmentSearchVO;
import com.quarkdata.quark.share.model.vo.DepartmentVO;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.vo.MemberInfoVO;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @Author hanhy0720@thundersoft.com
 * @Description 用户组
 *
 */
public interface DepartmentService {

    /**
     * 根据条件查询部门结构树
     * @param incId
     * @param name
     * @param parentId
     * @return
     */
    public ResultCode<List<DepartmentVO>> getParentDepart(Integer incId, String name, String parentId) throws InvocationTargetException, IllegalAccessException;

    /**
     * 根据父部门id和部门名称模糊查询所有的部门数据
     * @param incId
     * @param name
     * @param parentId
     * @return
     */
    public ResultCode<List<Department>> getAllDepartment(Integer incId, String name, String parentId);

    /**
     * 根据父部门id查询部门下所有的数据
     * @param incId
     * @param name
     * @param parentId
     * @return
     */
    public ResultCode<MemberInfoVO> getAllChildren(Integer incId, String name, String parentId);

    /**
     * 根据parentId查询部门
     * @param incId
     * @param parentId
     * @param name
     * @return
     */
    List<Department> getDepartmentByParentId(Integer incId, String parentId, String name);

    /**
     * 查询企业内部门列表
     * @param incId
     * @param name
     * @return
     */
    ResultCode<List<DepartmentSearchVO>> getDepartmentListStructureLessness(Integer incId, String name);
}
