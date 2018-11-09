package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.aspect.checkDocPermission.CheckDocPermission;
import com.quarkdata.yunpan.api.aspect.isDelete.IsDelete;
import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.common.DocumentPermissionConstants;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.vo.RecycleFileVO;
import com.quarkdata.yunpan.api.service.DeleteService;
import com.quarkdata.yunpan.api.service.DocumentPermissionService;
import com.quarkdata.yunpan.api.service.DocumentService;
import com.quarkdata.yunpan.api.util.StringUtils;
import com.quarkdata.yunpan.api.util.common.Exception.YCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 删除相关
 *
 * @author jiadao
 * @date 2017-12-19
 *
 */
@Controller
@RequestMapping(RouteKey.PREFIX_API + RouteKey.DELETE)
public class DeleteController extends BaseController {

    @Autowired
    private DeleteService deleteService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentPermissionService documentPermissionService;

    static Logger logger = LoggerFactory.getLogger(DeleteController.class);

    /**
     * 文档删除接口
     *
     * @author jiadao
     */
    @ResponseBody
    @RequestMapping(value = "/doc_del")
    @IsDelete(ids = "#{docIds}")
    @CheckDocPermission(ids = "#{docIds}" ,permission = DocumentPermissionConstants.PermissionIndex.DELETE)
    public ResultCode<List<String>> delDocBatch(HttpServletRequest request, String docIds) {
        ResultCode<List<String>> result = new ResultCode<List<String>>();

        try {
            // token中获取用户信息
            UserInfoVO user = UserInfoUtil.getUserInfoVO();
            long userId = user.getUser().getUserId();
            long incId = user.getUser().getIncid();
            String userName = user.getUser().getUserName();


            // 删除逻辑开始
            result = deleteService.delDocBatch(request, docIds, userId, userName, incId);

        } catch (Exception e) {
            logger.error("delete doc error", e);
            result.setCode(Messages.API_ERROR_CODE);
            result.setMsg(Messages.API_ERROR_MSG);
            return result;
        }
        return result;
    }

    /**
     * 删除一个文档版本
     *
     * @author jiadao
     */
    @ResponseBody
    @RequestMapping(value = "/doc_version_del")
    public ResultCode<String> delDocVersion(HttpServletRequest request, HttpServletResponse response) {
        ResultCode<String> result = new ResultCode<String>();
        try {
            // token中获取用户信息
            UserInfoVO user = UserInfoUtil.getUserInfoVO();
            long userId = user.getUser().getUserId();
            long incId = user.getUser().getIncid();

            String docVersionIdStr = request.getParameter("docVersionId");

            Document document = this.documentService.getDocumentByVersion(Long.parseLong(docVersionIdStr));
            if(document == null) {
                throw new YCException(Messages.FILE_NOT_EXIST_MSG, Messages.FILE_NOT_EXIST_CODE);
            }
            Boolean hasPermission = this.documentPermissionService.hasPermission(incId, userId, UserInfoUtil.getGroupIds(), UserInfoUtil.getDeptId(), document.getId(), DocumentPermissionConstants.PermissionIndex.VERSION_MANAGEMENT);
            if(!hasPermission) {
                throw new YCException(Messages.API_AUTHEXCEPTION_MSG, Messages.API_AUTHEXCEPTION_CODE);
            }
            // 删除逻辑开始
            result = deleteService.delDocumentVersion(request, incId, userId, docVersionIdStr);


        } catch (Exception e) {
            logger.error("delete doc version error", e);
            result.setCode(Messages.API_ERROR_CODE);
            result.setMsg(Messages.API_ERROR_MSG);
            return result;
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/get_recycle_list")
    public ResultCode<List<RecycleFileVO>> getRecycleList(HttpServletRequest request, HttpServletResponse response) {
        ResultCode<List<RecycleFileVO>> result = new ResultCode<List<RecycleFileVO>>();

        try {
            // token中获取用户信息
            UserInfoVO user = UserInfoUtil.getUserInfoVO();
            long userId = user.getUser().getUserId();
            long incId = user.getUser().getIncid();

            String filter = request.getParameter("filter");
            String folderId = request.getParameter("folderId");

            logger.info("开始获取回收站列表！");
            List<RecycleFileVO> docInRecycles = deleteService.getDocListInRecycle(incId, userId,filter,folderId);
            logger.info("完毕！");
//			String jsonString = JsonMapper.toJsonString(docInRecycles);
            result.setData(docInRecycles);

        } catch (Exception e) {
            logger.error("get recycle list error", e);
            result.setCode(Messages.API_ERROR_CODE);
            result.setMsg(Messages.API_ERROR_MSG);
            return result;
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/open_folder_in_recycle")
    public ResultCode<List<RecycleFileVO>> openFolderInRecycle(HttpServletRequest request,
                                                               @RequestParam(value = "folderId") String folderIdStr) {
        ResultCode<List<RecycleFileVO>> result = new ResultCode<List<RecycleFileVO>>();
        try {
            String filter = request.getParameter("filter");

            if (StringUtils.isNotBlank(folderIdStr)) {
                Long folderId = Long.valueOf(folderIdStr);
                logger.info("开始查询文件夹==" + folderId + "下的文档！");
                List<RecycleFileVO> docList = deleteService.getDocListInFolderInRecycle(folderId,filter);
                logger.info("查询结束！");
//				String data = JsonMapper.toJsonString(docList);
                result.setData(docList);
            }
        } catch (Exception e) {
            logger.error("get child doc list error", e);
            result.setCode(Messages.API_ERROR_CODE);
            result.setMsg(Messages.API_ERROR_MSG);
            return result;
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/doc_remove_completely")
    public ResultCode<List<String>> removeDocCompletely(HttpServletRequest request, String docIds) {
        ResultCode<List<String>> result = new ResultCode<List<String>>();
        try {
            // token中获取用户信息
            UserInfoVO user = UserInfoUtil.getUserInfoVO();
            long userId = user.getUser().getUserId();
            long incId = user.getUser().getIncid();

            // 彻底删除逻辑开始
            result = deleteService.removeDocCompletelyBatch(request, incId, userId, docIds);

        } catch (Exception e) {
            logger.error("delete doc completely error", e);
            result.setCode(Messages.API_ERROR_CODE);
            result.setMsg(Messages.API_ERROR_MSG);
            return result;
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/doc_recover")
    public ResultCode<List<String>> recoverDoc(HttpServletRequest request, HttpServletResponse response) {
        ResultCode<List<String>> result = new ResultCode<List<String>>();
        try {
            // token中获取用户信息
            UserInfoVO user = UserInfoUtil.getUserInfoVO();
            long userId = user.getUser().getUserId();

            String docIdsStr = request.getParameter("docIds");

            //还原逻辑开始
            result = deleteService.recoverDocBatch(request, docIdsStr, userId);

        } catch (Exception e) {
            logger.error("doc recover error", e);
            result.setCode(Messages.API_ERROR_CODE);
            result.setMsg(Messages.API_ERROR_MSG);
            return result;
        }
        return result;
    }
}
