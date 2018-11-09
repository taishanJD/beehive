package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by xiexl on 2018/1/18.
 *  云盘空间
 */


@Controller
@RequestMapping(RouteKey.PREFIX_API + RouteKey.ZONE)
public class ZoneController extends BaseController {

    @Autowired
    private DocumentService documentService;

    /**
     *  统计当前用户空间占比
     * @param response
     * @return
     */
    @RequestMapping(value = "/user")
    @ResponseBody
    public ResultCode personalProportion(HttpServletResponse response){
        Long incId = UserInfoUtil.getIncorporation().getId().longValue();
        UserInfoVO userInfoVO = UserInfoUtil.getUserInfoVO();
        Users user = userInfoVO.getUser();
        Long userId=user.getUserId().longValue();
        ResultCode<String> result = new ResultCode<String>();
        String size= documentService.calculateZoneUtilization(userId,incId);
        result.setCode(Messages.SUCCESS_CODE);
        result.setMsg(Messages.SUCCESS_MSG);
        result.setData(size);
        return result;
    }




}
