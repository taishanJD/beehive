package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.quark.share.model.dataobj.Department;
import com.quarkdata.quark.share.model.vo.DepartmentSearchVO;
import com.quarkdata.quark.share.model.vo.DepartmentVO;
import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.vo.MemberInfoVO;
import com.quarkdata.yunpan.api.service.DepartmentService;
import com.quarkdata.yunpan.api.util.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @Author hanhy0720@thundersoft.com
 * @Description  用户组
 *
 */
@Controller
@RequestMapping(RouteKey.PREFIX_API + "/department")
public class DepartmentController extends BaseController{

    @Autowired
    private DepartmentService departmentService;

    /**
     * 根据条件查询部门结构树
     * @param name
     * @param parentId
     * @return
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ResponseBody
    public ResultCode<List<DepartmentVO>> getDepartmentList(@RequestParam(value="name",defaultValue = "") String name,
                                                @RequestParam(value="parentId",defaultValue = "") String parentId){

        ResultCode<List<DepartmentVO>> result = new ResultCode<List<DepartmentVO>>();
        try {
            // 当前企业ID
            Integer incId = UserInfoUtil.getIncorporation().getId();

            //调用service层方法先获取符条件的父节点集合
            result = departmentService.getParentDepart(incId,name,parentId);
            //获取结果中的所有父节点
            List<DepartmentVO> parentDepartment=result.getData();
            if(parentDepartment != null) {
                for (DepartmentVO dep:parentDepartment) {
                    findSubDepartment(dep);
                }
            }
        } catch (Exception e) {
            result.setCode(1);
            result.setMsg(Messages.FIND_DEPARTMENT_FAIL_MSG);
            this.logger.error(Messages.FIND_DEPARTMENT_FAIL_MSG, e);
        }
        return result;
    }

    //查找子部门
    public void findSubDepartment(DepartmentVO departmentVO){
        Integer deptId=null;
        if (departmentVO!=null){
            deptId=departmentVO.getId();
        }
        //根据deptId查询子部门
        ResultCode<List<DepartmentVO>> result = new ResultCode<List<DepartmentVO>>();
        // 当前企业ID
        Integer incId = UserInfoUtil.getIncorporation().getId();

        //查询父节点为deptId的子节点
        try {
            result = departmentService.getParentDepart(incId,null,deptId.toString());
        } catch (InvocationTargetException e) {
            result.setData(null);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            result.setData(null);
            e.printStackTrace();
        }
        List<DepartmentVO> subDepartmentList=result.getData();
        //赋值子节点
        departmentVO.setChildren(subDepartmentList);
        if (subDepartmentList==null || subDepartmentList.size()<=0){
            departmentVO.setLastNode(true);
        }else {
            departmentVO.setLastNode(false);
        }
        //判断是不是叶子节点，如果是，则退出递归
        if (departmentVO.isLastNode()==true){
            return;
        }
        //递归子节点
        for (DepartmentVO depart:subDepartmentList) {
            findSubDepartment(depart);
        }
    }


    /**
     * 根据条件查询所有部门list
     * @param name
     * @param parentId
     * @return
     */
    @RequestMapping(value = "/all",method = RequestMethod.GET)
    @ResponseBody
    public ResultCode<List<Department>> getAllDepartment(@RequestParam(value="name",defaultValue = "") String name,
                                                            @RequestParam(value="parentId",defaultValue = "") String parentId){

        ResultCode<List<Department>> result = new ResultCode<List<Department>>();
        try {
            // 当前企业ID
            Integer incId = UserInfoUtil.getIncorporation().getId();
            //调用service层方法先获取符条件的父节点集合
            result = departmentService.getAllDepartment(incId,name,parentId);
        } catch (Exception e) {
            result.setCode(1);
            result.setMsg(Messages.FIND_DEPARTMENT_FAIL_MSG);
            this.logger.error(Messages.FIND_DEPARTMENT_FAIL_MSG, e);
        }
        return result;
    }

    /**
     * 查询企业内部门列表
     * @param name
     * @return
     */
    @RequestMapping(value = "/get_department_list",method = RequestMethod.GET)
    @ResponseBody
    public ResultCode<List<DepartmentSearchVO>> getDepartmentListStructureLessness(@RequestParam(value="name",defaultValue = "") String name){

        ResultCode<List<DepartmentSearchVO>> result = new ResultCode<List<DepartmentSearchVO>>();
        try {
            Integer incId = UserInfoUtil.getIncorporation().getId();
            result = departmentService.getDepartmentListStructureLessness(incId,name);
        } catch (Exception e) {
            result.setCode(1);
            result.setMsg(Messages.FIND_DEPARTMENT_FAIL_MSG);
            this.logger.error(Messages.FIND_DEPARTMENT_FAIL_MSG, e);
        }
        return result;
    }

    /**
     * 根据parentId查询部门
     * @param name
     * @param parentId
     * @return
     */
    @RequestMapping(value = "/get_dept_by_parentId")
    @ResponseBody
    public ResultCode<List<Department>> getDepartmentByParentId(@RequestParam(value="name",defaultValue = "") String name,
                                                         @RequestParam(value="parentId",defaultValue = "") String parentId){

        ResultCode<List<Department>> result = new ResultCode<>();
        if(StringUtils.isBlank(parentId) || "0".equals(parentId)) {
            parentId = null;
        }
        try {
            // 当前企业ID
            Integer incId = UserInfoUtil.getIncorporation().getId();
            List<Department> departments = departmentService.getDepartmentByParentId(incId, parentId, name);
            if(CollectionUtils.isNotEmpty(departments)) {
                result.setData(departments);
            }
        } catch (Exception e) {
            result.setCode(1);
            result.setMsg(Messages.FIND_DEPARTMENT_FAIL_MSG);
            this.logger.error(Messages.FIND_DEPARTMENT_FAIL_MSG, e);
        }
        return result;
    }

    @RequestMapping(value = "/getChildren",method = RequestMethod.GET)
    @ResponseBody
    public ResultCode<MemberInfoVO> getAllChildren(@RequestParam(value="name",defaultValue = "") String name,
                                                           @RequestParam(value="parentId",defaultValue = "") String parentId){

        ResultCode<MemberInfoVO> result = new ResultCode<>();
        try {
            // 当前企业ID
            Integer incId = UserInfoUtil.getIncorporation().getId();
            //调用service层方法先获取符条件的父节点集合
            result = departmentService.getAllChildren(incId,name,parentId);
        } catch (Exception e) {
            result.setCode(1);
            result.setMsg(Messages.FIND_DEPARTMENT_FAIL_MSG);
            this.logger.error(Messages.FIND_DEPARTMENT_FAIL_MSG, e);
        }
        return result;
    }
}
