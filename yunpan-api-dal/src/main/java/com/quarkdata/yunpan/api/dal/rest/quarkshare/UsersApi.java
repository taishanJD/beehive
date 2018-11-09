package com.quarkdata.yunpan.api.dal.rest.quarkshare;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.quarkdata.quark.share.model.dataobj.Admin;
import com.quarkdata.quark.share.model.dataobj.Incorporation;
import com.quarkdata.quark.share.model.dataobj.OneshareRole;
import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.common.ListResult;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.util.HttpTookit;
import com.quarkdata.yunpan.api.util.common.persistence.Page;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author yanyq1129@thundersoft.com
 * @Description Share项目中用户相关API
 *
 */
@Repository
public class UsersApi {

	private static Logger logger = Logger.getLogger(Users.class);

	@Autowired
	private IncorporationApi incorporationApi;

	/**
	 * 根据企业ID统计企业用户限额使用情况
	 * 
	 * @param incId
	 * @return
	 */
	public ResultCode<Map<String, Object>> getUserUsage(String incId) {
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.user_count;
		Map<String, String> params = new HashMap<>();
		params.put("incId", String.valueOf(incId));
		String doGet = HttpTookit.doGet(url, params);
		logger.info("request url : " + url + " ,params : " + params + " ,result : " + doGet);
		ResultCode<Map<String, Object>> result = JSON.parseObject(doGet,
				new TypeReference<ResultCode<Map<String, Object>>>() {
				});
		return result;
	}

	public ResultCode<ListResult<Users>> getUserList(Integer incId, String email, Integer pageNum, Integer pageSize) {
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.user_list;
		Map<String, String> params = new HashMap<>();
		params.put("incId", String.valueOf(incId));
		params.put("search",email);
		params.put("pageNo", String.valueOf(pageNum));
		params.put("pageSize", String.valueOf(pageSize));
		String doGet = HttpTookit.doGet(url, params);
		logger.info("request url : " + url + " ,params : " + params + " ,result : " + doGet);
		ResultCode<Page<Users>> httpResult = JSON.parseObject(doGet,
				new TypeReference<ResultCode<Page<Users>>>() {
				});
		ResultCode<ListResult<Users>> result = new ResultCode<>();
		ListResult<Users> listResult = new ListResult<>();
		if(httpResult.getData() != null) {
			listResult.setPageNum(httpResult.getData().getPageNo());
			listResult.setPageSize(httpResult.getData().getPageSize());
			listResult.setTotalNum(Integer.parseInt(httpResult.getData().getCount() + ""));
			List<Users> users = httpResult.getData().getList();
			resetUserProperty(users);
			listResult.setListData(users);
		}
		result.setData(listResult);
		return result;
	}

	public ResultCode<List<Long>> getAdminUserListByFilter(){
		ResultCode<List<Long>> resultCode = new ResultCode<>();
		List<Long> superAdminIdList = null;
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.super_admin_list;
		Map<String, String> params = new HashMap<>();
		params.put("roleFlag", "0");
		params.put("incId", UserInfoUtil.getIncId().toString());
		String doPost = HttpTookit.doPost(url, params);
		logger.info("request url : " + url + " ,params : " + null + " ,result : " + doPost);
		ResultCode<List<Map<String,Object>>> httpResult = JSON.parseObject(doPost,
				new TypeReference<ResultCode<List<Map<String,Object>>>>() {
				});
		superAdminIdList = new ArrayList<>();
		if(httpResult != null && CollectionUtils.isNotEmpty(httpResult.getData())) {
			for(Map<String, Object> map: httpResult.getData()) {
				superAdminIdList.add(Long.parseLong(map.get("user_id").toString()));
			}
		}
		resultCode.setData(superAdminIdList);
		return resultCode;
	}
	/**
	 * 根据企业ID分页获取管理员列表
	 * 
	 * @param incId
	 * @param pageNum
	 * @param pageSize
	 * @param username 
	 * @return
	 */
	public ResultCode<ListResult<UserInfoVO>> getAdminList(Integer incId, Integer pageNum, Integer pageSize, String username) {
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.admin_list;
		Map<String, String> params = new HashMap<>();
		params.put("incId", String.valueOf(incId));
		params.put("pageNum", String.valueOf(pageNum));
		params.put("pageSize", String.valueOf(pageSize));
		params.put("pageSize", String.valueOf(pageSize));
		params.put("username", username);
		String doGet = HttpTookit.doGet(url, params);
		logger.info("request url : " + url + " ,params : " + params + " ,result : " + doGet);
		ResultCode<ListResult<UserInfoVO>> result = JSON.parseObject(doGet,
				new TypeReference<ResultCode<ListResult<UserInfoVO>>>() {
				});
		return result;
	}

