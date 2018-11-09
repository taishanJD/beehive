package com.quarkdata.yunpan.api.dal.rest.quarkshare;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.quarkdata.yunpan.api.model.common.Constants;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.util.HttpTookit;
import com.quarkdata.yunpan.api.util.JedisUtils;
import com.quarkdata.yunpan.api.util.common.mapper.JsonMapper;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wujianbo
 */
@Repository
public class TokenApi {

    private static Logger logger = Logger.getLogger(TokenApi.class);

    /**
     * 验证token
     * @param token
     * @return
     */
    public ResultCode<Map<String, Long>> validateToken(String token){

        Map<String, String> params=new HashMap<String, String>();
        params.put("token", token);
        String doGet = HttpTookit.doGet(QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.validate_token, params);
        logger.info("request url : "+QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.validate_token
                + " ,params : " + HttpTookit.getParams(params)
                + " ,result : " + doGet);
        return JSON.parseObject(doGet,new TypeReference<ResultCode<Map<String,Long>>>(){});
    }

    /**
     * 登录 获取token
     *
     *
     * @param request
     * @param userName
     * @param password
     * @return
     */
    public ResultCode<String> login(HttpServletRequest request, String userName, String password){
        Map<String, String> params=new HashMap<String, String>();
        params.put("domain", request.getServerName());
        params.put("userName", userName);
        params.put("password", password);
        String doPost = HttpTookit.doPost(QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.login, params);
        logger.info("request url : "+QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.login
                + " ,params : " + HttpTookit.getParams(params)
                + " ,result : " + doPost);
//        ResultCode<String> resultCode = JSON.parseObject(doPost,new TypeReference<ResultCode<String>>(){});
        ResultCode<String> resultCode = (ResultCode<String>) JsonMapper.fromJsonString(doPost, ResultCode.class);
        return resultCode;
    }

    /**
     * 登出 销毁token
     * @param token
     * @return
     */
    public ResultCode<String> logout(String token){
        ResultCode<Map<String, Long>> mapResultCode = this.validateToken(token);
        if(mapResultCode.getCode().equals(Messages.SUCCESS_CODE)) {
            JedisUtils.del(Constants.ONESHARE_REDIS_PREFIX + Constants.ONESHARE_REDIS_DELIMITER + Constants.ONESHARE_REDIS_USER_INFO_VO + Constants.ONESHARE_REDIS_DELIMITER + mapResultCode.getData().get("userId"));
        }
        String doGet = HttpTookit.doPostWithAuthorization(QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.logout, null, token);
        logger.info("request url : " + QuarkShareApiConstants.apiBasePath + QuarkShareApiConstants.logout + ", result : " + doGet);
        return JSON.parseObject(doGet, new TypeReference<ResultCode<String>>(){});
    }

}
