package com.quarkdata.yunpan.api.dal.api;

import com.quarkdata.quark.share.model.dataobj.*;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.DepartmentApi;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.GroupApi;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.IncorporationApi;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.UsersApi;
import com.quarkdata.yunpan.api.model.common.Constants;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.common.RoleConstants;
import com.quarkdata.yunpan.api.util.JedisUtils;
import com.quarkdata.yunpan.api.util.SpringContextHolder;
import com.quarkdata.yunpan.api.util.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class UserInfoUtil {

    private static Integer cacheSeconds;

    private static UsersApi usersApi;

    private static GroupApi groupApi;

    private static DepartmentApi departmentApi;

    private static IncorporationApi incorporationApi;

    @Value("${jwt.ttl}")
    public void setCacheSeconds(Integer cacheSeconds) {
        UserInfoUtil.cacheSeconds = cacheSeconds;
    }

    @Autowired
    public void setUsersApi(UsersApi usersApi) {
        UserInfoUtil.usersApi = usersApi;
    }

    @Autowired
    public void setGroupApi(GroupApi groupApi) {
        UserInfoUtil.groupApi = groupApi;
    }

    @Autowired
    public void setDepartmentApi(DepartmentApi departmentApi) {
        UserInfoUtil.departmentApi = departmentApi;
    }

    @Autowired
    public void setIncorporationApi(IncorporationApi incorporationApi) {
        UserInfoUtil.incorporationApi = incorporationApi;
    }

    private static UserInfo getUserInfo() {
        return SpringContextHolder.getBean(UserInfoRedis.class);
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    public static UserInfoVO getUserInfoVO() {
        UserInfoVO userInfoVO = getUserInfo().getUserInfoVO();
        int userId = SpringContextHolder.getBean(UserInfoRedis.class).getUserId();
        int incId = SpringContextHolder.getBean(UserInfoRedis.class).getIncId();
        if (userInfoVO == null && userId != 0) {
            // 用户已登录, 缓存无数据, 从mysql查询数据放入redis中
            userInfoVO = new UserInfoVO();
            //缓存用户相关信息
            Users users = usersApi.getUserById(userId + "").getData();
            if (users != null) {
                users.setUserName(UserUtils.getFinalUserNameFromUser(users));
            }
            List<Group> groups = groupApi.getGroupByUserId(Long.valueOf(userId + ""));
            Department department = departmentApi.getDepartmentByUserId(Long.valueOf(userId + ""));
            OneshareRole role = usersApi.getUserRoleById(userId + "").getData();
            Admin admin = usersApi.getOneSpaceAdminByUserId(userId).getData();
            userInfoVO.setUser(users);
            if (CollectionUtils.isEmpty(groups)) {
                List<Group> defaultGroupList = new ArrayList<Group>();
                Group defaultGroup = new Group();
                defaultGroup.setId(0);
                defaultGroup.setIncId(incId);
                defaultGroupList.add(defaultGroup);
                userInfoVO.setGroupsList(defaultGroupList);
            } else {
                userInfoVO.setGroupsList(groups);
            }
            if (null == department) {
                Department defaultDepartment = new Department();
                defaultDepartment.setId(0);
                defaultDepartment.setIncid(incId);
                userInfoVO.setDepartment(defaultDepartment);
            } else {
                userInfoVO.setDepartment(department);
            }
            userInfoVO.setRole(role);
            if(admin != null) {
                admin.setPassword(null);
                userInfoVO.setAdmin(admin);
            }
            JedisUtils.setObject(Constants.ONESHARE_REDIS_PREFIX + Constants.ONESHARE_REDIS_DELIMITER + Constants.ONESHARE_REDIS_USER_INFO_VO + Constants.ONESHARE_REDIS_DELIMITER + users.getUserId(), userInfoVO, cacheSeconds / 1000);
        } else if (userInfoVO != null && userInfoVO.getUser() != null) {
            userInfoVO.getUser().setUserName(UserUtils.getFinalUserNameFromUser(userInfoVO.getUser()));
        }
        return userInfoVO;
    }

    /**
     * 获取当前用户所在企业
     *
     * @return
     */
    public static Incorporation getIncorporation() {
        Incorporation incorporation = getUserInfo().getIncorporation();

        if (incorporation == null) {
            int incId = SpringContextHolder.getBean(UserInfoRedis.class).getIncId();
            ResultCode<Incorporation> resultCode = incorporationApi.check(Long.parseLong(incId + ""));
            if (resultCode != null && resultCode.getData() != null) {
                //缓存inc
                incorporation = resultCode.getData();
                JedisUtils.setObject(Constants.ONESHARE_REDIS_PREFIX + Constants.ONESHARE_REDIS_DELIMITER + Constants.ONESHARE_REDIS_INC + Constants.ONESHARE_REDIS_DELIMITER + incorporation.getId(), incorporation, cacheSeconds);
            }
        }
        return incorporation;
    }

     //❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀ 自定义方法 ❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀❀

    /**
     * 获取当前登录用户企业id
     * @return
     */
    public static Long getIncId() {
        return UserInfoUtil.getUserInfoVO().getUser().getIncid().longValue();
    }

    /**
     * 获取当前登录用户id
     * @return
     */
    public static Long getUserId() {
        return UserInfoUtil.getUserInfoVO().getUser().getUserId().longValue();
    }

    /**
     * 获取当前登录用户名
     * @return
     */
    public static String getUserName() {
        return UserInfoUtil.getUserInfoVO().getUser().getUserName();
    }

    /**
     * 获取当前登录用户部门id
     * @return
     */
    public static Long getDeptId() {
        Department department = UserInfoUtil.getUserInfoVO().getDepartment();
        if(department != null) {
            return department.getId().longValue();
        } else {
            return null;
        }
    }

    /**
     * 获取用户组id集合
     * @return
     */
    public static List<Long> getGroupIds() {
        List<Group> groupList = UserInfoUtil.getUserInfoVO().getGroupsList();
        List<Long> groupIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(groupList)) {
            for (Group g : groupList) {
                if (g != null) {
                    groupIds.add(g.getId().longValue());
                }
            }
        }
        return groupIds;
    }

    /**
     * 判断是否是管理员(超级管理员或组织管理员)
     * @return
     */
    public static boolean isAdmin() {
        Long roleId = UserInfoUtil.getUserInfoVO().getRole().getId();
        if ((roleId != null && roleId.equals(RoleConstants.ORGANIZED_ADMIN_ROLE_ID)) || isSystemAdmin()) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是超级管理员
     * @return
     */
    public static boolean isSystemAdmin() {
        Admin admin = UserInfoUtil.getUserInfoVO().getAdmin();
        if(admin != null) {
            return true;
        }
        return false;
    }
}
