package com.quarkdata.yunpan.api.service;

import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.common.YunPanApiException;
import com.quarkdata.yunpan.api.model.vo.IncConfigVO;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Author yanyq1129@thundersoft.com
 * @Description 设置
 *
 */
public interface SettingsService extends BaseLogService {

	/**
	 * 查询企业信息和云盘配置信息
	 * 
	 * @param incId
	 * 
	 * @return
	 */
	ResultCode<IncConfigVO> checkSettings(Long incId);

	/**
	 * 自定义企业信息和云盘配置
	 * 
	 * @param incConfigVO
	 * @param incId
	 * @return
	 */
	ResultCode<Object> updateSettings(HttpServletRequest request, IncConfigVO incConfigVO, Long incId) throws YunPanApiException;

	/**
	 * 获取logo和每个用户使用空间限额
	 * @param incId
	 * @return
	 */
    ResultCode<IncConfigVO> getLogoAndPerUserQuota(Long incId);

	/**
	 * 回显企业信息
	 * @param incId
	 * @return
	 */
	ResultCode<Map<String,Object>> getIncInfo(Long incId);
}
