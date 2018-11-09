package com.quarkdata.yunpan.api.dal.rest.quarkshare;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.quarkdata.quark.share.model.dataobj.Group;
import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.yunpan.api.model.common.ListResult;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.util.HttpTookit;

import com.quarkdata.yunpan.api.util.StringUtils;
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
public class GroupApi {
    private static Logger logger = Logger.getLogger(Group.class);

    public ResultCode<ListResult<Group>> getGroupList(Integer incId, String groupName, Integer pageNum, Integer pageSize) {
        String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.group_list;
        Map<String, String> params = new HashMap<>();
        params.put("incId", String.valueOf(incId));
        params.put("groupName",groupName);
        params.put("pageNum", pageNum == null ? null : pageNum.toString());
        params.put("pageSize", pageSize == null ? null : pageSize.toString());
        String doGet = HttpTookit.doGet(url, params);
        logger.info("request url : " + url + " ,params : " + params + " ,result : " + doGet);
        ResultCode<ListResult<Group>> result = JSON.parseObject(doGet,
                new TypeReference<ResultCode<ListResult<Group>>>() {
                });
        return result;
    }
    public ResultCode<List<Group>> groupDetails(String[] groupId,String incId){
    	Map<String, String[]> params = new HashMap<>();
		params.put("groupIds", groupId);
    	String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.group_details;
    	String doPostForParamsArray = HttpTookit.doPostForParamsArray(url, params );
    	logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPostForParamsArray);
    	ResultCode<List<Group>> result=JSON.parseObject(doPostForParamsArray, new TypeReference<ResultCode<List<Group>>>() {
		});
    	return result;
    }

    public List<Integer> getGroupAllUserId(String incId,String groupId){
        Map<String, String> params = new HashMap<>();
        params.put("groupId",groupId);
        String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.group_userlist;
        String doPost = HttpTookit.doPost(url, params);
        logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPost);
        ResultCode<Page<Users>> result = JSON.parseObject(doPost,
                new TypeReference<ResultCode<Page<Users>>>() {
                });
        List<Integer> userIds = new ArrayList<>();
        if(result.getData().getList() != null && result.getData().getList().size() > 0) {
            for(Users user: result.getData().getList()) {
                userIds.add(user.getUserId());
            }
        }
        return userIds;

    }

    public List<Group> getGroupByDeptId(Integer incId, String deptId, String name) {
        String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.group_list_by_dept_id;
        Map<String, String> params = new HashMap<>();
        params.put("incId", String.valueOf(incId));
        params.put("deptId",String.valueOf(deptId));
        params.put("groupName",String.valueOf(name));
        String doPost = HttpTookit.doPost(url, params);
        logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPost);
        ResultCode<Page<Group>> httpResult = JSON.parseObject(doPost,
                new TypeReference<ResultCode<Page<Group>>>() {
                });
        List<Group> result = new ArrayList<>();
        if(httpResult.getData() != null) {
            result = httpResult.getData().getList();
        }
        return result;
    }

    public List<Group> getGroupByGroupId(Integer incId, String groupId, String groupName, Integer pageNum, Integer pageSize) {
        String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.group_list_by_group_id;
        Map<String, String> params = new HashMap<>();
        params.put("incId", String.valueOf(incId));
        if(StringUtils.isNotBlank(groupId)) {
            params.put("groupId", String.valueOf(groupId));
        }
        params.put("groupName",groupName);
        params.put("pageNum",pageNum == null ? null : pageNum.toString());
        params.put("pageSize",pageSize == null ? null : pageSize.toString());
        String doPost = HttpTookit.doPost(url, params);
        logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPost);
        ResultCode<Page<Group>> httpResult = JSON.parseObject(doPost,
                new TypeReference<ResultCode<Page<Group>>>() {
                });
        List<Group> result = new ArrayList<>();
        if(httpResult.getData() != null) {
            result = httpResult.getData().getList();
        }
        return result;
    }

    public List<Group> getGroupBySources(Integer incId, String source) {
        String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.get_group_list_by_inc_id_and_source;
        Map<String, String> params = new HashMap<>();
        params.put("incId", String.valueOf(incId));
        params.put("source",source);
        String doPost = HttpTookit.doPost(url, params);
        logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPost);
        ResultCode<List<Group>> httpResult = JSON.parseObject(doPost,
                new TypeReference<ResultCode<List<Group>>>() {
                });
        List<Group> result = new ArrayList<>();
        if(httpResult.getData() != null) {
            result = httpResult.getData();
        }
        return result;
    }

    /**
     * 根据用户id获取用户所在组
     * @param userId
     * @return
     */
    public List<Group> getGroupByUserId(Long userId) {
        String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.group_list_by_user_id;
        Map<String, String> params = new HashMap<>();
        params.put("userId",String.valueOf(userId));
        String doPost = HttpTookit.doPost(url, params);
        logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPost);
        ResultCode<List<Group>> httpResult = JSON.parseObject(doPost,
                new TypeReference<ResultCode<List<Group>>>() {
                });
        return httpResult.getData();
    }

    /**
     * 根据用户组ID查询用户组
     * @param incId
     * @param id
     * @return
     */
    public Group getGroupById(Integer incId, String id) {
        String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.group_By_id;
        Map<String, String> params = new HashMap<>();
        params.put("incId", incId.toString());
        params.put("groupId", id);
        String doPost = HttpTookit.doPost(url, params);
        logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPost);
        ResultCode<Group> httpResult = JSON.parseObject(doPost,
                new TypeReference<ResultCode<Group>>() {
                });
        return httpResult.getData();
    }

    public ResultCode<ListResult<Group>> getGroupListByIncId(Integer incId, String groupName, Integer pageNum, Integer pageSize) {
        String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.group_list_by_incId;
        Map<String, String> params = new HashMap<>();
        params.put("incId", incId == null ? null : String.valueOf(incId));
        params.put("groupName",groupName);
        params.put("pageNum", pageNum == null ? null : String.valueOf(pageNum));
        params.put("pageSize", pageSize == null ? null : String.valueOf(pageSize));
        String doPost = HttpTookit.doPost(url, params);
        logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPost);
        ResultCode<ListResult<Group>> result = JSON.parseObject(doPost,
                new TypeReference<ResultCode<ListResult<Group>>>() {
                });
        return result;
    }
}
