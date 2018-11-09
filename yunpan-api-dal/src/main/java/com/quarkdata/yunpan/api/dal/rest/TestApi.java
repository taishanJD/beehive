package com.quarkdata.yunpan.api.dal.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import com.quarkdata.yunpan.api.model.common.Constants;
import com.quarkdata.yunpan.api.model.common.ResultCode;

import com.quarkdata.yunpan.api.util.HttpTookit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 根云接口
 * Created by xxm on 2017-1-16.
 */
@Repository
public class TestApi {
	
//	@Autowired
//	private ProductMapper productMapper;

    /**
     * 日志对象
     */
    private static Logger logger = Logger.getLogger(TestApi.class);


	public ResultCode<Object> processPaymentForBusiness(String billNum,String productCode) {
		ResultCode<Object> result=null;
		
//	    	String resultStr = HttpTookit.doPost(url, params);
//			logger.info("request url : "+url
//					+ ", params : " + JsonMapper.toJsonString(params)
//					+ ", result : " + resultStr);
//			result = JSON.parseObject(resultStr, new TypeReference<ResultCode<Object>>(){});			

		return result;
	}
}
