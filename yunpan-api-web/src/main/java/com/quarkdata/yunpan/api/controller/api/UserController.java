package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.quark.share.model.vo.UserSearchVO;
import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.common.ListResult;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.vo.MemberInfoVO;
import com.quarkdata.yunpan.api.service.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiexl on 2018/1/18.
 */

@Controller
@RequestMapping(RouteKey.PREFIX_API + RouteKey.USER)
public class UserController  extends BaseController {

    @Autowired
    private UserService userService;
    /**
     *  根据主键查询用户ID
     * @param id
     * @param response
     * @return
     */
    @RequestMapping(value = "info/{id}")
    @ResponseBody
    public ResultCode getUser(@PathVariable("id") Integer id, HttpServletResponse response)
    {
        ResultCode<UserSearchVO> resultCode=new ResultCode<UserSearchVO>();
        try {
            UserSearchVO userIfon = userService.getUserSearchVOById(id.toString());
            resultCode.setData(userIfon);
        } catch (Exception e) {
            resultCode.setCode(Messages.API_ERROR_CODE);
            resultCode.setMsg(Messages.API_ERROR_MSG);
        }
        return resultCode;
    }

    /**
     * 根据主键更新用户信息
     * @param response
     * @param user
     * @return
     */
    @RequestMapping(value="/update")
    @ResponseBody
    public ResultCode updateUser(HttpServletResponse response, @RequestBody Users user)
    {
        ResultCode<String> result = new ResultCode<String>();
        result= userService.update(user);
        return  result;
    }

    /**
     * 修改密码
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public ResultCode<String> updatePassword(HttpServletRequest request, HttpServletResponse response) {
        ResultCode<String> result = new ResultCode<>();
        String userId = request.getParameter("userId");
        String oldPwd = request.getParameter("originalPwd");
        String newPwd = request.getParameter("newPwd");
        if (userId == null || userId == "" || oldPwd == null || oldPwd == "" || newPwd == null || newPwd == "") {
            result.setCode(Messages.MISSING_INPUT_CODE);
            result.setMsg(Messages.MISSING_INPUT_MSG);
            return result;
        }
        result = userService.updatePassword(userId, oldPwd, newPwd);
        return result;
    }

    /**
     * 模糊查询用户列表
     *
     * @param pageNum
     * @param pageSize
     * @param name
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public ResultCode<ListResult<UserSearchVO>> getUserList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "documentId", required = false)Long documentId,
            @RequestParam(value = "source", required = false)Integer source) {
        ResultCode<ListResult<UserSearchVO>> result = new ResultCode<>();
        try {
            // 当前企业ID
            Integer incId = UserInfoUtil.getIncorporation().getId();
            //是否过滤系统管理员
            Boolean filter = source == null?false:(source==1?false:true);
            result = this.userService.getUserList(incId, name, pageNum, pageSize,documentId,filter);
        } catch (Exception e) {
            result.setCode(1);
            result.setMsg(Messages.FIND_USER_FAIL_MSG);
            this.logger.error(Messages.FIND_USER_FAIL_MSG, e);
        }
        return result;
    }

    /**
     * 获取部门下所有用户(包括所有子部门)
     * @param deptId
     * @param name
     * @return
     */
    @RequestMapping(value = "list_by_dept")
    @ResponseBody
    public ResultCode<Map<String, Object>> getAllUsersByDepartmentId(Long deptId, String name,
                                                                     @RequestParam(defaultValue = "1") Integer pageNum,
                                                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        ResultCode<Map<String, Object>> resultCode = new ResultCode<>();
        if(null == deptId) {
            resultCode.setCode(Messages.MISSING_INPUT_CODE);
            resultCode.setMsg(Messages.MISSING_INPUT_MSG);
            return resultCode;
        }
        Integer incId = UserInfoUtil.getIncorporation().getId();
        List<UserSearchVO> users = null;
        try {
            users = this.userService.getAllUsersByDepartmentId(incId, deptId, name, pageNum, pageSize);
            if(CollectionUtils.isNotEmpty(users)) {
                Map<String, Object> data = new HashMap<>();
                data.put("users", users);
                resultCode.setData(data);
            }
        } catch (Exception e) {
            resultCode.setCode(Messages.API_ERROR_CODE);
            resultCode.setMsg(Messages.API_ERROR_MSG);
        }
        return resultCode;
    }

    /**
     * 获取部门下所有用户和用户组(包括所有子用户组)
     * @param groupId
     * @param name
     * @return
     */
    @RequestMapping(value = "list_by_group")
    @ResponseBody
    public ResultCode<MemberInfoVO> getAllUsersByGroupId(Long groupId, String name,
                                                                     @RequestParam(defaultValue = "1") Integer pageNum,
                                                                     @RequestParam(defaultValue = "1000") Integer pageSize) {
        ResultCode<MemberInfoVO> resultCode = new ResultCode<>();
        if(null == groupId) {
            resultCode.setCode(Messages.MISSING_INPUT_CODE);
            resultCode.setMsg(Messages.MISSING_INPUT_MSG);
            return resultCode;
        }
        Integer incId = UserInfoUtil.getIncorporation().getId();
        MemberInfoVO memberInfoVO = null;
        try {
            memberInfoVO = this.userService.getAllUsersAndGroupsByGroupId(incId, groupId, name, pageNum, pageSize);
            resultCode.setData(memberInfoVO);
        } catch (Exception e) {
            resultCode.setCode(Messages.API_ERROR_CODE);
            resultCode.setMsg(Messages.API_ERROR_MSG);
        }
        return resultCode;
    }


}
