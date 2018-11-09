package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.TokenApi;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.UsersApi;
import com.quarkdata.yunpan.api.log.LogAnnotation;
import com.quarkdata.yunpan.api.model.common.Constants;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.ExternalUser;
import com.quarkdata.yunpan.api.service.UserService;
import com.quarkdata.yunpan.api.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录
 */
@Controller
public class LoginController extends BaseController{

    @Autowired
    private TokenApi tokenApi;

    @Autowired
    private UserService userService;

    @Autowired
    private UsersApi usersApi;

    @LogAnnotation
    @ResponseBody
    @RequestMapping(value = "/login")
    public ResultCode<String> login(HttpServletRequest request, HttpServletResponse response, String userName, String password, String isLDAP){
        ResultCode<String> resultCode = new ResultCode<>();
        if (StringUtils.isEmpty(userName)){
            resultCode.setCode(Messages.MISSING_INPUT_CODE);
            resultCode.setMsg(Messages.MISSING_INPUT_MSG + " : userName");
            return resultCode;
        }
        if (StringUtils.isEmpty(password)){
            resultCode.setCode(Messages.MISSING_INPUT_CODE);
            resultCode.setMsg(Messages.MISSING_INPUT_MSG + " : password");
            return resultCode;
        }
        try {
            if ("true".equals(isLDAP)) {
                BASE64Decoder decoder = new BASE64Decoder();
                char i = password.charAt(0);
                char j = password.charAt(password.length() - 1);
                String k = password.substring(1, password.length() - 1);
                String key = j + k + i;
                StringBuffer buffer = new StringBuffer(key);
                String userPassword = buffer.reverse().toString();
                password = new String(decoder.decodeBuffer(userPassword));
                resultCode = tokenApi.login(request, userName, password);
            } else {
                // 如果是系统账号判断是否过期
                List<Users> users = this.usersApi.getUserByUsername(request, userName).getData();
                if(CollectionUtils.isNotEmpty(users) && Constants.USER_SOURCE_GENERATE_ACCOUNT.equals(users.get(0).getSource())) {
                    ExternalUser externalUser = this.userService.getExternalUserByUserId(users.get(0).getUserId());
                    if(externalUser != null && externalUser.getExpiryDate().before(new Date())) {
                        resultCode.setCode(Messages.EXTERNAL_USER_EXPIRE_CODE);
                        resultCode.setMsg(Messages.EXTERNAL_USER_EXPIRE_MSG);
                        return resultCode;
                    }
                }
                resultCode = tokenApi.login(request, userName, password);
            }

            // 记录操作日志
            /*if(resultCode.getCode() == 0) {
                LogUtils.addLog(request, ActionType.LOGIN, userName);
            }*/

        } catch (Exception e){
            logger.error("登录异常", e);
            resultCode.setCode(Messages.API_ERROR_CODE);
            resultCode.setMsg(Messages.API_ERROR_MSG);
        }
        return resultCode;
    }

    @LogAnnotation
    @ResponseBody
    @RequestMapping(value = "/logout")
    public ResultCode<String> logout(HttpServletRequest request, HttpServletResponse response) {
        ResultCode<String> resultCode = new ResultCode<>();
        String authorization = request.getHeader("Authorization");
        try {
            // 记录操作日志
            // LogUtils.addLog(request, ActionType.LOGOUT, UserInfoUtil.getUserInfoVO().getUser().getUserName());

            // 注释了
            tokenApi.logout(authorization);

        } catch (Exception e) {
            logger.error("登出异常", e);
            resultCode.setMsg(Messages.API_AUTHENTICATION_FAILED_MSG);
        }
        return resultCode;
    }

