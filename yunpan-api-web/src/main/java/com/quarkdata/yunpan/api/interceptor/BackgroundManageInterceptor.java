package com.quarkdata.yunpan.api.interceptor;

import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.util.common.mapper.JsonMapper;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yanyq1129@thundersoft.com on 2018/6/5.
 */
public class BackgroundManageInterceptor implements HandlerInterceptor {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       if(UserInfoUtil.isSystemAdmin()) {
           return true;
       } else {
           ResultCode<String> result = new ResultCode<>();
           result.setCode(Messages.NO_SYSTEM_ADMIN_PERMISSION_CODE);
           result.setMsg(Messages.NO_SYSTEM_ADMIN_PERMISSION_MSG);
           response.reset();
           response.setContentType("application/json");
           response.setCharacterEncoding("utf-8");
           response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
           response.getWriter().print(JsonMapper.toJsonString(result));
           return false;
       }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
