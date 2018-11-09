package com.quarkdata.yunpan.api.service;

import java.util.Map;

import com.quarkdata.quark.share.model.dataobj.Incorporation;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.model.common.ResultCode;

/**
 * @Author yanyq1129@thundersoft.com
 * @Description 仪表板
 */
public interface DashboardService {

	/**
	 * 获取云盘使用情况
	 * 
	 * @param incId
	 */
	ResultCode<Map<String, Object>> getUsage(long incId);

	/**
	 * 获取用户使用排行
	 * 
	 * @param incId
	 */
	ResultCode<Map<String, Object>> getUserRank(long incId);

	/**
	 * 获取个人空间使用情况(以当前登录用户查询)
	 *
	 * @param incId
	 * @param userId
	 * @return
	 */
	ResultCode<Object> getUsageByCurrentUserAndIncId(long incId, Integer userId);


	/**
	 * 获取组织空间使用情况
	 * @param incId
	 * @return
	 */
    ResultCode<Map<String,Object>> getIncUsage(long incId);

	/**
	 * 根据当前登录用户获取个人空间使用情况和组织空间使用情况(若当前用户是组织管理员)
	 * @param userInfoVO
	 * @param incorporation
	 * @return
	 */
    ResultCode<Object> getAllUsageByCurrentUser(UserInfoVO userInfoVO, Incorporation incorporation);
}