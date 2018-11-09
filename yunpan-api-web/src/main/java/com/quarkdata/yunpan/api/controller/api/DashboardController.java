package com.quarkdata.yunpan.api.controller.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.service.DashboardService;

/**
 * @Author yanyq1129@thundersoft.com
 * @Description 仪表板Controller
 */
@Controller
@RequestMapping(RouteKey.PREFIX_API + "/dashboard")
public class DashboardController extends BaseController {

	@Autowired
	private DashboardService dashboardService;

	/**
	 * 获取云盘使用情况
	 * 
	 * @return
	 */
	@RequestMapping(value = "/usage", method = RequestMethod.GET)
	@ResponseBody
	public ResultCode<Map<String, Object>> getUsage() {
		ResultCode<Map<String, Object>> result = new ResultCode<Map<String, Object>>();
		try {
			this.logger.info("获取云盘使用情况");
			result = this.dashboardService
					.getUsage(Long.parseLong(String.valueOf(UserInfoUtil.getIncorporation().getId())));
		} catch (NumberFormatException e) {
			result.setCode(1);
			result.setMsg(Messages.GET_USAGE_FAIL_MSG);
			this.logger.error(Messages.GET_USAGE_FAIL_MSG, e);
		}
		return result;
	}

	/**
	 * 获取组织空间使用情况
	 *
	 * @return
	 */
	@RequestMapping(value = "/usage-inc", method = RequestMethod.GET)
	@ResponseBody
	public ResultCode<Map<String, Object>> getIncUsage() {
		ResultCode<Map<String, Object>> result = new ResultCode<Map<String, Object>>();
		try {
			this.logger.info("获取组织空间使用情况");
			result = this.dashboardService
					.getIncUsage(Long.parseLong(String.valueOf(UserInfoUtil.getIncorporation().getId())));
		} catch (NumberFormatException e) {
			result.setCode(1);
			result.setMsg("获取组织空间使用情况失败!");
			this.logger.error("获取组织空间使用情况失败!", e);
		}
		return result;
	}

	/**
	 * 获取用户使用排行
	 * 
	 * @return
	 */
	@RequestMapping(value = "/rank", method = RequestMethod.GET)
	@ResponseBody
	public ResultCode<Map<String, Object>> getUserUsage() {
		ResultCode<Map<String, Object>> result = new ResultCode<Map<String, Object>>();
		try {
			this.logger.info("获取用户使用排行");
			result = this.dashboardService
					.getUserRank(Long.parseLong(String.valueOf(UserInfoUtil.getIncorporation().getId())));
		} catch (NumberFormatException e) {
			result.setCode(1);
			result.setMsg(Messages.GET_USER_RANK_MSG);
			this.logger.error(Messages.GET_USER_RANK_MSG, e);
		}
		return result;
	}

	/**
	 * 获取个人空间使用情况(以当前登录用户查询)
	 *
	 * @return
	 */
	@RequestMapping(value = "/my-space", method = RequestMethod.GET)
	@ResponseBody
	public ResultCode<Object> getUsageByCurrentUserAndIncId() {
		ResultCode<Object> result = new ResultCode<Object>();
		try {
			this.logger.info("获取个人空间使用情况");
			result = this.dashboardService.getUsageByCurrentUserAndIncId(
					Long.parseLong(String.valueOf(UserInfoUtil.getIncorporation().getId())),
					UserInfoUtil.getUserInfoVO().getUser().getUserId());
		} catch (NumberFormatException e) {
			result.setCode(1);
			result.setMsg(Messages.GET_MY_SPACE_FAIL_MSG);
			this.logger.error(Messages.GET_MY_SPACE_FAIL_MSG, e);
		}
		return result;
	}

	/**
	 * 根据当前登录用户获取个人空间使用情况和组织空间使用情况(若当前用户是组织管理员)
	 * @return
	 */
	@RequestMapping(value = "/usage-all", method = RequestMethod.GET)
	@ResponseBody
	public ResultCode<Object> getAllUsageByCurrentUser() {
		ResultCode<Object> result = new ResultCode<Object>();
		result = this.dashboardService.getAllUsageByCurrentUser(UserInfoUtil.getUserInfoVO(), UserInfoUtil.getIncorporation());
		return result;
	}

}