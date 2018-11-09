package com.quarkdata.yunpan.api.controller.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quarkdata.quark.share.model.dataobj.Incorporation;
import com.quarkdata.quark.share.model.dataobj.OneshareRole;
import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.quark.share.model.vo.UserVo;
import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.UsersApi;
import com.quarkdata.yunpan.api.log.LogAnnotation;
import com.quarkdata.yunpan.api.model.common.*;
import com.quarkdata.yunpan.api.model.dataobj.DocumentPermission;
import com.quarkdata.yunpan.api.service.AdminService;
import com.quarkdata.yunpan.api.service.DocumentPermissionService;
import com.quarkdata.yunpan.api.service.DocumentService;
import com.quarkdata.yunpan.api.service.UserService;
import com.quarkdata.yunpan.api.util.common.mapper.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author yanyq1129@thundersoft.com
 * @Description 管理员
 */
@Controller
@RequestMapping(RouteKey.PREFIX_API + "/admin")
public class AdminController extends BaseController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private DocumentPermissionService documentPermissionService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private UsersApi usersApi;

    @Autowired
    private UserService userService;

    /**
     * 获取管理员列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public ResultCode<ListResult<UserInfoVO>> getAdminList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, String username) {
        ResultCode<ListResult<UserInfoVO>> result = new ResultCode<ListResult<UserInfoVO>>();
        try {
            // 当前企业ID
            Integer incId = UserInfoUtil.getIncorporation().getId();
            result = this.adminService.getAdminList(incId, pageNum, pageSize, username);
        } catch (Exception e) {
            result.setCode(1);
            result.setMsg(Messages.GET_ADMIN_LIST_FAIL_MSG);
            this.logger.error(Messages.GET_ADMIN_LIST_FAIL_MSG, e);
        }
        return result;
    }

    /**
     * 批量添加管理员
     *
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public ResultCode<Object> addAdmin(HttpServletRequest request, String userIds, Long roleId) {
        ResultCode<Object> result = new ResultCode<Object>();
        try {
            this.logger.info("批量添加管理员");
            result = this.adminService.addAdmin(request, UserInfoUtil.getIncorporation().getId(), userIds, roleId);
        } catch (Exception e) {
            result.setCode(1);
            if (roleId == 1) {
                result.setMsg(Messages.ADD_THE_SYSTEM_ADMINISTRATOR_FAIL_MSG);
                this.logger.error(Messages.ADD_THE_SYSTEM_ADMINISTRATOR_FAIL_MSG, e);
            }
            if (roleId == 2) {
                result.setMsg(Messages.ADD_THE_ORGANIZATION_ADMINISTRATOR_FAIL_MSG);
                this.logger.error(Messages.ADD_THE_ORGANIZATION_ADMINISTRATOR_FAIL_MSG, e);
            }
        }
        return result;
    }

    /**
     * 根据ID撤销系统管理员
     *
     * @param systemAdminId
     * @return
     */
    @RequestMapping(value = "/revoke", method = RequestMethod.POST)
    @ResponseBody
    public ResultCode<Object> revokeSystemAdmin(HttpServletRequest request, Long systemAdminId) {
        ResultCode<Object> result = new ResultCode<Object>();
        try {
            result = this.adminService.revokeSystemAdmin(request, UserInfoUtil.getIncorporation().getId(), systemAdminId);
            this.logger.info("=============delete_the_system_administrator=============");
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(1);
            result.setMsg(Messages.DELETE_THE_SYSTEM_ADMINISTRATOR_FAIL_MSG);
            this.logger.error(Messages.DELETE_THE_SYSTEM_ADMINISTRATOR_FAIL_MSG, e);
        }
        return result;
    }

    /**
     * 根据ID撤销组织管理员
     * @param orgAdminId
     * @param replacerId
     * @return
     */
    @RequestMapping(value = "/revoke/org", method = RequestMethod.POST)
    @ResponseBody
    public ResultCode<Object> revokeOrgAdmin(HttpServletRequest request, Long orgAdminId, Long replacerId){
        ResultCode<Object> result = new ResultCode<>();
        try {
            result = this.adminService.revokeOrgAdmin(request, Long.valueOf(UserInfoUtil.getIncorporation().getId().toString()), orgAdminId, replacerId);
            this.logger.info("==========delete_the_organization_administrator==========");
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(1);
            result.setMsg(Messages.DELETE_THE_ORGANIZATION_ADMINISTRATOR_FAIL_MSG);
            this.logger.error(Messages.DELETE_THE_ORGANIZATION_ADMINISTRATOR_FAIL_MSG, e);
        }
        return result;
    }

    /**
     * 编辑系统管理员
     *
     * @param systemAdminId
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    @LogAnnotation
    public ResultCode<Object> editSystemAdmin(HttpServletRequest request, Long systemAdminId) {
        ResultCode<Object> result = new ResultCode<Object>();
        try {
            result = this.adminService.editSystemAdmin(request, UserInfoUtil.getIncorporation().getId(), systemAdminId);
            this.logger.info("===========delete_the_system_administrator==========");
            this.logger.info("===========add_organization_administrator===========");
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(1);
            result.setMsg(Messages.EDIT_THE_SYSTEM_ADMINISTRATOR_FAIL_MSG);
            this.logger.error(Messages.EDIT_THE_SYSTEM_ADMINISTRATOR_FAIL_MSG, e);
        }
        return result;
    }

    /**
     * 编辑组织管理员
     * @param orgAdminId
     * @param replacerId
     * @return
     */
    @RequestMapping(value = "/edit/org", method = RequestMethod.POST)
    @ResponseBody
    @LogAnnotation
    public ResultCode<Object> editOrgAdmin(HttpServletRequest request, Long orgAdminId, Long replacerId){
        ResultCode<Object> result = new ResultCode<>();
        try {
            result = this.adminService.editOrgAdmin(request, Long.valueOf(UserInfoUtil.getIncorporation().getId().toString()), orgAdminId, replacerId);
            this.logger.info("==========delete_the_organization_administrator==========");
            this.logger.info("==========add_system_administrator==========");
            this.logger.info("==========add_organization_administrator==========");
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(1);
            result.setMsg(Messages.EDIT_THE_ORGANIZATION_ADMINISTRATOR_FAIL_MSG);
            this.logger.error(Messages.EDIT_THE_ORGANIZATION_ADMINISTRATOR_FAIL_MSG, e);
        }
        return result;
    }



    // 根据用户名模糊查询普通用户(非云盘管理员)
    @RequestMapping(value = "/check_by_username", method = RequestMethod.GET)
    @ResponseBody
    public ResultCode<List<Users>> findUserByUserName(String userName,
                                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        ResultCode<List<Users>> result = new ResultCode<List<Users>>();
        try {
            // 当前企业ID
            Integer incId = UserInfoUtil.getIncorporation().getId();
            if (org.apache.commons.lang3.StringUtils.isNotBlank(userName)) {
                result = this.adminService.findUserByUserName(incId, userName, pageNum, pageSize);
            }
        } catch (Exception e) {
            result.setCode(1);
            result.setMsg(Messages.FIND_USER_FAIL_MSG);
            this.logger.error(Messages.FIND_USER_FAIL_MSG, e);
        }
        return result;
    }

    // 查询当前登录用户信息
    @RequestMapping(value = "/check_user_info", method = RequestMethod.GET)
    @ResponseBody
    public ResultCode<UserVo> getUserList() {
        ResultCode<UserVo> result = new ResultCode<>();
        try {
            this.logger.info("获取当前登录用户信息");
            UserInfoVO userInfoVO = UserInfoUtil.getUserInfoVO();
            Users user = userInfoVO.getUser();
            user.setPassword(null);
            Incorporation incorporation = UserInfoUtil.getIncorporation();
            Incorporation incorporation_simple = new Incorporation();
            incorporation_simple.setName(incorporation.getName());
            if(userInfoVO != null){
                UserVo userVo=new UserVo();//用于返回
                //若source=oneshre
                if (Constants.USER_SOURCE_OUTSIDE_ACCOUNT.equals(userInfoVO.getUser().getSource())){
                    //查出外部账号对应的目录id
                    DocumentPermission documentPermission=documentPermissionService.getDocPermissionByRidAndType
                            (userInfoVO.getUser().getUserId().longValue(),DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_GENERATE_ACCOUNT);
                    if (documentPermission!=null && documentPermission.getDocumentId()!=null){
                        //根据documentid获得document
                       com.quarkdata.yunpan.api.model.dataobj.Document document=documentService.getDocumentById(documentPermission.getDocumentId());
                        userVo.setDocument(document);
                    }

                }
                JsonMapper jsonMapper = new JsonMapper();
                jsonMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
                userVo.setUser((Users) jsonMapper.fromJsonString(jsonMapper.toJson(user), Users.class));
                userVo.setGroupsList(userInfoVO.getGroupsList());
                userVo.setDepartment(userInfoVO.getDepartment());
                OneshareRole role = new OneshareRole();
                if(userInfoVO.getAdmin() != null) {
                    role.setId(1L);
                    role.setRoleName("系统管理员");
                } else {
                    role.setId(userInfoVO.getRole().getId());
                    role.setRoleName(userInfoVO.getRole().getRoleName());
                }
                userVo.setRole(role);
                userVo.setIncorporation(incorporation_simple);

                result.setData(userVo);
            }
        } catch (Exception e) {
            result.setCode(1);
            result.setMsg(Messages.FIND_CURRENT_USER_FAIL_MSG);
            this.logger.error(Messages.FIND_CURRENT_USER_FAIL_MSG, e);
            e.printStackTrace();
        }
        return result;
    }

}
