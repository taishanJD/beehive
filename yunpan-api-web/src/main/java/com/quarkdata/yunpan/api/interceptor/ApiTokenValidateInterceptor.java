package com.quarkdata.yunpan.api.interceptor;

import com.quarkdata.yunpan.api.dal.api.UserInfoRedis;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.TokenApi;
import com.quarkdata.yunpan.api.model.common.Constants;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.util.JedisUtils;
import com.quarkdata.yunpan.api.util.RequestUtils;
import com.quarkdata.yunpan.api.util.SpringContextHolder;
import com.quarkdata.yunpan.api.util.common.mapper.JsonMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * 验证token
 * @author wujianbo
 */
public class ApiTokenValidateInterceptor implements HandlerInterceptor {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TokenApi tokenApi;

    @Value("${isLinux}")
    private Boolean isLinux;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
         if(isLinux && !GetSpaceEnough()) {
            ResultCode<String> resultCode = new ResultCode<>();
            resultCode.setCode(Messages.NO_SPACE_FAIL_CODE);
            resultCode.setMsg(Messages.NO_SPACE_FAIL_MSG);
            sendFailRes(resultCode, response);
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            return false;
        }

        if(StringUtils.isNotBlank(RequestUtils.isClient(request))) {
            // Client
            String authorization = request.getHeader("Authorization");
            String authorization1 = request.getParameter("Authorization");
            if (StringUtils.isBlank(authorization) && StringUtils.isBlank(authorization1)){
                //没有token
                ResultCode<String> resultCode = new ResultCode<>();
                resultCode.setCode(Messages.API_AUTHENTICATION_FAILED_CODE);
                resultCode.setMsg(Messages.API_AUTHENTICATION_FAILED_MSG);
                sendFailRes(resultCode, response);
                return false;
            }

            String token = StringUtils.isBlank(authorization) ? authorization1 : authorization;
            ResultCode<Map<String, Long>> resultCode = tokenApi.validateToken(token);
            if (resultCode.getCode().equals(Messages.SUCCESS_CODE)){
                UserInfoRedis userInfo = SpringContextHolder.getBean(UserInfoRedis.class);
                userInfo.setUserId(Integer.parseInt(resultCode.getData().get("userId")+""));
                userInfo.setIncId(Integer.parseInt(resultCode.getData().get("incId")+""));
                return true;
            } else {
                logger.warn("token 验证失败{}", JsonMapper.toJsonString(resultCode));
                sendFailRes(resultCode, response);
                return false;
            }

        } else {
            // Browser
            HttpSession session = request.getSession(false);

            if(session == null) {
                String jsessionid = request.getParameter("JSESSIONID");
                if(StringUtils.isNotBlank(jsessionid)) {
                    Map<String, String> map = JedisUtils.getMap("spring:session:sessions:" + jsessionid);
                    if(map != null) {
                        String incId = map.get("sessionAttr:incId");
                        String userId = map.get("sessionAttr:userId");
                        if(incId != null && userId != null) {
                            UserInfoRedis userInfo = SpringContextHolder.getBean(UserInfoRedis.class);
                            userInfo.setIncId(Integer.parseInt(incId));
                            userInfo.setUserId(Integer.parseInt(userId));
                        }
                        return true;
                    }
                }
                ResultCode<String> resultCode = new ResultCode<>();
                resultCode.setCode(Messages.API_AUTHENTICATION_FAILED_CODE);
                resultCode.setMsg(Messages.API_AUTHENTICATION_FAILED_MSG);
                sendFailRes(resultCode, response);
                return false;
            } else {
                // 查看session中是否存在关键字段
                Integer incId = (Integer) session.getAttribute(Constants.SESSION_ATTRIBUTE_INCID);
                Integer userId = (Integer) session.getAttribute(Constants.SESSION_ATTRIBUTE_USERID);
                if (incId == null || userId == null){
                    ResultCode<String> resultCode = new ResultCode<>();
                    resultCode.setCode(Messages.API_AUTHENTICATION_FAILED_CODE);
                    resultCode.setMsg(Messages.API_AUTHENTICATION_FAILED_MSG);
                    sendFailRes(resultCode, response);
                    return false;
                }else {
                    UserInfoRedis userInfo = SpringContextHolder.getBean(UserInfoRedis.class);
                    userInfo.setIncId(incId);
                    userInfo.setUserId(userId);
                }
            }
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    // df -hl 查看硬盘空间是否已满
    private  Boolean GetSpaceEnough(){
        try {
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec("df -hl");
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(
                        p.getInputStream()));
                String str = null;
                String[] strArray = null;
                int line = 0;
                while ((str = in.readLine()) != null) {
                    line++;
                    if (line != 2) {
                        continue;
                    }
                    int m = 0;
                    strArray = str.split(" ");
                    for (String para : strArray) {
                        if (para.trim().length() == 0)
                            continue;
                        ++m;
                        int percentIndex = para.indexOf('%');
                        if (percentIndex >= 0) {
                            if (m == 5) {
                                String percentStr = para.substring(0, percentIndex);
                                return new Long(percentStr) < 98;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.warn(e.toString());
            } finally {
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  true;
    }

    private void sendFailRes(ResultCode resultCode, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpStatus.SC_FORBIDDEN);
        response.getWriter().print(JsonMapper.toJsonString(resultCode));
    }


}
