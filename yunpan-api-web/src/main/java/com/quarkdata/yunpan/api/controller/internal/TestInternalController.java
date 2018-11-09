package com.quarkdata.yunpan.api.controller.internal;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.quarkdata.quark.share.model.dataobj.Incorporation;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.util.common.mapper.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.service.TestService;

/**
 * 
 *
 * Created by xu on 2017-01-16.
 */
@Controller
@RequestMapping(RouteKey.PREFIX_API+"/"+RouteKey.TEST)
public class TestInternalController {

    @Autowired
    private TestService testService;

    
    static Logger logger = LoggerFactory.getLogger(TestInternalController.class);

    /**
     * test
     * @param request
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/test1")
    public ResultCode<UserInfoVO> getTest(HttpServletRequest request, HttpServletResponse response){
        ResultCode<UserInfoVO> result = new ResultCode<>();
        try{
            //获取当前用户信息（包括：用户、用户组、部门、角色）
            UserInfoVO userInfoVO = UserInfoUtil.getUserInfoVO();
            //获取当前用户所在企业信息
            Incorporation incorporation = UserInfoUtil.getIncorporation();

            logger.info(JsonMapper.toJsonString(incorporation));
            logger.info(JsonMapper.toJsonString(userInfoVO));
            result.setData(userInfoVO);
        } catch (Exception e){
            logger.error("test error", e);
            result.setCode(Messages.API_ERROR_CODE);
            result.setMsg(Messages.API_ERROR_MSG);
        }
        return result;
    }

 
}
