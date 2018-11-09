package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.quark.share.model.dataobj.*;
import com.quarkdata.quark.share.model.vo.GroupSearchVO;
import com.quarkdata.quark.share.model.vo.UserSearchVO;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.dal.dao.DocumentMapper;
import com.quarkdata.yunpan.api.dal.dao.ExternalUserMapper;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.DepartmentApi;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.GroupApi;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.UsersApi;
import com.quarkdata.yunpan.api.model.common.*;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.dataobj.ExternalUser;
import com.quarkdata.yunpan.api.model.dataobj.ExternalUserExample;
import com.quarkdata.yunpan.api.model.vo.MemberInfoVO;
import com.quarkdata.yunpan.api.service.UserService;
import com.quarkdata.yunpan.api.util.MD5Encyption;
import com.quarkdata.yunpan.api.util.ResultUtil;
import com.quarkdata.yunpan.api.util.SignatureUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by xiexl on 2018/1/18.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersApi usersApi;

    @Autowired
    private GroupApi groupApi;

    @Autowired
    private DepartmentApi departmentApi;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private ExternalUserMapper externalUserMapper;

    @Autowired
    private DocumentMapper documentMapper;

    @Override
    public ResultCode getUserById(String id) {
        ResultCode<Users> resultCode = usersApi.getUserById(id);
        return resultCode;
    }

    @Override
    public ResultCode update(Users user) {
        ResultCode<Users> resultCode = usersApi.update(user);
        return resultCode;
    }

    @Override
    public ResultCode<Users> getUserByUsername(HttpServletRequest request, String username) {
        ResultCode<List<Users>> resultCode = usersApi.getUserByUsername(request, username);
        // 排除
        ResultCode<Users> result = new ResultCode<>();
        result.setCode(resultCode.getCode());
        result.setMsg(resultCode.getMsg());
        if(resultCode.getData() != null && resultCode.getData().size() > 0) {
            result.setData(resultCode.getData().get(0));
        }
        return result;
    }

    @Override
    public ResultCode<String> getNewTokenByUsername(String username, String oldToken) {
        return usersApi.getNewTokenByUsername(username, oldToken);
    }

    @Override
    public ResultCode<String> updatePassword(String userId, String oldPwd, String newPwd) {
        ResultCode<String> result = new ResultCode<>();
        //判断用户状态是否正常
        Users user = this.usersApi.getUserById(userId).getData();
        if(UserStatus.STATUSACTIVE != user.getUserStatus()) {
            result.setCode(Messages.UPDATE_PASSWORD_FAIL_CODE);
            result.setMsg(Messages.USER_STATUS_ERROR_MSG);
            return result;
        }
        //校验输入的旧密码是否与数据库里存储的相同
        String encryptPwd = MD5Encyption.getMD5Encyption(oldPwd);
        if (user.getPassword() == null || !encryptPwd.equals(user.getPassword())) {
            result.setCode(Messages.UPDATE_PASSWORD_FAIL_CODE);
            result.setMsg(Messages.ORIGINAL_PASSWORD_ERROR_MSG);
            return result;
        }
        //校验通过,更新密码(更新用户信息)
        Users userRecord = new Users();
        userRecord.setUserId(Integer.parseInt(userId));
        userRecord.setPassword(MD5Encyption.getMD5Encyption(newPwd));
        return this.usersApi.update(userRecord);
    }

    @Override
    public ResultCode<String> resetPassword(Integer userId, String username, String password, String timestampStr, String resetType, String incId) {
        ResultCode<String> result = new ResultCode<>();
        // 获取重置密码URL有效期策略，单位天
        Long validTime = Long.parseLong(Constants.RESET_PWD_URL_VALID_TIME);
        // 检查链接是否过期
        long oldTimestamp = Long.parseLong(timestampStr);
        long nowTimestamp = SignatureUtil.getCurrentTimestampMs();
        long diff = nowTimestamp - oldTimestamp;
        long validTimeLong = validTime * 24 * 60 * 60 * 1000;
        if (diff >= validTimeLong) {
            result.setCode(Messages.RESET_PASSWORD_URL_INVALID_CODE);
            result.setMsg(Messages.RESET_PASSWORD_URL_INVALID_MSG);
            return result;
        }
        if (Constants.FORGOT_PASSWORD_RESET.equals(resetType)) {
            // 重置用户密码(更新用户信息)
            Users userRecord = new Users();
            userRecord.setUserId(userId);
            userRecord.setPassword(MD5Encyption.getMD5Encyption(password));
            result = usersApi.update(userRecord);
        } else {
            result.setCode(Messages.NOT_FORGOT_PASSWORD_CODE);
            result.setMsg(Messages.NOT_FORGOT_PASSWORD_MSG);
            return result;
        }
        return result;
    }

    /**
     * 保存用户
     * @param user
     * @return
     */
    @Override
    public ResultCode<Users> save(Users user) {
        return usersApi.save(user);
    }

    @Override
    public ResultCode<String> delete(Integer id) {
        return null;
    }

    @Override
    public ResultCode<ListResult<UserSearchVO>> getUserList(Integer incId, String search, Integer pageNum, Integer pageSize, Long documentId,Boolean filter) {
        ResultCode<ListResult<UserSearchVO>> resultCode = new ResultCode<>();
        ListResult<UserSearchVO> list = new ListResult<>();
        //判断是否有非法搜索值
         if (search.indexOf("%")!=-1){
             return ResultUtil.success();
         }
        ResultCode<ListResult<Users>> result = usersApi.getUserList(incId, search, pageNum, pageSize);
        //寻找更目录创建者
        Long userId=null;
        if (documentId!=null){
            Document document = documentMapper.selectByPrimaryKey(documentId);
            String idPath = document.getIdPath();
            String substring;
            if (idPath.indexOf("/")!=idPath.lastIndexOf("/")){
                substring= idPath.substring(idPath.indexOf("/") + 1, idPath.indexOf("/", idPath.indexOf("/") + 1));
            }else {
                substring = idPath.substring(idPath.indexOf("/") + 1, idPath.length());
            }
            Document document1 = documentMapper.selectByPrimaryKey(Long.parseLong(substring));
            userId = document1.getCreateUser();
        }
        // 过滤掉系统生成的账号和根目录的创建者
        List<Users> users = result.getData().getListData();
        List<UserSearchVO> data = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(users)) {
            List<Users> users_new = new ArrayList<>();
            List<Users> users_new_not_root = new ArrayList<>();
            for(Users u: users) {
                if(!Constants.USER_SOURCE_GENERATE_ACCOUNT.equals(u.getSource())
                        && !u.getUserId().equals(UserInfoUtil.getUserInfoVO().getUser().getUserId())) {
                    if (userId!=null && u.getUserId().longValue()!=userId){
                        users_new.add(u);
                    }else if (userId==null){
                        users_new.add(u);
                    }
                }
            }

            //如果是组织文件归档文件过滤系统管理员
            if (filter && CollectionUtils.isNotEmpty(users_new)){
                //查找所有的系统管理员
                ResultCode<List<Long>> adminUserListByFilter = usersApi.getAdminUserListByFilter();
                for(Users u1: users_new) {
                    if (!adminUserListByFilter.getData().contains(u1.getUserId().longValue())){
                        users_new_not_root.add(u1);
                    }
                }
            }

            if (!filter){
                users_new_not_root=users_new;
            }
            if(CollectionUtils.isNotEmpty(users_new_not_root)) {
                for(Users u: users_new_not_root) {
                    UserSearchVO userSearchVO = new UserSearchVO();
                    BeanUtils.copyProperties(u, userSearchVO);
                    userSearchVO.setId(u.getUserId().longValue());
                    userSearchVO.set_type(Constants.TYPE_USER);
                    // 查询用户部门信息
                    Department department = this.departmentApi.getDepartmentByUserId(Long.parseLong(u.getUserId().toString()));
                    userSearchVO.setDeptNamePath(getDeptNamePath(department).toString());
                    data.add(userSearchVO);
                }
            }
            list.setListData(data);
            list.setTotalNum(result.getData().getTotalNum());
            resultCode.setData(list);
        }
        return resultCode;
    }

    @Override
    public List<UserSearchVO> getAllUsersByDepartmentId(Integer incId, Long deptId, String search, Integer pageNum, Integer pageSize) {
//        List<Users> users = this.recursiveGetUsersByDepartmentId(incId, new ArrayList<Users>(), deptId.toString(), search);
        if(deptId != null && deptId == 0) {
            deptId = 1L;
        }
        List<UserSearchVO> data = new ArrayList<>();
        List<Users> users = this.usersApi.getUsersByDeptIdRecursive(incId, deptId.toString(), search, pageNum, pageSize);
        if(CollectionUtils.isNotEmpty(users)) {
            List<Users> users_new = new ArrayList<>();
            for(Users u: users) {
                if(!Constants.USER_SOURCE_GENERATE_ACCOUNT.equals(u.getSource()) && !u.getUserId().equals(UserInfoUtil.getUserInfoVO().getUser().getUserId())) {
                    users_new.add(u);
                }
            }
            if(CollectionUtils.isNotEmpty(users_new)) {
                for(Users u: users_new) {
                    UserSearchVO userSearchVO = new UserSearchVO();
                    BeanUtils.copyProperties(u, userSearchVO);
                    // 查询用户部门信息
                    Department department = this.departmentApi.getDepartmentByUserId(Long.parseLong(u.getUserId().toString()));
                    userSearchVO.setDeptNamePath(getDeptNamePath(department).toString());
                    data.add(userSearchVO);
                }
            }
        }
        return data;
    }

    @Override
    public MemberInfoVO getAllUsersAndGroupsByGroupId(Integer incId, Long groupId, String search, Integer pageNum, Integer pageSize) {
        if(groupId != null && groupId == 0) {
            groupId = 1L;
        }
        // 用户
        List<Users> users = this.recursiveGetUsersByGroupId(incId, new ArrayList<Users>(), groupId.toString(), search);
        List<UserSearchVO> newUsers = null;
        if(CollectionUtils.isNotEmpty(users)) {
            // 去重
            for(int i = 0; i< users.size() - 1; i++) {
                for(int j = users.size() - 1; j > i; j--) {
                    if(users.get(j).getUserId().equals(users.get(i).getUserId())) {
                        users.remove(j);
                    }
                }
            }
            newUsers = new ArrayList<>();
            for(Users u: users) {
                UserSearchVO userSearchVO = new UserSearchVO();
                BeanUtils.copyProperties(u, userSearchVO);
                userSearchVO.setId(u.getUserId().longValue());
                userSearchVO.set_type(Constants.TYPE_USER);
                newUsers.add(userSearchVO);
            }
        }
        List<UserSearchVO> data = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(users)) {
            for(Users u: users) {
                UserSearchVO userSearchVO = new UserSearchVO();
                BeanUtils.copyProperties(u, userSearchVO);
                // 查询用户所在组信息
                List<Group> groupList = this.groupApi.getGroupByUserId(Long.parseLong(u.getUserId().toString()));
                if(CollectionUtils.isNotEmpty(groupList)) {
                    List<String> groupNames = new ArrayList<>();
                    for(Group g: groupList) {
                        groupNames.add(g.getGroupName());
                    }
                    userSearchVO.setGroupNames(groupNames);
                }
                data.add(userSearchVO);
            }
        }
        // 用户组
        List<Group> groups = this.recursiveGetGroupsByGroupId(incId, new ArrayList<Group>(), groupId.toString(), search);
        List<GroupSearchVO> newGroups = null;
        if(CollectionUtils.isNotEmpty(groups)) {
            // 去重
            for(int i = 0; i< groups.size() - 1; i++) {
                for(int j = groups.size() - 1; j > i; j--) {
                    if(groups.get(j).getId().equals(groups.get(i).getId())) {
                        groups.remove(j);
                    }
                }
            }
            newGroups = new ArrayList<>();
            for(Group g: groups) {
                GroupSearchVO groupSearchVO = new GroupSearchVO();
                BeanUtils.copyProperties(g, groupSearchVO);
                groupSearchVO.set_type(Constants.TYPE_GROUP);
                newGroups.add(groupSearchVO);
            }
        }
        MemberInfoVO memberInfoVO = new MemberInfoVO(newUsers, newGroups, null);
        return memberInfoVO;
    }

    @Override
    public ExternalUser getExternalUserByUserId(Integer userId) {
        ExternalUserExample example = new ExternalUserExample();
        example.createCriteria().andUserIdEqualTo(Long.parseLong(userId.toString()));
        List<ExternalUser> externalUsers = this.externalUserMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(externalUsers)) {
            return externalUsers.get(0);
        } else {
            return null;
        }

    }

    /**
     * 获取密码策略字符串
     *
     * @return
     */
    private String getPasswordPolicyString() {

        // 8_1_1_1_0：密码长度_大写字母_小写字母_数字_特殊字符
        String pwdPolicy = "";

        Integer upperCase = 0;
        Integer lowerCase = 0;
        Integer number = 0;
        Integer specialCharacter = 0;

        if (Constants.UPERCASE == true) {
            upperCase = 1;
        }

        if (Constants.LOWERCASE == true) {
            lowerCase = 1;
        }

        if (Constants.HAVE_NUMBER == true) {
            number = 1;
        }

        if (Constants.SPECIAL_CHARACER == true) {
            specialCharacter = 1;
        }

        pwdPolicy = Constants.PWD_MIN_LENGTH + "_" + upperCase + "_" + lowerCase + "_" + number
                + "_" + specialCharacter;

        return pwdPolicy;
    }

    /**
     * 获取邮件配置
     */
    private Map<String, String> getMailConfig() {
        InputStream in = null;
        Properties properties = new Properties();
        Map<String, String> mailConfigMap = new HashMap<String, String>();
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("config/config.properties");
            properties.load(in);
            String mailHost = properties.getProperty("mail.host");
            String mailUsername = properties.getProperty("mail.username");
            String mailPwd = properties.getProperty("mail.password");
            mailConfigMap.put("mailHost", mailHost);
            mailConfigMap.put("mailUsername", mailUsername);
            mailConfigMap.put("mailPwd", mailPwd);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert in != null;
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mailConfigMap;
    }

    /**
     * 发送忘记密码邮件
     *
     * @param username----邮箱
     * @return
     */
    @Override
    public ResultCode<String> sendForgotPwdMail(String username, Integer incId) {
        ResultCode<String> result = new ResultCode<>();

        // 获取构造url需要的参数
        // 获取密码设置的限制测策略
        // 密码长度至少6位，必须有大写字母，必须有小写字母，必须有数字，有特殊字符
        String passwordRule = getPasswordPolicyString();
        String resetType = Constants.FORGOT_PASSWORD_RESET;

        // 获取链接有效期策略，单位小时
        String validTime = Constants.RESET_PWD_URL_VALID_TIME;//1
        String[] receiver = {username};

        Map<String, String> mailConfMap = getMailConfig();

        result = emailSender.sendForgotPwdMail(mailConfMap, username, receiver, validTime,
                passwordRule, incId, resetType);
        return result;
    }

    private List<Users> recursiveGetUsersByDepartmentId(Integer incId, List<Users> users, String deptId, String search) {
        List<Users> usersByDeptId = this.usersApi.getUsersByDeptId(incId, deptId.toString(), search);
        if(CollectionUtils.isNotEmpty(usersByDeptId)) {
            users.addAll(usersByDeptId);
        }
        List<Department> departments = this.departmentApi.getDeptmentsByDeptId(incId, null, deptId.toString());
        if(CollectionUtils.isNotEmpty(departments)) {
            for(Department department: departments) {
                this.recursiveGetUsersByDepartmentId(incId, users, department.getId().toString(), search);
            }
        }
        return users;
    }
    private List<Users> recursiveGetUsersByGroupId(Integer incId, List<Users> users, String groupId, String search) {
        List<Users> usersByGroupId = this.usersApi.getUsersByGroupId(incId, groupId.toString(), search);
        if(CollectionUtils.isNotEmpty(usersByGroupId)) {
            users.addAll(usersByGroupId);
        }
        List<Group> groups = this.groupApi.getGroupByGroupId(incId, groupId, search, null, null);
        if(CollectionUtils.isNotEmpty(groups)) {
            for(Group g: groups) {
                this.recursiveGetUsersByGroupId(incId, users, g.getId().toString(), search);
            }
        }
        return users;
    }
    private List<Group> recursiveGetGroupsByGroupId(Integer incId, List<Group> groups, String groupId, String search) {
        List<Group> groupList = this.groupApi.getGroupByGroupId(incId, groupId, search, null, null);
        if(CollectionUtils.isNotEmpty(groupList)) {
            groups.addAll(groupList);
            for(Group g: groupList) {
                this.recursiveGetGroupsByGroupId(incId, groups, g.getId().toString(), search);
            }
        }
        return groups;
    }

    @Override
    public UserSearchVO getUserSearchVOById(String userId) {
        UserSearchVO userSearchVO = null;
        ResultCode<Users> resultUser = usersApi.getUserById(userId);
        if(resultUser != null){
            userSearchVO = new UserSearchVO();
            Users user = resultUser.getData();
            BeanUtils.copyProperties(user, userSearchVO);
            userSearchVO.setId(user.getUserId().longValue());
            userSearchVO.set_type(Constants.TYPE_USER);
            Department department =  this.departmentApi.getDepartmentByUserId(user.getUserId().longValue());
            userSearchVO.setDeptNamePath(getDeptNamePath(department).toString());
            ResultCode<OneshareRole> roleResultCode =  this.usersApi.getUserRoleById(userId);
            if(roleResultCode != null && roleResultCode.getData() != null) {
                userSearchVO.setRoleId(roleResultCode.getData().getId());
            }
        }
        return  userSearchVO;
    }

    private  StringBuilder getDeptNamePath(Department department){
        StringBuilder namePath = new StringBuilder();
        if(department != null) {
            namePath.append(department.getName());
            namePath = this.getAllFatherDepartmentNamePath(namePath, department);
        }
        return  namePath;
    }

    private StringBuilder getAllFatherDepartmentNamePath(StringBuilder namePath, Department department) {
        if(department.getFatherid() != null) {
            Department fatherDept = this.departmentApi.getDepartmentById(department.getFatherid());
            if(fatherDept != null && !fatherDept.getCode().equals("999999")) {
                namePath.insert(0, fatherDept.getName() + "/");
            }
            if(fatherDept.getFatherid() != null) {
                this.getAllFatherDepartmentNamePath(namePath, fatherDept);
            }
        }
        return namePath;
    }
}
