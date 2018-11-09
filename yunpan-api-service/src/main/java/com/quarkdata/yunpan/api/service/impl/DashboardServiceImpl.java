package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.quark.share.model.dataobj.Incorporation;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.quark.share.model.vo.UserRankVO;
import com.quarkdata.yunpan.api.dal.dao.DocumentMapper2;
import com.quarkdata.yunpan.api.dal.dao.IncConfigMapper;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.UsersApi;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.IncConfig;
import com.quarkdata.yunpan.api.model.dataobj.IncConfigExample;
import com.quarkdata.yunpan.api.model.dataobj.IncConfigExample.Criteria;
import com.quarkdata.yunpan.api.service.DashboardService;
import com.quarkdata.yunpan.api.util.ArithmeticUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Service
public class DashboardServiceImpl implements DashboardService {

	@Autowired
	private IncConfigMapper incConfigMapper;

	@Autowired
	private UsersApi usersApi;

	@Autowired
	private DocumentMapper2 documentMapper2;

	@Override
	public ResultCode<Map<String, Object>> getUsage(long incId) {
		ResultCode<Map<String, Object>> result = new ResultCode<Map<String, Object>>();
		// 1.获取云盘使用情况
		// 设置查询条件
		IncConfigExample incConfigExample = new IncConfigExample();
		Criteria incCriteria = incConfigExample.createCriteria();
		incCriteria.andIncIdEqualTo(incId);
		// 执行查询
		List<IncConfig> list = incConfigMapper.selectByExample(incConfigExample);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if (list != null && list.size() > 0) {
			IncConfig incConfig = list.get(0);
			// 企业总空间
			Integer incTotalQuota = incConfig.getIncTotalQuota(); // MB
			// 组织空间占比
			Integer incRatio = incConfig.getIncRatio();
			// 个人空间占比
			Integer userRatio = incConfig.getUserRatio();
			// 组织总空间
			BigDecimal incQuota = ArithmeticUtil.mul(String.valueOf(incTotalQuota), ArithmeticUtil
					.div(String.valueOf(incRatio), String.valueOf((incRatio + userRatio)), 10).toString());
			incQuota = ArithmeticUtil.round(String.valueOf(incQuota), 0); // MB
			// 个人总空间
			BigDecimal userQuota = ArithmeticUtil.mul(String.valueOf(incTotalQuota), ArithmeticUtil
					.div(String.valueOf(userRatio), String.valueOf((incRatio + userRatio)), 10).toString());
			userQuota = ArithmeticUtil.round(String.valueOf(userQuota), 0); // MB

			// 已用个人空间
			long userDocumentsSize = 0;
			// 1.未删除个人文档
			long userSize1 = this.documentMapper2.sumUserDocumentUndeleted(incId);
			// 2.回收站中的个人文档
			long userSize2 = this.documentMapper2.sumUserDocumentInRecycle(incId);
			// 3.合计
			userDocumentsSize = userSize1 + userSize2;
			double personal_quota_used = Math.abs(Double.parseDouble(ArithmeticUtil.round(ArithmeticUtil
					.strDiv(ArithmeticUtil.strDiv(String.valueOf(userDocumentsSize), "1024", 10), "1024", 10), 1)
					.toString()));

			
			// 已用组织空间
			long incDocumentsSize = 0;
			// 1.未删除组织文档
			long incSize1 = this.documentMapper2.sumIncDocumentUndeleted(incId);
			// 2.回收站中的组织文档
			long incSize2 = this.documentMapper2.sumIncDocumentInRecycle(incId);
			// 3.合计
			incDocumentsSize = incSize1 + incSize2;
			double inc_quota_used = Math
					.abs(Double.parseDouble(ArithmeticUtil
							.round(ArithmeticUtil.strDiv(
									ArithmeticUtil.strDiv(String.valueOf(incDocumentsSize), "1024", 10), "1024", 10), 1)
							.toString()));

			// 合计已用总空间
			BigDecimal total_quota_used = ArithmeticUtil.add(String.valueOf(personal_quota_used),
					String.valueOf(inc_quota_used));

			map.put("total_quota", incConfig.getIncTotalQuota());
			map.put("inc_quota", incQuota);
			map.put("personal_quota", userQuota);
			map.put("inc_quota_used", inc_quota_used);
			map.put("personal_quota_used", personal_quota_used);
			map.put("total_quota_used", total_quota_used);

		}
		// 2.统计企业用户使用情况(用户限额,现有用户数,活跃用户数)
		ResultCode<Map<String, Object>> resultCode = this.usersApi.getUserUsage(String.valueOf(incId));
		Map<String, Object> userMap = resultCode.getData();
		for (Entry<String, Object> entry : userMap.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}

		// 封装数据
		result.setData(map);
		return result;
	}

