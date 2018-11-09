package com.quarkdata.yunpan.api.dal.rest.quarkshare;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.quarkdata.quark.share.model.dataobj.Incorporation;
import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.util.HttpTookit;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author yanyq1129@thundersoft.com
 * @Description Share项目中企业(租户)相关API
 *
 */
@Repository
public class IncorporationApi {
	private static Logger logger = Logger.getLogger(Users.class);

	/**
	 * 根据企业ID查询企业信息
	 * 
	 * @param incId
	 * @return
	 */
	public ResultCode<Incorporation> check(Long incId) {
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.incorporation_info_check;
		Map<String, String> params = new HashMap<String, String>();
		params.put("incId", String.valueOf(incId));
		String doGet = HttpTookit.doGet(url, params);
		logger.info("request url : " + url + " ,params : " + params + " ,result : " + doGet);
		ResultCode<Incorporation> result = JSON.parseObject(doGet, new TypeReference<ResultCode<Incorporation>>() {
		});

		return result;
	}

	/**
	 * 根据企业ID更新企业信息
	 * 
	 * @param incId
	 * @return
	 */
	public ResultCode<Object> update(Long incId, String incName) {
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.incorporation_info_update;
		Map<String, String> params = new HashMap<String, String>();
		params.put("incId", String.valueOf(incId));
		params.put("incName", incName);
		String doPost = HttpTookit.doPost(url, params);
		logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPost);
		ResultCode<Object> result = JSON.parseObject(doPost, new TypeReference<ResultCode<Object>>() {
		});

		return result;
	}

	/**
	 * 根据企业ID更新企业信息
	 *
	 * @param domain
	 * @return
	 */
	public ResultCode<Incorporation> getIncByDomain(String domain) {
		String url = QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.incorporation_info_get_by_domain;
		Map<String, String> params = new HashMap<String, String>();
		params.put("domain", domain);
		String doPost = HttpTookit.doPost(url, params);
		logger.info("request url : " + url + " ,params : " + params + " ,result : " + doPost);
		ResultCode<Incorporation> result = JSON.parseObject(doPost, new TypeReference<ResultCode<Incorporation>>() {
		});

		return result;
	}

}
