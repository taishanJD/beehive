package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.quark.share.model.dataobj.Group;
import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.quark.share.model.vo.GroupSearchVO;
import com.quarkdata.quark.share.model.vo.UserSearchVO;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.GroupApi;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.UsersApi;
import com.quarkdata.yunpan.api.model.common.Constants;
import com.quarkdata.yunpan.api.model.common.ListResult;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.vo.MemberInfoVO;
import com.quarkdata.yunpan.api.service.GroupService;
import com.quarkdata.yunpan.api.util.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class GroupServiceImpl implements GroupService{

    @Autowired
    private GroupApi groupApi;
    @Autowired
    private UsersApi usersApi;

    @Override
    public ResultCode<ListResult<GroupSearchVO>> getGroupList(Integer incId, String groupName, Integer pageNum, Integer pageSize) {
        ResultCode<ListResult<Group>> result=groupApi.getGroupList(incId,groupName,pageNum,pageSize);
        ResultCode<ListResult<GroupSearchVO>> newResult = new ResultCode<>();
        ListResult<GroupSearchVO> newData = new ListResult<>();
        List<GroupSearchVO> newGroups = new ArrayList<>();
        List<Group> groups = result.getData() == null ? null :result.getData().getListData();
        if(CollectionUtils.isNotEmpty(groups)) {
            for(Group g: groups) {
                if (!StringUtils.equals(g.getSource(), "superGroup")) {
                    GroupSearchVO groupSearchVO = new GroupSearchVO();
                    BeanUtils.copyProperties(g, groupSearchVO);
                    groupSearchVO.set_type(Constants.TYPE_GROUP);
                    newGroups.add(groupSearchVO);
                }
            }
        }
        newData.setListData(newGroups);
        newData.setTotalNum(result.getData() == null ? 0 : result.getData().getTotalNum());
        newData.setPageNum(result.getData() == null ? 0 : result.getData().getPageNum());
        newData.setPageSize(result.getData() == null ? 0 : result.getData().getPageSize());
        newResult.setCode(result.getCode());
        newResult.setMsg(result.getMsg());
        newResult.setData(newData);
        return newResult;
    }

    @Override
    public ResultCode<MemberInfoVO> getAllChildren(Integer incId, String parentId, String name) {
        ResultCode<MemberInfoVO> result = new ResultCode<>();
        List<Group> groups = new ArrayList<>();
        List<GroupSearchVO> newGroups = null;
        List<Users> users = null;
        List<UserSearchVO> newUsers = null;
        if(StringUtils.isBlank(parentId) || parentId.equals("0")) {
            // 查询该企业下所有用户组
            ResultCode<ListResult<Group>> groupList = groupApi.getGroupListByIncId(incId, name, null, null);
            if(groupList != null && groupList.getData() != null && CollectionUtils.isNotEmpty(groupList.getData().getListData())) {
                groups = groupList.getData().getListData();
            }
            /*// 查询有关联关系的用户组
            List<Groups> groupsByGroupId = groupApi.getGroupsByGroupId(incId, null, null, null, null);
            Groups superGroup = null;
            if(CollectionUtils.isNotEmpty(tempList)) {
                 for(Groups item :tempList){
                     if(item.getIncId().equals(incId)){
                         if(StringUtils.equals(item.getSource(),"superGroup")){
                             superGroup = item;
                         }else {
                             groups.add(item);
                         }
                     }
                 }
            }
            if(superGroup != null) {
                tempList = groupApi.getGroupsByGroupId(incId, superGroup.getId().toString(), null, null, null);
                if (CollectionUtils.isNotEmpty(tempList)) {
                    groups.addAll(tempList);
                }
            }*/
        }else {
            groups = groupApi.getGroupByGroupId(incId, parentId, name, null, null);
        }
        users = usersApi.getUsersByGroupId(incId, parentId, name);
        if(CollectionUtils.isNotEmpty(users)) {
            newUsers = new ArrayList<>();
            for (Users u : users) {
                UserSearchVO userSearchVO = new UserSearchVO();
                BeanUtils.copyProperties(u, userSearchVO);
                userSearchVO.setId(u.getUserId().longValue());
                userSearchVO.set_type(Constants.TYPE_USER);
                newUsers.add(userSearchVO);
            }
        }

        if(CollectionUtils.isNotEmpty(groups)) {
            newGroups = new ArrayList<>();
            for(Group g: groups) {
                GroupSearchVO groupSearchVO = new GroupSearchVO();
                BeanUtils.copyProperties(g, groupSearchVO);
                groupSearchVO.set_type(Constants.TYPE_GROUP);
                newGroups.add(groupSearchVO);
            }
        }
        MemberInfoVO child = new MemberInfoVO(newUsers, newGroups, null);
        result.setData(child);
        return result;
    }

    @Override
    public ResultCode<List<Group>> getGroupListByParentId(Integer incId, String parentId, String groupName, Integer pageNum, Integer pageSize) {
        ResultCode<List<Group>> result = new ResultCode<>();
        List<Group> groups = null;
        if(StringUtils.isBlank(parentId) || parentId.equals("0")) {
            Group g = this.groupApi.getGroupById(incId, "1");
            if(g != null) {
                groups = new ArrayList<>();
                groups.add(g);
            }
        } else {
            groups = this.groupApi.getGroupByGroupId(incId, parentId, groupName, pageNum, pageSize);
        }
        result.setData(groups);
        return result;
    }
}
