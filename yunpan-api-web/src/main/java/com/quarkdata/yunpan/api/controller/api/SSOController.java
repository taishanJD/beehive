package com.quarkdata.yunpan.api.controller.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.quarkdata.auth.common.ConstCode;
import com.quarkdata.auth.common.ServerResponse;
import com.quarkdata.auth.common.SessionManage;
import com.quarkdata.yunpan.api.dal.rest.onespace.OneSpaceApiConstants;
import com.quarkdata.yunpan.api.model.common.Constants;
import com.quarkdata.yunpan.api.util.HttpTookit;
import com.quarkdata.yunpan.api.util.JedisUtils;
import com.quarkdata.yunpan.api.util.common.Exception.YCException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yanyq1129@thundersoft.com on 2018/9/13.
 * 供oneSpace单点登录使用
 */
@Controller
public class SSOController {

    @Value("${auth.server.url}")
    private String ONESPACE_URL;

    @Value("${local.index.url}")
    private String DEFAULT_INDEX;

    @RequestMapping(value = "/signin", method = RequestMethod.GET)
    public void signin(HttpServletRequest request,
                       HttpServletResponse response,
                       @RequestParam(value = "service_ticket") String serviceTicket) throws IOException {

        Map<String, String> params = new HashMap<>();
        params.put("service_ticket", serviceTicket);
        String result = HttpTookit.doGet(ONESPACE_URL + OneSpaceApiConstants.VERIFY, params);
        ServerResponse<Map<String, Object>> resultMap = JSON.parseObject(result, new TypeReference<ServerResponse<Map<String, Object>>>() {
        });

        if (resultMap != null && "200".equals(resultMap.getCode())) {

            Map<String, Object> data = resultMap.getData();
            HttpSession session = request.getSession(true);
            session.setAttribute(ConstCode.SERVICE_TICKET, serviceTicket);
            session.setAttribute("userId", data.get("userId"));
            session.setAttribute("incId", data.get("incId"));
            session.setAttribute("adminId", data.get("adminId"));
            session.setAttribute("adminPlatfrom", data.get("adminPlatfrom"));

            SessionManage.MANAGED_SESSIONS.put(serviceTicket, session);
        } else {
            throw new YCException("登录失败", 1);
        }

        response.setHeader("cache-control", "no-cache");
        response.sendRedirect(DEFAULT_INDEX);

    }

    @RequestMapping(value = "/signout", method = RequestMethod.GET)
    public String signout(HttpServletRequest request,
                          HttpServletResponse response,
                          @RequestParam(value = "service_ticket") String serviceTicket) {
        Object userId = null;

        HttpSession session = SessionManage.MANAGED_SESSIONS.get(serviceTicket);
        if (null != session) {
            userId = session.getAttribute("userId");
            session.invalidate();
        }

        // 清除redis缓存
        if (userId != null) {
            JedisUtils.del(Constants.ONESHARE_REDIS_PREFIX + Constants.ONESHARE_REDIS_DELIMITER + Constants.ONESHARE_REDIS_USER_INFO_VO + Constants.ONESHARE_REDIS_DELIMITER + userId);
        }
        return "success";
    }
}
