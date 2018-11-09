package com.quarkdata.yunpan.api.service;

import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.model.common.ListResult;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.common.YunPanApiException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author yanyq1129@thundersoft.com
 * @Description 管理员
 *
 */
public interface AdminService extends BaseLogService {

	/**
	 * 获取管理员列表
	 * 
	 * @param incId
	 * @param pageSize
	 * @param pageNum
	 * @param username 
	 * @return
	 */
	ResultCode<ListResult<UserInfoVO>> getAdminList(Integer incId, Integer pageNum, Integer pageSize, String username);

	/**
	 * 批量添加管理员
	 * 
	 *
     * @param incId
     * @param userIds
     * @param roleId
     * @return
	 */
	ResultCode<Object> addAdmin(HttpServletRequest request, Integer incId, String userIds, Long roleId);

	/**
	 * 根据ID撤销系统管理员
	 * 
	 *
	 * @param incId
	 * @param systemAdminId
	 * @return
	 */
	ResultCode<Object> revokeSystemAdmin(HttpServletRequest request, Integer incId, Long systemAdminId);

	/**
	 * 根据ID撤销组织管理员
	 * @param orgAdminId
	 * @param replacerId
	 * @return
	 */
	ResultCode<Object> revokeOrgAdmin(HttpServletRequest request, Long incId, Long orgAdminId, Long replacerId) throws YunPanApiException;

	/**
	 * 根据用户名模糊查询普通用户(非云盘管理员)
	 *
	 * @param incId
	 * @param userName
	 * @param pageNum
     *@param pageSize @return
	 */
	ResultCode<List<Users>> findUserByUserName(Integer incId, String userName, Integer pageNum, Integer pageSize);

	/**
	 * 编辑系统管理员
	 *
	 * @param incId
	 * @param systemAdminId
	 * @return
	 */
    ResultCode<Object> editSystemAdmin(HttpServletRequest request, Integer incId, Long systemAdminId);

	/**
	 * 编辑组织管理员
	 * @param incId
	 * @param orgAdminId
	 * @param replacerId
	 * @return
	 */
	ResultCode<Object> editOrgAdmin(HttpServletRequest request, Long incId, Long orgAdminId, Long replacerId) throws YunPanApiException;

}
