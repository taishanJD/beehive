package com.quarkdata.yunpan.api.service;

import com.quarkdata.quark.share.model.dataobj.Group;
import com.quarkdata.quark.share.model.vo.GroupSearchVO;
import com.quarkdata.yunpan.api.model.common.ListResult;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.vo.MemberInfoVO;

import java.util.List;

/**
 * @Author hanhy0720@thundersoft.com
 * @Description 用户组
 *
 */
public interface GroupService {

    /**
     * 获取用户组列表
     * @param incId
     * @param groupName
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResultCode<ListResult<GroupSearchVO>> getGroupList(Integer incId, String groupName, Integer pageNum, Integer pageSize);
    /**
     * 获取用户组列表
     */
    ResultCode<MemberInfoVO>  getAllChildren(Integer incId, String parentId, String name);

    /**
     * 根据parentId获取用户组
     *
     * @param incId
     * @param parentId
     * @param groupName
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResultCode<List<Group>> getGroupListByParentId(Integer incId, String parentId, String groupName, Integer pageNum, Integer pageSize);
}