    /**
     *  根据用户名查询用户信息
     * @param username
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/login/sourceByName")
    public ResultCode getUser(HttpServletRequest request, HttpServletResponse response,  String username)
    {
        ResultCode<Users> resultCode=new ResultCode<Users>();
        resultCode=userService.getUserByUsername(request, username);
        if(resultCode.getData() != null) {
            Users user = new Users();
            user.setSource(resultCode.getData().getSource());
            resultCode.setData(user);
        } else {
            resultCode.setCode(Messages.USERNAME_ERROR_CODE);
            resultCode.setMsg(Messages.USERNAME_ERROR_MSG);
        }
        return resultCode;
    }

    /**
     *  根据用户名查询用户信息
     * @param username
     * @param oldToken
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/login/getNewTokenByUsername")
    public ResultCode<String> getNewTokenByUsername(HttpServletRequest request, HttpServletResponse response, String username, String oldToken) {
        ResultCode<String> resultCode = new ResultCode<>();
        resultCode = userService.getNewTokenByUsername(username, oldToken);
        return resultCode;
    }

    /**
     * 获取验证码
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/login/validateCode")
    public ResultCode<Void> getVerifyCode(HttpServletRequest request, HttpServletResponse response){
        ResultCode<Void> result=new ResultCode<>();
        RandomValidateCode randomValidateCode = new RandomValidateCode();
        try {
            randomValidateCode.getRandcode(request, response);// 输出图片方法
        } catch (Exception e) {
            result.setCode(1);
            result.setMsg(Messages.GET_VRTRIFY_CODE_FAIL_MSG);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 校验验证码
     * @param request
     * @param validateCode
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/login/checkValidateCode")
    public ResultCode<Boolean> validateCode(HttpServletRequest request,String validateCode){
        HttpSession session=request.getSession();
        ResultCode<Boolean> result=new ResultCode<>();
        if (validateCode==null ||validateCode==""){
            result.setCode(Messages.MISSING_INPUT_CODE);
            result.setMsg(Messages.MISSING_INPUT_MSG);
            return result;
        }
        String verifyStr = (String) session.getAttribute(RandomValidateCode.RANDOMCODEKEY);
        boolean right = RandomValidateCode.checkValidateCode(validateCode, verifyStr);
        if (right==true){
            result.setData(true);
        }else {
            result.setData(false);
        }
        return result;
    }

    /**
     * 发送重置密码邮件--忘记密码
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/login/resetPwdEmail")
    public ResultCode<String> sendResetPwdEmail(@RequestParam("userName") String userName,
                                                @RequestParam("validateCode") String validateCode,
                                                HttpServletRequest request) {
        HttpSession session=request.getSession();
        ResultCode<String> result=new ResultCode<>();
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(validateCode)){
            result.setCode(Messages.MISSING_INPUT_CODE);
            result.setMsg(Messages.MISSING_INPUT_MSG);
            return result;
        }
        //验证码
        String verifyStr = (String) session.getAttribute(RandomValidateCode.RANDOMCODEKEY);
        boolean right = RandomValidateCode.checkValidateCode(validateCode, verifyStr);
        if (!right){
            result.setCode(Messages.FORGET_PASSWORD_EXCEPTION_CODE);
            result.setMsg(Messages.VERIFY_CODE_ERROR_MSG);
            return result;
        }
        //校验邮箱
        boolean isEmail= EmailUtil.emailFormat(userName);
        if (!isEmail){
            result.setCode(Messages.FORGET_PASSWORD_EXCEPTION_CODE);
            result.setMsg(Messages.EMAIL_FORMAT_ERROR_MSG);
            return result;
        }
        //判断用户是否存在
        ResultCode<Users> resultUser= userService.getUserByUsername(request, userName);
        Users user=resultUser.getData();
        if (user==null){
            result.setCode(Messages.FORGET_PASSWORD_EXCEPTION_CODE);
            result.setMsg(Messages.USERNAEM_NOT_EXIST_MSG);
            return result;
        }
        //判断用户是不是激活状态
        if (!UserUtils.STATUSACTIVE.equals(user.getUserStatus())){
            result.setCode(Messages.FORGET_PASSWORD_EXCEPTION_CODE);
            result.setMsg(Messages.USERNAEM_NOT_ACTIVE_MSG);
            return result;
        }
        //发送邮件
        result=userService.sendForgotPwdMail(userName,user.getIncid());
        return result;
    }

    /**
     * 用户忘记密码--重置密码
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/login/resetPassword",method = RequestMethod.POST)
    public ResultCode<String> resetPassword(HttpServletRequest request){
        ResultCode<String> result=new ResultCode<>();
        // 重置密码，验证签名，区分申请激活与忘记密码，验证链接的时效性
        // 获取请求参数
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String timestamp = request.getParameter("timestamp");
        String incId = request.getParameter("incId");
        String sign = request.getParameter("sign");
        String resetType = request.getParameter("resetType");
        String passwordRule = request.getParameter("passwordRule");

        SignatureUtil.SignType signType = SignatureUtil.SignType.MD5;
        String privateKey = SignatureUtil.PRIVATE_KEY;

        //判断用户名和密码
        if (username==null || username=="" || password==null || password==""){
            result.setCode(Messages.MISSING_INPUT_CODE);
            result.setMsg(Messages.MISSING_INPUT_MSG);
            return result;
        }
        // 验证签名
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("username", username);
        dataMap.put("incId", incId);
        dataMap.put("timestamp", timestamp);
        dataMap.put("passwordRule", passwordRule);
        dataMap.put("resetType", resetType);
        dataMap.put("sign", sign);
        try {
            boolean isValid = SignatureUtil.isSignatureValid(dataMap, privateKey, signType);
            if (isValid == false) {
               result.setCode(Messages.USER_SIGN_EXCEPTION_CODE);
               result.setMsg(Messages.USER_SIGN_EXCEPTION_MSG);
               return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("验证签名失败");
            result.setCode(Messages.USER_SIGN_EXCEPTION_CODE);
            result.setMsg(Messages.USER_SIGN_EXCEPTION_MSG);
            return result;
        }
        //判断用户是否存在
        ResultCode<Users> resultUser= userService.getUserByUsername(request, username);
        Users user=resultUser.getData();
        if (user==null){
            result.setCode(Messages.FORGET_PASSWORD_EXCEPTION_CODE);
            result.setMsg(Messages.USERNAEM_NOT_EXIST_MSG);
            return result;
        }
        //判断用户是不是激活状态
        if (!UserUtils.STATUSACTIVE.equals(user.getUserStatus())){
            result.setCode(Messages.FORGET_PASSWORD_EXCEPTION_CODE);
            result.setMsg(Messages.USERNAEM_NOT_ACTIVE_MSG);
            return result;
        }
        //重置密码
        result=userService.resetPassword(user.getUserId(), username,password,timestamp,resetType,incId);
        return result;
    }

    /**
     * 判断系统账号是否过期
     * @param userId
     * @return
     */
    @RequestMapping("/login/expire/{userId}")
    @ResponseBody
    public ResultCode<Boolean> isExpire(@PathVariable(value = "userId") Integer userId) {
        ResultCode<Boolean> resultCode = new ResultCode<>();
        if(userId == null) {
            resultCode.setCode(Messages.MISSING_INPUT_CODE);
            resultCode.setMsg(Messages.MISSING_INPUT_MSG);
            return resultCode;
        }
        ExternalUser externalUser = this.userService.getExternalUserByUserId(userId);
        if(externalUser != null && externalUser.getExpiryDate().before(new Date())) {
            resultCode.setData(true);
        } else {
            resultCode.setData(false);
        }
        return resultCode;
    }
}
