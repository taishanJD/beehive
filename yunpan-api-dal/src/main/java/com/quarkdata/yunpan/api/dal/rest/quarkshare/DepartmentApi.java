package com.quarkdata.yunpan.api.dal.rest.quarkshare;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.quarkdata.quark.share.model.dataobj.Department;
import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.util.HttpTookit;
import com.quarkdata.yunpan.api.util.common.persistence.Page;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author hanhy0720@thundersoft.com
 * @Description 用户组相关api
 *
 */
@Repository
public class DepartmentApi {
    private static Logger logger = Logger.getLogger(Department.class);


    /**
     *  根据父节点id和name查询部门结构树
     * @param incId
     * @param name
     * @param parentId
     * @return
     */
    public ResultCode<List<Department>> getDepartmentList(Integer incId, String name, String parentId){
        String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.department_list;
        Map<String, String> params = new HashMap<>();
        params.put("incId", String.valueOf(incId));
        params.put("deptName",name);
        params.put("parentId",parentId);

        String doPost = HttpTookit.doPost(url, params);
        logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPost);
        ResultCode<List<Department>> result = JSON.parseObject(doPost,
                new TypeReference<ResultCode<List<Department>>>() {
                });
        return result;
    }

    /**
     *  根据父节点id和name查询部门结构树
     * @param incId
     * @param name
     * @param parentId
     * @return
     */
    public ResultCode<List<Department>> getAllDepartment(Integer incId,String name,String parentId){
        String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.department_list;
        Map<String, String> params = new HashMap<>();
        params.put("incId", String.valueOf(incId));
        params.put("deptName",name);
        params.put("parentId", parentId);
        String doPost = HttpTookit.doPost(url, params);
        logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPost);
        ResultCode<List<Department>> result = JSON.parseObject(doPost,
                new TypeReference<ResultCode<List<Department>>>() {
                });
        return result;
    }
    public ResultCode<List<Department>> departmentDetails(String[] depId,String incId){
    	String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.department_details;
    	Map<String, String[]> params = new HashMap<>();
		params.put("deptIds", depId);
		String doPostForParamsArray = HttpTookit.doPostForParamsArray(url, params );
    	logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPostForParamsArray);
    	ResultCode<List<Department>> result=JSON.parseObject(doPostForParamsArray, new TypeReference<ResultCode<List<Department>>>() {
		});
    	return result;
    }

    public List<Integer> getDeptAllUserId(String deptId){
        Map<String, String> params = new HashMap<>();
        params.put("deptId",deptId);
        String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.dept_userlist;
        String doGet = HttpTookit.doGet(url, params);
        logger.info("request url : " + url + " ,params : " + params + " ,result : " + doGet);
        ResultCode<Page<Users>> result = JSON.parseObject(doGet,
                new TypeReference<ResultCode<Page<Users>>>() {
                });
        List<Integer> list = new ArrayList<>();
        if(result.getData().getList() != null && result.getData().getList().size() > 0) {
            for (Users user: result.getData().getList()) {
                list.add(user.getUserId());
            }
        }
        return list;

    }

    public List<Department> getDeptmentsByDeptId(Integer incId, String name, String parentId){
        ResultCode<List<Department>> departs = this.getAllDepartment(incId, name, parentId);
        return departs.getData();
    }

    /**
     * 根据用户id查询用户所在部门
     * @param userId
     * @return
     */
    public Department getDepartmentByUserId(Long userId) {
        String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.get_department_by_user_id;
        Map<String, String> params = new HashMap<>();
        params.put("deptId",String.valueOf(userId));
        String doPost = HttpTookit.doPost(url, params);
        logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPost);
        ResultCode<Department> httpResult = JSON.parseObject(doPost,
                new TypeReference<ResultCode<Department>>() {
                });
        return httpResult.getData();
    }

    /**
     * 查询企业内所有部门列表
     * @param incId
     * @param name
     * @return
     */
    public List<Department> getDepartmentListStructureLessness(Integer incId, String name) {
        String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.department_list_no_structure;
        Map<String, String> params = new HashMap<>();
        params.put("incId", String.valueOf(incId));
        params.put("deptName",name);

        String doPost = HttpTookit.doPost(url, params);
        logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPost);
        ResultCode<Page<Department>> result = JSON.parseObject(doPost,
                new TypeReference<ResultCode<Page<Department>>>() {
                });

        return result.getData().getList();
    }


    /**
     * 根据id查询部门数据
     * @param id
     * @return
     */
    public Department getDepartmentById(Integer id) {
        String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.get_department_by_id;
        Map<String, String> params = new HashMap<>();
        params.put("deptId", String.valueOf(id));

        String doPost = HttpTookit.doPost(url, params);
        logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPost);
        ResultCode<Department> result = JSON.parseObject(doPost,
                new TypeReference<ResultCode<Department>>() {
                });

        return result.getData();
    }

}
