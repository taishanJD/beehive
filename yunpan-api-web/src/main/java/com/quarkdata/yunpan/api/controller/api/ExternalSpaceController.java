package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.model.common.*;
import com.quarkdata.yunpan.api.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 外部共享空间
 * created by hanhy0720
 */
@RequestMapping(RouteKey.PREFIX_API + "/external/share")
@RestController
public class ExternalSpaceController extends BaseController {

    @Autowired
    private OrganizedFileService organizedFileService;

    @Autowired
    private ExternalSpaceService externalSpaceService;

    @Autowired
    private DocumentPermissionService documentPermissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private LinkService linkService;

    /**
     * 生成外部账号
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/generate_account")
    public ResultCode<Users> generateExternalAccount(HttpServletRequest request){

        ResultCode<Users> resultCode=new ResultCode<>();
        String account = GenerateUtils.generatePsw(6,false);
        ResultCode<Users> result = userService.getUserByUsername(request, account);
        if(result.getData() != null) {
            // 说明已有同名账户, 重新生成用户名
            account = GenerateUtils.generatePsw(6,false);
            result = userService.getUserByUsername(request, account);
            while(result.getData() != null) {
                account = GenerateUtils.generatePsw(6,false);
                result = userService.getUserByUsername(request, account);
            }
        }
        // String pwd=GenerateUtils.generatePsw(6,false);
        Users user=new Users();
        user.setUserName(account);
        user.setPassword("Bee12345");
        resultCode.setData(user);

        return resultCode;
    }

    /**
     * 删除外部账号--userId为外部账号的id
     * @return
     */
    // @LogAnnotation
    @ResponseBody
    @RequestMapping(value = "/delete_external_account")
    public ResultCode<String> deleteExternalAccount(@RequestParam("documentId") String documentId){

        ResultCode resultCode=new ResultCode();
        if (documentId!=null){
             resultCode=externalSpaceService.deleteExternalUser(documentId);
        }else {
            resultCode.setCode(Messages.MISSING_INPUT_CODE);
            resultCode.setMsg(Messages.MISSING_INPUT_MSG);
        }
        return resultCode;
    }

    /**
     * 根据文档id获取外部用户
     * @param documentId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/get_external_account")
    public ResultCode<String> getExternalUserById(@RequestParam("documentId") String documentId){
        ResultCode<String> result=new ResultCode<>();
        if (documentId!=null){
            ResultCode<Users> resultCode=externalSpaceService.getExternalUser(documentId);
            Users user=resultCode.getData();
            result.setData(user.getUserName());
        }else {
            result.setCode(Messages.MISSING_INPUT_CODE);
            result.setMsg(Messages.MISSING_INPUT_MSG);
        }
        return result;
    }
}
