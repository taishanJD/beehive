package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.common.YunPanApiException;
import com.quarkdata.yunpan.api.model.vo.IncConfigVO;
import com.quarkdata.yunpan.api.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Author yanyq1129@thundersoft.com
 * @Description 设置
 *
 */
@Controller
@RequestMapping(RouteKey.PREFIX_API + "/settings")
public class SettingsController extends BaseController {

	@Autowired
	private SettingsService settingsService;

	/**
	 * 回显企业信息和云盘配置信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "/check", method = RequestMethod.GET)
	@ResponseBody
	public ResultCode<IncConfigVO> checkSettings() {
		ResultCode<IncConfigVO> result = new ResultCode<IncConfigVO>();
		try {
			result = this.settingsService
					.checkSettings(Long.valueOf(String.valueOf(UserInfoUtil.getIncorporation().getId())));
		} catch (NumberFormatException e) {
			result.setCode(1);
			result.setMsg(Messages.GET_INCINFO_AND_INCCONFIG_FAIL_MSG);
			this.logger.error(Messages.GET_INCINFO_AND_INCCONFIG_FAIL_MSG, e);
		}
		return result;
	}

	/**
	 * 获取logo和每个用户使用空间限额
	 *
	 * @return
	 */
	@RequestMapping(value = "/logo-quota", method = RequestMethod.GET)
	@ResponseBody
	public ResultCode<IncConfigVO> getLogoAndPerUserQuota() {
		ResultCode<IncConfigVO> result = new ResultCode<IncConfigVO>();
		try {
			result = this.settingsService
					.getLogoAndPerUserQuota(Long.valueOf(String.valueOf(UserInfoUtil.getIncorporation().getId())));
		} catch (NumberFormatException e) {
			result.setCode(1);
			result.setMsg("获取logo和每个用户使用空间限额失败!");
			this.logger.error("获取logo和每个用户使用空间限额失败!", e);
		}
		return result;
	}

	/**
	 * 自定义企业信息和云盘配置
	 * 
	 * @return
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	public ResultCode<Object> update(HttpServletRequest request, IncConfigVO incConfigVO) {
		ResultCode<Object> result = new ResultCode<Object>();
		try {
			this.logger.info("修改企业信息和云盘配置");
			result = this.settingsService.updateSettings(request, incConfigVO,Long.valueOf(String.valueOf(UserInfoUtil.getIncorporation().getId())));
			result.setData(null);
		} catch (NumberFormatException e) {
			result.setCode(1);
			result.setMsg(Messages.UPDATE_INCINFO_AND_INCCONFIG_FAIL_MSG);
			this.logger.error(Messages.UPDATE_INCINFO_AND_INCCONFIG_FAIL_MSG, e);
			result.setData(null);
		} catch (YunPanApiException e) {
			e.printStackTrace();
			result.setCode(1);
			result.setMsg(Messages.UPDATE_INCINFO_AND_INCCONFIG_FAIL_MSG);
			this.logger.error(Messages.UPDATE_INCINFO_AND_INCCONFIG_FAIL_MSG, e);
			result.setData(null);
		}
		return result;
	}

	/**
	 * 回显企业信息
	 *
	 * @return
	 */
	@RequestMapping(value = "/inc", method = RequestMethod.GET)
	@ResponseBody
	public ResultCode<Map<String, Object>> getIncInfo() {
		ResultCode<Map<String, Object>> result = null;
		try {
			result = this.settingsService.getIncInfo(Long.valueOf(String.valueOf(UserInfoUtil.getIncorporation().getId())));
		} catch (NumberFormatException e) {
			result = new ResultCode<>();
			result.setCode(Messages.GET_INC_INFO_FAIL_CODE);
			result.setMsg(Messages.GET_INC_INFO_FAIL_MSG);
			this.logger.error(Messages.GET_INC_INFO_FAIL_MSG, e);
		}
		return result;
	}
}
