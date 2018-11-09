package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.quark.share.model.dataobj.Incorporation;
import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.service.PushService;
import com.quarkdata.yunpan.api.util.CookieUtils;
import com.quarkdata.yunpan.api.util.ResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * author:liuda
 * Date:2018/9/11
 * Time:13:45
 */
@RestController
@RequestMapping(RouteKey.PREFIX_API + "/push")
public class PushController extends BaseController {

    @Autowired
    private PushService pushService;

    @RequestMapping(value = "/getUdid" , method = RequestMethod.GET)
    public ResultCode getUdid(HttpServletResponse response, HttpServletRequest request,
                              @RequestParam(value = "imei") String IMEI,
                              @RequestParam(value = "meid") String MEID,
                              @RequestParam(value = "sn") String SN,
                              @RequestParam(value = "identity", required = false) String identity){
        ResultCode<String> result = new ResultCode<>();
        try {
            StringBuffer source = new StringBuffer(120);
            if (StringUtils.isNotBlank(IMEI)) {
                source.append(IMEI);
            }
            if (StringUtils.isNotBlank(MEID)) {
                source.append(MEID);
            }
            if (StringUtils.isNotBlank(SN)) {
                source.append(SN);
            }
            if (source.length() <= 0) {
                return ResultUtil.error(Messages.REGISTER_PUSH_HIATUS_CODE,Messages.REGISTER_PUSH_HIATUS_MSG);
            }
            Incorporation incorporation = UserInfoUtil.getIncorporation();
            Users user = UserInfoUtil.getUserInfoVO().getUser();
            // TODO  推送测试，后续添加完整推送规则
            return ResultUtil.success(
                    pushService.getDeviceUDID(
                            null,null,identity,IMEI,MEID,SN,null,incorporation,user));
        }catch (Exception e){
            return ResultUtil.error(Messages.REGISTER_PUSH_ERROR_CODE,Messages.REGISTER_PUSH_ERROR_MSG);
        }
    }

    @RequestMapping(value = "/setIosToken" ,method = RequestMethod.GET)
    public ResultCode setIosToken(@RequestParam(value = "udid") String token){
        try {
            if (StringUtils.isBlank(token)){
                return ResultUtil.error(Messages.REGISTER_IOS_ERROR_NULL_CODE,Messages.REGISTER_IOS_ERROR_NULL_MSG);
            }
            Users user = UserInfoUtil.getUserInfoVO().getUser();
            return pushService.setIosToken(token,user);
        }catch (Exception e){
            return ResultUtil.error(Messages.REGISTER_IOS_ERROR_CODE,Messages.REGISTER_IOS_ERROR_MSG);
        }
    }

    @RequestMapping(value = "/getWebUdid",method = RequestMethod.GET)
    public ResultCode getWebUdid(HttpServletRequest request){
        try {
//            String udid = request.getParameter("JSESSIONID");
            String udid = CookieUtils.getCookie(request, "JSESSIONID");
            if (StringUtils.isNotBlank(udid)){
                Users user = UserInfoUtil.getUserInfoVO().getUser();
                String webUdid = pushService.getWebUdid(user,udid);
                return ResultUtil.success(webUdid);
            }else {
                return ResultUtil.error(Messages.REGISTER_PUSH_ERROR_CODE,Messages.REGISTER_PUSH_ERROR_MSG);
            }
        }catch (Exception e){
            return ResultUtil.error(Messages.REGISTER_PUSH_ERROR_CODE,Messages.REGISTER_PUSH_ERROR_MSG);
        }
    }
}
