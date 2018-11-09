package com.quarkdata.yunpan.api.controller.ajax;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.service.TestService;
import com.quarkdata.yunpan.api.controller.RouteKey;

/**
 * 
 *
 * Created by xu on 2017-01-16.
 */
@Controller
@RequestMapping(RouteKey.PREFIX_AJAX+"/"+RouteKey.TEST)
public class TestAjaxController {

    @Autowired
    private TestService testService;
    
    static Logger logger = LoggerFactory.getLogger(TestAjaxController.class);

    /**
     * test
     * @param request
     * @param response
     * @param model
     */
    @ResponseBody
    @RequestMapping(value = "/test1")
    public ResultCode<String> getTest(HttpServletRequest request, HttpServletResponse response){
        ResultCode<String> result = new ResultCode<String>();
        try{

        } catch (Exception e){
            logger.error("get pay channels error", e);
            result.setCode(Messages.API_ERROR_CODE);
            result.setMsg(Messages.API_ERROR_MSG);
            return result;
        }
        return result;
    }

 
}