	@Override
	public ResultCode<Map<String, Object>> getUserRank(long incId) {
		ResultCode<Map<String, Object>> result = new ResultCode<Map<String, Object>>();
		List<UserRankVO> userRankList = this.documentMapper2.getUserRank(incId);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if (userRankList != null && userRankList.size() > 0) {
			for (UserRankVO userRankVO : userRankList) {
				map.put(userRankVO.getCreateUserName(), ArithmeticUtil.strRound(
						ArithmeticUtil.strDiv(ArithmeticUtil.strDiv(userRankVO.getSumSize(), "1024", 10), "1024", 10),
						0));
			}
		}
		result.setData(map);
		return result;
	}

	@Override
	public ResultCode<Object> getUsageByCurrentUserAndIncId(long incId, Integer userId) {
		ResultCode<Object> result = new ResultCode<Object>();
		// 已用个人空间
		long userDocumentsSize = 0;
		// 1.未删除个人文档
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("incId", incId);
		map.put("userId", userId);
		long userSize1 = this.documentMapper2.sumUserDocumentUndeletedByCurrentUser(map);
		// 2.回收站中的个人文档
		long userSize2 = this.documentMapper2.sumUserDocumentInRecycleByCurrentUser(map);
		// 3.合计
		userDocumentsSize = userSize1 + userSize2;
		double mySpaceUsed = Math
				.abs(Double.parseDouble(ArithmeticUtil
						.round(ArithmeticUtil.strDiv(
								ArithmeticUtil.strDiv(String.valueOf(userDocumentsSize), "1024", 10), "1024", 10), 1)
						.toString()));
		result.setData(mySpaceUsed);
		return result;
	}

	@Override
	public ResultCode<Map<String, Object>> getIncUsage(long incId) {
		ResultCode<Map<String, Object>> result = new ResultCode<Map<String, Object>>();
		// 1.获取云盘使用情况
		// 设置查询条件
		IncConfigExample incConfigExample = new IncConfigExample();
		Criteria incCriteria = incConfigExample.createCriteria();
		incCriteria.andIncIdEqualTo(incId);
		// 执行查询
		List<IncConfig> list = incConfigMapper.selectByExample(incConfigExample);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if (list != null && list.size() > 0) {
			IncConfig incConfig = list.get(0);
			// 企业总空间
			Integer incTotalQuota = incConfig.getIncTotalQuota(); // MB
			// 组织空间占比
			Integer incRatio = incConfig.getIncRatio();
			// 个人空间占比
			Integer userRatio = incConfig.getUserRatio();
			// 组织总空间
			BigDecimal incQuota = ArithmeticUtil.mul(String.valueOf(incTotalQuota), ArithmeticUtil
					.div(String.valueOf(incRatio), String.valueOf((incRatio + userRatio)), 10).toString());
			incQuota = ArithmeticUtil.round(String.valueOf(incQuota), 0); // MB

			// 已用组织空间
			long incDocumentsSize = 0;
			// 1.未删除组织文档
			long incSize1 = this.documentMapper2.sumIncDocumentUndeleted(incId);

			// 2.回收站中的组织文档
			long incSize2 = this.documentMapper2.sumIncDocumentInRecycle(incId);

			// 3.合计
			incDocumentsSize = incSize1 + incSize2;
			double inc_quota_used = Math
					.abs(Double.parseDouble(ArithmeticUtil
							.round(ArithmeticUtil.strDiv(
									ArithmeticUtil.strDiv(String.valueOf(incDocumentsSize), "1024", 10), "1024", 10), 1)
							.toString()));

			map.put("inc_quota", incQuota);
			map.put("inc_quota_used", inc_quota_used);

		}
		// 封装数据
		result.setData(map);
		return result;
	}

	@Override
	public ResultCode<Object> getAllUsageByCurrentUser(UserInfoVO userInfoVO, Incorporation incorporation) {
		ResultCode<Object> resultCode = new ResultCode<>();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		// 获取个人空间总量
		IncConfigExample incConfigExample = new IncConfigExample();
		Criteria incCriteria = incConfigExample.createCriteria();
		incCriteria.andIncIdEqualTo(Long.parseLong(incorporation.getId().toString()));
		List<IncConfig> list = incConfigMapper.selectByExample(incConfigExample);
		if(list != null && list.size() > 0) {
			map.put("perUserQuota", list.get(0).getPerUserQuota());
		}
		// 获取个人空间已使用量
		map.put("perUserQuota_used", this.getUsageByCurrentUserAndIncId(incorporation.getId(), userInfoVO.getUser().getUserId()).getData());
		// 判断当前登录用户是否是组织管理员
		if(userInfoVO.getRole().getId() == 2 || userInfoVO.getRole().getId() == 1) {
			// 获取组织空间使用情况
			Map<String, Object> inc_quota_data = this.getIncUsage(incorporation.getId()).getData();
			map.put("inc_quota", inc_quota_data.get("inc_quota"));
			map.put("inc_quota_used", inc_quota_data.get("inc_quota_used"));
		}
		resultCode.setData(map);
		return resultCode;
	}

}