	/**
	 * 批量添加管理员
	 * 
	 *
	 * @param incId
	 * @param userIds
	 * @param roleId
	 * @return
	 */
	public ResultCode<Object> addAdmin(Integer incId, String userIds, Long roleId) {
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.admin_add;
		Map<String, String> params = new HashMap<>();
		params.put("incId", incId.toString());
		params.put("userIds", userIds);
		params.put("roleId", String.valueOf(roleId));
		String doPost = HttpTookit.doPost(url, params);
		logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPost);
		ResultCode<Object> result = JSON.parseObject(doPost, new TypeReference<ResultCode<Object>>() {
		});
		return result;
	}

	/**
	 * 根据ID撤销系统管理员
	 * 
	 *
	 * @param incId
	 * @param systemAdminId
	 * @return
	 */
	public ResultCode<Object> revokeSystemAdmin(Integer incId, Long systemAdminId) {
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.admin_revoke;
		Map<String, String> params = new HashMap<>();
		params.put("incId", incId + "");
		params.put("systemAdminId", systemAdminId + "");
		String doPost = HttpTookit.doPost(url, params);
		logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPost);
		ResultCode<Object> result = JSON.parseObject(doPost, new TypeReference<ResultCode<Object>>() {
		});
		return result;
	}

	/**
	 * 根据ID撤销组织管理员
	 *
	 * @param incId
	 * @param orgAdminId
	 * @param replacerId
	 * @return
	 */
	public ResultCode<Object> revokeOrgAdmin(Long incId, Long orgAdminId, Long replacerId) {
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.admin_revoke_org;
		Map<String, String> params = new HashMap<>();
		params.put("incId", incId + "");
		params.put("orgAdminId", orgAdminId + "");
		params.put("replacerId", replacerId + "");
		String doPost = HttpTookit.doPost(url, params);
		logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPost);
		ResultCode<Object> result = JSON.parseObject(doPost, new TypeReference<ResultCode<Object>>() {
		});
		return result;
	}

	/**
	 * 编辑系统管理员
	 *
	 * @param incId
	 * @param systemAdminId
	 * @return
	 */
	public ResultCode<Object> editSystemAdmin(Integer incId, Long systemAdminId) {
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.admin_edit;
		Map<String, String> params = new HashMap<>();
		params.put("incId", incId + "");
		params.put("systemAdminId", systemAdminId + "");
		String doPost = HttpTookit.doPost(url, params);
		logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPost);
		ResultCode<Object> result = JSON.parseObject(doPost, new TypeReference<ResultCode<Object>>() {
		});
		return result;
	}

	/**
	 * 编辑组织管理员
	 *
	 * @param incId
	 * @param orgAdminId
	 * @param replacerId
	 * @return
	 */
	public ResultCode<Object> editOrgAdmin(Long incId, Long orgAdminId, Long replacerId) {
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.admin_edit_org;
		Map<String, String> params = new HashMap<>();
		params.put("incId", incId + "");
		params.put("orgAdminId", orgAdminId + "");
		params.put("replacerId", replacerId + "");
		String doPost = HttpTookit.doPost(url, params);
		logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPost);
		ResultCode<Object> result = JSON.parseObject(doPost, new TypeReference<ResultCode<Object>>() {
		});
		return result;
	}

	/**
	 * 根据ID查询用户信息
	 * 
	 * @param userId
	 * @return
	 */
	public ResultCode<Users> getUserById(String userId) {
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.get_user_by_id;
		Map<String, String> params = new HashMap<>();
		params.put("userId", userId);
		String doPost = HttpTookit.doPost(url, params);
		logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPost);
		ResultCode<Users> result = JSON.parseObject(doPost, new TypeReference<ResultCode<Users>>() {
		});
		return result;
	}

	/**
	 * 根据ID查询用户角色信息
	 * 
	 * @param userId
	 * @return
	 */
	public ResultCode<OneshareRole> getUserRoleById(String userId) {
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.get_user_role_by_id;
		Map<String, String> params = new HashMap<>();
		params.put("userId", userId);
		String doGet = HttpTookit.doGet(url, params);
		logger.info("request url : " + url + " ,params : " + params + " ,result : " + doGet);
		ResultCode<OneshareRole> result = JSON.parseObject(doGet, new TypeReference<ResultCode<OneshareRole>>() {
		});
		return result;
	}

	/**
	 * 根据用户名模糊查询普通用户(非云盘管理员)
	 * 
	 * @param incId
	 * @param userName
	 * @param pageNum
     *@param pageSize @return
	 */
	public ResultCode<List<Users>> findUserByUserName(Integer incId, String userName, Integer pageNum, Integer pageSize) {
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.get_user_by_userName;
		Map<String, String> params = new HashMap<>();
		params.put("incId", String.valueOf(incId));
		params.put("userName", String.valueOf(userName));
		params.put("pageNum", String.valueOf(pageNum));
		params.put("pageSize", String.valueOf(pageSize));
		String doGet = HttpTookit.doGet(url, params);
		logger.info("request url : " + url + " ,params : " + params + " ,result : " + doGet);
		ResultCode<List<Users>> result = JSON.parseObject(doGet, new TypeReference<ResultCode<List<Users>>>() {
		});
		return result;
	}

	/**
	 * 更新用户
	 * @param users
	 * @return
	 */
	public ResultCode update(Users users)
	{
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.update_user;
		String doPost=HttpTookit.doPostJson(url,users);
		ResultCode<String> result=JSON.parseObject(doPost, new TypeReference<ResultCode<String>>() {
		});
		return  result;
	}

	/**
	 * 插入用户
	 * @param user
	 * @return
	 */
	public ResultCode<Users> save(Users user){
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.save_user;
		String doPost=HttpTookit.doPostJson(url,user);
		ResultCode<Users> result=JSON.parseObject(doPost, new TypeReference<ResultCode<Users>>() {
		});
		return  result;
	}

	public ResultCode<List<Users>> getUserDetails(String[] userId,String incId){
		Map<String, String[]> params = new HashMap<>();
		params.put("userIds", userId);
		params.put("incId", new String[]{incId});
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.user_details;
		String doGetForParamsArray = HttpTookit.doGetForParamsArray(url, params );
		logger.info("request url : " + url + " ,params : " + params + " ,result : " + doGetForParamsArray);
		ResultCode<List<Users>> result=JSON.parseObject(doGetForParamsArray, new TypeReference<ResultCode<List<Users>>>() {
		});
		return result;
	}

	/**
	 * 根据用户名查询用户
	 *
	 * @param request
	 * @param username
	 * @return
	 */
	public ResultCode<List<Users>> getUserByUsername(HttpServletRequest request, String username) {
        String serverName = request.getServerName();
        String incId = "1";
        // 根据域名查找企业
        ResultCode<Incorporation> resultCode = this.incorporationApi.getIncByDomain(serverName);
        if(resultCode != null && resultCode.getData() != null) {
            incId = resultCode.getData().getId().toString();
        }
        String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.find_user_by_username;
		Map<String, String> params = new HashMap<>();
        ResultCode<List<Users>> result = null;
		params.put("userName", username);
		params.put("incId", incId);

		// 第一次查询(能根据域名查到企业或者incId=1)
		String doGet = HttpTookit.doGet(url, params);
		logger.info("request url : " + url + " ,params : " + params + " ,result : " + doGet);
		result = JSON.parseObject(doGet, new TypeReference<ResultCode<List<Users>>>() {
		});
        // 第二次查询(incId=2)
		if(result == null || CollectionUtils.isEmpty(result.getData())) {
            params.put("incId", "2");
            result = JSON.parseObject(HttpTookit.doGet(url, params), new TypeReference<ResultCode<List<Users>>>() {
            });
        }
		return result;
	}

	/***
	 * 删除一个账号
	 * @param id
	 * @return
	 */
	public ResultCode<String> delete(Integer id){
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.del_user;
		Map<String, String> params = new HashMap<>();
		params.put("userId", id.toString());
		String doPost = HttpTookit.doPost(url, params);
		logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPost);
		ResultCode<String> result = JSON.parseObject(doPost, new TypeReference<ResultCode<String>>() {
		});
		return result;
	}

	/**
	 * 获取指定用户的新token
	 * @param username
	 * @param oldToken
	 * @return
	 */
	public ResultCode<String> getNewTokenByUsername(String username, String oldToken) {
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.get_token_by_username;
		Map<String, String> params = new HashMap<>();
		params.put("username", username);
		params.put("oldToken", oldToken);
		String doGet = HttpTookit.doGet(url, params);
		logger.info("request url : " + url + " ,params : " + params + " ,result : " + doGet);
		ResultCode<String> result = JSON.parseObject(doGet, new TypeReference<ResultCode<String>>() {
		});
		return result;
	}

	public List<Users> getUsersByDeptId(Integer incId, String deptId, String search) {
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.user_list_by_dept_id;
		Map<String, String> params = new HashMap<>();
		//params.put("incId", String.valueOf(incId));
		params.put("deptId",deptId);
		params.put("search",search);
		String doGet = HttpTookit.doGet(url, params);
		logger.info("request url : " + url + " ,params : " + params + " ,result : " + doGet);
		ResultCode<Page<Users>> httpResult = JSON.parseObject(doGet,
				new TypeReference<ResultCode<Page<Users>>>() {
				});
		List<Users> result = new ArrayList<>();
		if(httpResult.getData() != null) {
			result = httpResult.getData().getList();
			resetUserProperty(result);
		}
		return result;
	}

	public List<Users> getUsersByDeptIdRecursive(Integer incId, String deptId, String search, Integer pageNum, Integer pageSize) {
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.user_list_by_dept_id;
		Map<String, String> params = new HashMap<>();
		params.put("incId", String.valueOf(incId));
		params.put("deptId",deptId);
		params.put("pageNo",pageNum.toString());
		params.put("pageSize",pageSize.toString());
		params.put("search",search);
		String doGet = HttpTookit.doGet(url, params);
		logger.info("request url : " + url + " ,params : " + params + " ,result : " + doGet);
		ResultCode<Page<Users>> httpResult = JSON.parseObject(doGet,
				new TypeReference<ResultCode<Page<Users>>>() {
				});
		List<Users> result = new ArrayList<>();
		if(httpResult.getData() != null) {
			result = httpResult.getData().getList();
			resetUserProperty(result);

		}
		return result;
	}

	public List<Users> getUsersByGroupId(Integer incId, String groupId, String search) {
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.user_list_by_group_id;
		Map<String, String> params = new HashMap<>();
		params.put("incId", String.valueOf(incId));
		params.put("groupId",groupId);
		params.put("searchParam",search);
		String doGet = HttpTookit.doPost(url, params);
		logger.info("request url : " + url + " ,params : " + params + " ,result : " + doGet);
		ResultCode<Page<Users>> httpResult = JSON.parseObject(doGet,
				new TypeReference<ResultCode<Page<Users>>>() {
				});
		List<Users> result = new ArrayList<>();
		if(httpResult.getData() != null) {
			result = httpResult.getData().getList();
			resetUserProperty(result);
		}
		return result;
	}

	/**
	 * 重置User属性
	 *
	 */
	private void resetUserProperty(List<Users> users){
		if(users != null){
			for(Users user : users){
				if(user != null){
					user.setPassword(null);
					if(user.getDisplayName() == null){
						user.setDisplayName("");
					}
					if(user.getEmail() == null){
						user.setEmail("");
					}
					if(user.getUserName() == null){
						user.setUserName("");
					}
					if(user.getMobile() == null){
						user.setMobile("");
					}
				}
			}
		}
	}

	/**
	 * 批量删除用户
	 * @param userIds
	 */
    public ResultCode<Object> batchDeleteUsers(String[] userIds) {
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.del_user_batch;
		Map<String, String[]> params = new HashMap<>();
		params.put("userIds", userIds);
		String doPostForParamsArray = HttpTookit.doPostForParamsArray(url, params);
		logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPostForParamsArray);
		ResultCode<Object> result = JSON.parseObject(doPostForParamsArray, new TypeReference<ResultCode<Object>>() {
		});
		return result;
    }


	/**
	 * 判断是否是超级管理员
	 * @return
	 */
	public ResultCode<Admin> getOneSpaceAdminByUserId(Integer userId) {
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.GET_ONE_SPACE_ADMIN_BY_USERID;
		Map<String, String> params = new HashMap<>();
		params.put("userId", userId.toString());
		String doPost = HttpTookit.doPost(url, params);
		logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPost);
		ResultCode<Admin> result = JSON.parseObject(doPost, new TypeReference<ResultCode<Admin>>() {
		});
		return result;
    }
}
