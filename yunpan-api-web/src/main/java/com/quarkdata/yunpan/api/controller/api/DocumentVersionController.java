package com.quarkdata.yunpan.api.controller.api;

import com.alibaba.fastjson.JSON;
import com.quarkdata.yunpan.api.aspect.checkDocPermission.CheckDocPermission;
import com.quarkdata.yunpan.api.aspect.isDelete.IsDelete;
import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.common.DocumentPermissionConstants;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.vo.DocumentVersionVO;
import com.quarkdata.yunpan.api.service.DocumentService;
import com.quarkdata.yunpan.api.service.DocumentVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 文档历史版本
 * 
 * @author typ 2017年12月27日
 */
@RequestMapping(RouteKey.PREFIX_API + "/document/version")
@RestController
public class DocumentVersionController extends BaseController {

	@Autowired
	private DocumentVersionService documentVersionService;

	@Autowired
	private DocumentService documentService;

	/**
	 * 获取文档的历史版本
	 * 
	 * @param documentId
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@IsDelete(ids = "#{documentId}")
	public ResultCode<List<DocumentVersionVO>> list(
			@RequestParam(value = "documentId", required = true) Long documentId) {
		//获取登录态信息
		Long incId = UserInfoUtil.getIncorporation().getId().longValue();
		
		ResultCode<List<DocumentVersionVO>> resultCode = null;
		try {
			resultCode = documentVersionService.getDocumentVersions(documentId,
					incId);
		} catch (Exception e) {
			logger.error("get document versions", e);
			resultCode = new ResultCode<List<DocumentVersionVO>>();
			resultCode.setCode(Messages.API_ERROR_CODE);
			resultCode.setMsg(Messages.API_ERROR_MSG);
		}
		return resultCode;
	}

	/**
	 * 还原到历史版本 版本记录表，版本+1
	 * 
	 * @return
	 */
	@RequestMapping(value = "/reset_version", method = RequestMethod.POST)
	@IsDelete(ids = "#{documentId}")
	@CheckDocPermission(ids = "#{documentId}", permission = DocumentPermissionConstants.PermissionIndex.VERSION_MANAGEMENT)
	public ResultCode<String> resetVersion(
			@RequestParam(value = "documentId", required = true) Long documentId,
			@RequestParam(value = "toVersionId", required = true) Long toVersionId,
			HttpServletRequest request) {
		Long userId = UserInfoUtil.getUserInfoVO().getUser().getUserId().longValue();
		String userName = UserInfoUtil.getUserInfoVO().getUser().getUserName();

		ResultCode<String> resultCode = null;
		logger.info("start reset version,params --> "
				+ "documentId:{},toVersionId:{},userId:{},userName:{}",
				documentId, toVersionId, userId, userName);
		try {
			resultCode = documentVersionService.resetVersion(request, documentId,
					toVersionId, userId, userName);
			logger.info("after reset version,result --> "+JSON.toJSONString(resultCode));
		} catch (Exception e) {
			logger.error("reset version", e);
			resultCode = new ResultCode<String>();
			resultCode.setCode(Messages.API_ERROR_CODE);
			resultCode.setMsg(Messages.API_ERROR_MSG);
		}
		return resultCode;
	}
}
