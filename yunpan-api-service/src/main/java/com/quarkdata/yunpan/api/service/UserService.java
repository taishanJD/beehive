package com.quarkdata.yunpan.api.service;

import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.quark.share.model.vo.UserSearchVO;
import com.quarkdata.yunpan.api.model.common.ListResult;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.ExternalUser;
import com.quarkdata.yunpan.api.model.vo.MemberInfoVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by xiexl on 2018/1/18.
 */
public interface UserService {

    /**
     * 根据ID获取用户信息
     * @param id
     * @return
     */
    ResultCode getUserById(String id);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    ResultCode update(Users user);

    /**
     * 用户忘记密码，发送重置密码邮件
     * @param username
     * @return
     */
    public ResultCode<String> sendForgotPwdMail(String username, Integer incId);


    /**
     * 根据用户名查询用户
     *
     * @param request
     * @param username
     * @return
     */
    ResultCode<Users> getUserByUsername(HttpServletRequest request, String username);

    /**
     * 获取指定用户的新token
     * @param username
     * @param oldToken
     * @return
     */
    ResultCode<String> getNewTokenByUsername(String username, String oldToken);

    /**
     * 修改密码
     * @param userId
     * @param oldPwd
     * @param newPwd
     * @return
     */
    ResultCode<String> updatePassword(String userId,String oldPwd,String newPwd);

    /**
     * 重置密码
     *
     * @param userId
     * @param username
     * @param password
     * @param timestampStr
     * @param resetType
     * @return
     */
    ResultCode<String> resetPassword(Integer userId, String username, String password, String timestampStr,
                                     String resetType, String incId);

    ResultCode<Users> save(Users user);

    /**
     * 删除一个用户
     */
    ResultCode<String> delete(Integer id);

    /**
     * 获取用户列表
     * @param incId
     * @param search
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResultCode<ListResult<UserSearchVO>> getUserList(Integer incId, String search, Integer pageNum, Integer pageSize ,Long documentId,Boolean filter);


    /**
     * 获取部门下所有用户(包括所有子部门)
     * @param incId
     * @param deptId
     * @param search
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<UserSearchVO> getAllUsersByDepartmentId(Integer incId, Long deptId, String search, Integer pageNum, Integer pageSize);

    /**
     * 获取用户组下所有用户(包括所有子用户组)
     * @param incId
     * @param groupId
     * @param name
     * @param pageNum
     * @param pageSize
     * @return
     */
    MemberInfoVO getAllUsersAndGroupsByGroupId(Integer incId, Long groupId, String name, Integer pageNum, Integer pageSize);

    /**
     * 获取系统生成账号的过期时间
     * @param userId
     * @return
     */
    ExternalUser getExternalUserByUserId(Integer userId);

    UserSearchVO getUserSearchVOById(String userId);
}
