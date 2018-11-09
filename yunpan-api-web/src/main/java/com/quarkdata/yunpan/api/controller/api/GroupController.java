package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.quark.share.model.dataobj.Group;
import com.quarkdata.quark.share.model.vo.GroupSearchVO;
import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.common.ListResult;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.vo.MemberInfoVO;
import com.quarkdata.yunpan.api.service.GroupService;
import com.quarkdata.yunpan.api.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author hanhy0720@thundersoft.com
 * @Description  用户组
 *
 */
@Controller
@RequestMapping(RouteKey.PREFIX_API + "/group")
public class GroupController extends BaseController {

    @Autowired
    private GroupService groupService;

    // 根据用户名模糊查询用户
    @RequestMapping(value = "/page",method = RequestMethod.GET)
    @ResponseBody
    public ResultCode<ListResult<GroupSearchVO>> getGroupList(
            Integer pageNum,
            Integer pageSize,
            @RequestParam(value="groupName",defaultValue = "") String groupName) {
        ResultCode<ListResult<GroupSearchVO>> result = new ResultCode<ListResult<GroupSearchVO>>();
        try {
            // 当前企业ID
            Integer incId = UserInfoUtil.getIncorporation().getId();
            result = groupService.getGroupList(incId,groupName,pageNum,pageSize);
        } catch (Exception e) {
            result.setCode(1);
            result.setMsg(Messages.FIND_GROUP_FAIL_MSG);
            this.logger.error(Messages.FIND_GROUP_FAIL_MSG, e);
        }
        return result;
    }

    @RequestMapping("/get_group_by_parentId")
    @ResponseBody
    public ResultCode<List<Group>> getGroupListByParentId(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value="groupName",defaultValue = "") String groupName,
            @RequestParam(value = "parentId", defaultValue = "1") String parentId) {
        Integer incId = UserInfoUtil.getIncorporation().getId();
        if(StringUtils.isBlank(parentId) || "0".equals(parentId)) {
            parentId = null;
        }
        return this.groupService.getGroupListByParentId(incId, parentId, groupName, pageNum, pageSize);
    }

    @RequestMapping(value = "/getChildren",method = RequestMethod.GET)
    @ResponseBody
    public ResultCode<MemberInfoVO> getAllChildren(@RequestParam(value="name",defaultValue = "") String name,
                                                         @RequestParam(value="parentId",defaultValue = "0") String parentId){

        ResultCode<MemberInfoVO> result = new ResultCode<>();
        try {
            // 当前企业ID
            Integer incId = UserInfoUtil.getIncorporation().getId();
            //调用service层方法先获取符条件的父节点集合
            result = groupService.getAllChildren(incId, parentId, name);
        } catch (Exception e) {
            result.setCode(1);
            result.setMsg(Messages.FIND_DEPARTMENT_FAIL_MSG);
            this.logger.error(Messages.FIND_DEPARTMENT_FAIL_MSG, e);
        }
        return result;
    }
}
