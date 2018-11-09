package com.quarkdata.yunpan.api.controller.api;

import com.alibaba.fastjson.JSON;
import com.quarkdata.yunpan.api.aspect.isDelete.IsDelete;
import com.quarkdata.yunpan.api.aspect.isEnoughSpace.IsEnoughSpace;
import com.quarkdata.yunpan.api.aspect.isLock.IsLock;
import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.message.MessageAnnotation;
import com.quarkdata.yunpan.api.message.MessageType;
import com.quarkdata.yunpan.api.message.MessageUtils;
import com.quarkdata.yunpan.api.model.common.*;
import com.quarkdata.yunpan.api.model.vo.*;
import com.quarkdata.yunpan.api.service.OrganizedFileService;
import com.quarkdata.yunpan.api.util.common.Exception.YCException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 组织文件
 * 
 * @author typ 2017年12月13日
 */
@RequestMapping(RouteKey.PREFIX_API + "/org/file")
@RestController
public class OrganizedFileController extends BaseController {

	@Autowired
	private OrganizedFileService organizedFileService;

	/**
	 * 查询组织文件列表
	 * 
	 * @param documentName
	 * @param parentId defaultValue = "0"
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@IsDelete(ids = "#{parentId}")
	public void list(
			String documentName,
            @RequestParam(value = "isExact",required = false, defaultValue = "0") Long isExact,
			@RequestParam(value = "parentId",required = false, defaultValue = "0") Long parentId,
			@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			HttpServletResponse response) {
		try {
			// 获取登录态信息
			Long incId = UserInfoUtil.getIncorporation().getId().longValue();
			Long userId = UserInfoUtil.getUserInfoVO().getUser().getUserId()
					.longValue();
			List<Long> userGroupIds = UserInfoUtil.getGroupIds();
			Long deptId = UserInfoUtil.getUserInfoVO().getDepartment().getId()
					.longValue();
			boolean isAdmin = UserInfoUtil.isAdmin();
			
			ResultCode<List<DocumentExtend>> resultCode=null;

			documentName = StringUtils.isBlank(documentName) ? null : documentName.trim();
			if(UserInfoUtil.isSystemAdmin()){
				resultCode = organizedFileService.adminFileList(incId,parentId,userId, documentName, isExact, pageNum, pageSize);
			}else{
				resultCode = organizedFileService
						.getOrganizedFilesCarryCol_tags(incId, userId,
								userGroupIds, deptId, parentId, documentName, isExact, isAdmin, pageNum, pageSize);
			}

			renderString(response, resultCode);
		} catch (Exception e) {
			logger.error("get organized document list", e);
			ResultCode<String> resultCode = new ResultCode<String>();
			resultCode.setCode(Messages.API_ERROR_CODE);
			resultCode.setMsg(Messages.API_ERROR_MSG);
			renderString(response, resultCode);
		}
	}
	
	@RequestMapping("/isOrganizedAdmin")
	public ResultCode<Boolean> isOrganizedAdmin(){
		ResultCode<Boolean> resultCode=new ResultCode<Boolean>();
		boolean isAdmin = UserInfoUtil.isAdmin();
		resultCode.setData(isAdmin);
		return resultCode;
	}

	/**
	 * 上传组织文件
	 * 
	 * @param file
	 * @param fileName
	 * @param isOverwrite
	 * @param parentId
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/upload_file", method = RequestMethod.POST)
	@IsDelete(ids = "#{parentId}")
	@IsEnoughSpace(type = DocumentConstants.DOCUMENT_TYPE_ORGANIZED, docSize = "#{file.size}")
	public ResultCode<DocumentExtend> uploadFile(
			@RequestParam(value = "file", required = true) MultipartFile file,
			String fileName,
			@RequestParam(value = "isOverwrite", required = false, defaultValue = "0") int isOverwrite,
			@RequestParam(value = "parentId", required = true) Long parentId,
			HttpServletRequest request) {
		ResultCode<DocumentExtend> resultCode = null;
		try {
			// 获取登录态信息
			Long incId = UserInfoUtil.getIncorporation().getId().longValue();
			Long userId = UserInfoUtil.getUserInfoVO().getUser().getUserId()
					.longValue();
			String userName = UserInfoUtil.getUserInfoVO().getUser().getUserName();
			// 用户组id,部门id,角色id
			boolean isOrganizedAdmin = UserInfoUtil.isAdmin();
			Integer deptId = UserInfoUtil.getUserInfoVO().getDepartment().getId();
			List<Long> groupIds = UserInfoUtil.getGroupIds();

			logger.info(
					"start upload file,params --> "
							+ "originalFilename:{},fileName:{},"
							+ "isOverwrite:{},parentId:{},userId:{},"
							+ "userName:{},incId:{},deptId:{},groupIds:{},isOrganizedAdmin:{}",
					file.getOriginalFilename(), fileName, isOverwrite, parentId,
					userId, userName, incId, deptId, groupIds, isOrganizedAdmin);
			
			resultCode = organizedFileService.uploadFile(request, file, fileName,
					isOverwrite, parentId, userId, userName, incId, deptId,
					groupIds, isOrganizedAdmin);
			logger.info("after upload file,result --> "
					+ JSON.toJSONString(resultCode));
		} catch (DuplicateKeyException e) {
			logger.info("处理上传重复组织文件");
			resultCode = new ResultCode<DocumentExtend>();
			resultCode.setCode(Messages.API_INSERT_ERROR_CODE);
			resultCode.setMsg(Messages.API_INSERT_ERROR_MSG);
		}catch (Exception e) {
			logger.error("upload organized file", e);
			resultCode = new ResultCode<DocumentExtend>();
			resultCode.setCode(Messages.API_ERROR_CODE);
			resultCode.setMsg(Messages.API_ERROR_MSG);
		}
		return resultCode;
	}

	/**
	 * App上传组织文件
	 *
	 * @param file
	 * @param fileName
	 * @param isOverwrite
	 * @param parentId
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/upload_file_app", method = RequestMethod.POST)
	@IsDelete(ids = "#{parentId}")
	@IsEnoughSpace(type = DocumentConstants.DOCUMENT_TYPE_ORGANIZED, docSize = "#{file.size}")
	public ResultCode<DocumentExtend> uploadFileApp(
			@RequestParam(value = "file", required = true) MultipartFile file,
			String fileName,
			@RequestParam(value = "isOverwrite", required = false, defaultValue = "0") int isOverwrite,
			@RequestParam(value = "parentId", required = true) Long parentId,
			HttpServletRequest request) {
		ResultCode<DocumentExtend> resultCode = null;
		try {
			// 获取登录态信息
			Long incId = UserInfoUtil.getIncorporation().getId().longValue();
			Long userId = UserInfoUtil.getUserInfoVO().getUser().getUserId()
					.longValue();
			String userName = UserInfoUtil.getUserInfoVO().getUser().getUserName();
			// 用户组id,部门id,角色id
			boolean isOrganizedAdmin = UserInfoUtil.isAdmin();
			Integer deptId = UserInfoUtil.getUserInfoVO().getDepartment().getId();
			List<Long> groupIds = UserInfoUtil.getGroupIds();

			logger.info(
					"start upload file,params --> "
							+ "originalFilename:{},fileName:{},"
							+ "isOverwrite:{},parentId:{},userId:{},"
							+ "userName:{},incId:{},deptId:{},groupIds:{},isOrganizedAdmin:{}",
					file.getOriginalFilename(), fileName, isOverwrite, parentId,
					userId, userName, incId, deptId, groupIds, isOrganizedAdmin);

			resultCode = organizedFileService.uploadFileApp(request, file, fileName,
					isOverwrite, parentId, userId, userName, incId, deptId,
					groupIds, isOrganizedAdmin);
			logger.info("after upload file,result --> "
					+ JSON.toJSONString(resultCode));
		} catch (DuplicateKeyException e) {
			logger.info("处理上传重复组织文件");
			resultCode = new ResultCode<DocumentExtend>();
			resultCode.setCode(Messages.API_INSERT_ERROR_CODE);
			resultCode.setMsg(Messages.API_INSERT_ERROR_MSG);
		}catch (Exception e) {
			logger.error("upload organized file", e);
			resultCode = new ResultCode<DocumentExtend>();
			resultCode.setCode(Messages.API_ERROR_CODE);
			resultCode.setMsg(Messages.API_ERROR_MSG);
		}
		return resultCode;
	}

	/**
	 * 创建组织空间
	 *
	 * @param organizedSpace
	 * @param request
	 * @return
	 */
	@MessageAnnotation
	@RequestMapping(value = "/create_organized_space", method = RequestMethod.POST)
	public ResultCode<DocumentExtend> createOrganizedSpace(
			@RequestBody CreateOrganizedSpaceVO organizedSpace,
			HttpServletRequest request) {
		ResultCode<DocumentExtend> resultCode = null;
		if(organizedSpace == null || StringUtils.isBlank(organizedSpace.getDocumentName())) {
			resultCode = new ResultCode<>();
			resultCode.setCode(Messages.MISSING_INPUT_CODE);
			resultCode.setMsg(Messages.MISSING_INPUT_MSG);
			return resultCode;
		}
		// 获取登录态信息
		Long userId = UserInfoUtil.getUserInfoVO().getUser().getUserId()
				.longValue();
		String userName = UserInfoUtil.getUserInfoVO().getUser().getUserName();
		Long incId = UserInfoUtil.getIncorporation().getId().longValue();
		boolean isAdmin = UserInfoUtil.isAdmin();

		logger.info("start create organized space,params --> "
				+ JSON.toJSONString(organizedSpace));
		try {
			resultCode = organizedFileService.createOrganizedSpace(request,
					organizedSpace.getDocumentName().trim(),
					organizedSpace.getPermissions(), organizedSpace.getPermissionsOfGenerateAccounts(), userId, userName, incId,
					isAdmin);
			logger.info("after create organized space,result --> "
					+ JSON.toJSONString(resultCode));
			//接入消息通知
			List<Long> documentIds = new ArrayList<>();
			documentIds.add(resultCode.getData().getDocumentId());
			MessageUtils.createMessage(request, resultCode.getCode(), MessageType.organized_space, documentIds, organizedSpace.getPermissions());
		}catch (YunPanApiException e){
			logger.error("create organized space,errCode is {},errMsg is {}", e.getErrCode(),e.getErrMsg());
			resultCode = new ResultCode<DocumentExtend>();
			resultCode.setCode(e.getErrCode());
			resultCode.setMsg(e.getErrMsg());
		}catch (Exception e) {
			logger.error("create organized space", e);
			resultCode = new ResultCode<DocumentExtend>();
			resultCode.setCode(Messages.API_ERROR_CODE);
			resultCode.setMsg(Messages.API_ERROR_MSG);
		}
		return resultCode;
	}

	/**
	 * 创建组织文件夹
	 * @param createOrganizedDirectoryVO
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/create_dir", method = RequestMethod.POST)
	@IsDelete(ids = "#{createOrganizedDirectoryVO.parentId}")
	@MessageAnnotation
	public ResultCode<DocumentExtend> createDir(@RequestBody CreateOrganizedDirectoryVO createOrganizedDirectoryVO,
			HttpServletRequest request) {
		ResultCode<DocumentExtend> resultCode = null;
		if(createOrganizedDirectoryVO == null || StringUtils.isBlank(createOrganizedDirectoryVO.getDocumentName())) {
			resultCode = new ResultCode<>();
			resultCode.setCode(Messages.MISSING_INPUT_CODE);
			resultCode.setMsg(Messages.MISSING_INPUT_MSG);
			return resultCode;
		}
		// 获取登录态信息
		Long incId = UserInfoUtil.getIncorporation().getId().longValue();
		Long userId = UserInfoUtil.getUserInfoVO().getUser().getUserId().longValue();
		String userName = UserInfoUtil.getUserInfoVO().getUser().getUserName();
		boolean isAdmin = UserInfoUtil.isAdmin();
		Integer deptId = UserInfoUtil.getUserInfoVO().getDepartment().getId();
		List<Long> groupIds = UserInfoUtil.getGroupIds();

		logger.info("start create dir,params --> "
				+ "documentName:{},parentId:{},userId:{},userName:{},incId:{}",
				createOrganizedDirectoryVO.getDocumentName(), createOrganizedDirectoryVO.getParentId(), userId, userName, incId);

		try {
			if(createOrganizedDirectoryVO.getParentId() != null && createOrganizedDirectoryVO.getParentId().equals(DocumentConstants.DOCUMENT_ROOT_PARENTID)) {
				resultCode = this.organizedFileService.createOrganizedSpace(request, createOrganizedDirectoryVO.getDocumentName().trim(), createOrganizedDirectoryVO.getPermissions(), createOrganizedDirectoryVO.getPermissionsOfGenerateAccounts(), userId, userName, incId, isAdmin);
				//接入消息通知
				List<Long> documentIds = new ArrayList<>();
				documentIds.add(resultCode.getData().getDocumentId());
				MessageUtils.createMessage(request, resultCode.getCode(), MessageType.organized_space, documentIds, createOrganizedDirectoryVO.getPermissions());
				logger.info("after create organized space,result --> "
						+ JSON.toJSONString(resultCode));
			} else {
				resultCode = organizedFileService.createDir(request, createOrganizedDirectoryVO.getDocumentName().trim(), createOrganizedDirectoryVO.getParentId(), userId, userName, incId, deptId, groupIds, isAdmin,
						createOrganizedDirectoryVO.getPermissions(), createOrganizedDirectoryVO.getPermissionsOfGenerateAccounts());
				if(CollectionUtils.isNotEmpty(createOrganizedDirectoryVO.getPermissions()) && Messages.SUCCESS_CODE == resultCode.getCode()) {
					// 发送消息通知
					List<Long> documentIds = new ArrayList<>();
					documentIds.add(resultCode.getData().getId());
					MessageUtils.createMessage(request, resultCode.getCode(), MessageType.add_permission, documentIds, createOrganizedDirectoryVO.getPermissions());
					logger.info("after create dir,result --> "
							+ JSON.toJSONString(resultCode));
				}
			}
		}catch (YunPanApiException e){
			logger.error("new organized dir,errCode is {},errMsg is {}",
					e.getErrCode(),e.getErrMsg());
			resultCode = new ResultCode<>();
			resultCode.setCode(e.getErrCode());
			resultCode.setMsg(e.getErrMsg());
		}catch (Exception e) {
			logger.error("new organized dir", e);
			resultCode = new ResultCode<>();
			resultCode.setCode(Messages.API_ERROR_CODE);
			resultCode.setMsg(Messages.API_ERROR_MSG);
		}
		return resultCode;
	}

	/**
	 * 设置文档权限
	 * 
	 * @param documentPrivilege
	 * @param request
	 * @return
	 */
	@MessageAnnotation
	@RequestMapping(value = "/set_document_privilege", method = RequestMethod.POST)
	@IsDelete(ids = "#{documentPrivilege.documentId}")
    @IsLock(ids = "#{documentPrivilege.documentId}")
	public ResultCode<String> setDocumentPrivilege(
			@RequestBody SetDocumentPrivilegeVO documentPrivilege,
			HttpServletRequest request) {
		// 获取登录态信息
		Long incId = UserInfoUtil.getIncorporation().getId().longValue();
		boolean isOrganizedAdmin = UserInfoUtil.isAdmin();
        Long userId = UserInfoUtil.getUserInfoVO().getUser().getUserId()
                .longValue();
        List<Long> userGroupIds = UserInfoUtil.getGroupIds();
        Long deptId = UserInfoUtil.getUserInfoVO().getDepartment().getId()
                .longValue();
		logger.info(
				"start set document privilege,params --> {},incId:{},isOrganizedAdmin:{}",
				JSON.toJSONString(documentPrivilege),incId,isOrganizedAdmin);

		ResultCode<String> resultCode = null;
		try {
			resultCode = organizedFileService.setDocumentPrivilege(request, documentPrivilege.getDocumentId(), documentPrivilege.getPermissions(),
					documentPrivilege.getPermissionsOfGenerateAccounts(), incId,isOrganizedAdmin, userId, userGroupIds, deptId);
			logger.info("after set document privilege,result --> "
					+ JSON.toJSONString(resultCode));
			//消息通知处理
			List<Long> documentIds = new ArrayList<>();
			documentIds.add(documentPrivilege.getDocumentId());
			MessageUtils.createMessage(request, resultCode.getCode(), MessageType.add_permission, documentIds, documentPrivilege.getPermissions());
		}catch(YunPanApiException e){
			logger.error("set document privilege,errCode is {},errMsg is {}",
					e.getErrCode(),e.getErrMsg());
			resultCode = new ResultCode<String>();
			resultCode.setCode(e.getErrCode());
			resultCode.setMsg(e.getErrMsg());
		}catch (Exception e) {
			logger.error("set document privilege", e);
			resultCode = new ResultCode<String>();
			resultCode.setCode(Messages.API_ERROR_CODE);
			resultCode.setMsg(Messages.API_ERROR_MSG);
		}
		return resultCode;
	}

	@RequestMapping("/getDocDocumentPermissions")
	@IsDelete(ids = "#{docId}")
	public ResultCode<List<DocumentPermissionVO>> getDocDocumentPermissions(Long docId){
		Long incId = UserInfoUtil.getIncorporation().getId().longValue();
		ResultCode<List<DocumentPermissionVO>> resultCode=organizedFileService.getDocDocumentPermissions(docId,incId);
		return resultCode;
	}

	@RequestMapping("/getDocDocumentPermissionsToTheRoot")
	@IsDelete(ids = "#{docId}")
	public ResultTwoDataCode<List<DocumentPermissionVO>,List<Long>> getDocDocumentPermissionsToTheRoot(Long docId){
		Long incId = UserInfoUtil.getIncorporation().getId().longValue();
		ResultTwoDataCode<List<DocumentPermissionVO>, List<Long>> docDocumentPermissionsToTheRoot = organizedFileService.getDocDocumentPermissionsToTheRoot(docId, incId);
		return docDocumentPermissionsToTheRoot;
	}

	@RequestMapping(value = "/getDocumentPermissionOfMine", method = RequestMethod.GET)
	@IsDelete(ids = "#{docId}")
	public ResultCode<Integer> getDocumentPermissionOfMine(Long docId) {
		Long userId = UserInfoUtil.getUserInfoVO().getUser().getUserId().longValue();
		List<Long> userGroupIds = UserInfoUtil.getGroupIds();
		Long deptId = UserInfoUtil.getUserInfoVO().getDepartment().getId().longValue();

		return organizedFileService.getDocumentPermissionOfMine(docId, userId, userGroupIds, deptId, UserInfoUtil.isAdmin());
	}

	/**
	 * 根据parentId获取具有大于读写权限的组织文件子文件夹目录
	 * @param parentId
	 * @return
	 */
	@RequestMapping(value = "/dir")
	@ResponseBody
	@IsDelete(ids = "#{parentId}")
	public ResultCode<List<DocumentZtreeVO>> getGreaterThanWritePrivilegeIncDirectoryByParentId(
			String documentName,
			@RequestParam(value = "parentId", required = false, defaultValue = "0") Long parentId){
		ResultCode<List<DocumentZtreeVO>> resultCode=null;
		try {
			// 获取登录态信息
			Long incId = UserInfoUtil.getIncorporation().getId().longValue();
			Long userId = UserInfoUtil.getUserInfoVO().getUser().getUserId()
					.longValue();
			List<Long> userGroupIds = UserInfoUtil.getGroupIds();
			Long deptId = UserInfoUtil.getUserInfoVO().getDepartment().getId()
					.longValue();
			boolean isAdmin = UserInfoUtil.isAdmin();


			resultCode = organizedFileService
					.getGreaterThanWritePrivilegeIncDirectoryByParentId(incId, userId,
							userGroupIds, deptId, parentId, documentName, isAdmin);
		} catch (Exception e) {
			logger.error("get organized directory list", e);
			resultCode = new ResultCode<List<DocumentZtreeVO>>();
			resultCode.setCode(Messages.API_ERROR_CODE);
			resultCode.setMsg(Messages.API_ERROR_MSG);
		}
		return resultCode;
	}

	/**
	 * 根据parentId获取个人文件数量
	 * @param parentId
	 * @return
	 */
	@RequestMapping(value = "/getCount")
	@ResponseBody
	public ResultCode<String> getCount(
			@RequestParam(value = "parentId", required = false, defaultValue = "0") Long parentId){
		Long incId = UserInfoUtil.getIncorporation().getId().longValue();
		Long userId = UserInfoUtil.getUserInfoVO().getUser().getUserId().longValue();
		List<Long> userGroupIds = UserInfoUtil.getGroupIds();
		Long deptId = UserInfoUtil.getUserInfoVO().getDepartment().getId()
				.longValue();
		ResultCode<String> resultCode = null;
		try {
			resultCode = organizedFileService.getFileCount(incId, userId, userGroupIds, deptId);
		} catch (Exception e) {
			logger.error("get personal directory list", e);
			resultCode = new ResultCode<>();
			resultCode.setCode(Messages.API_ERROR_CODE);
			resultCode.setMsg(Messages.API_ERROR_MSG);
		}
		return resultCode;
	}

	/**
	 * 根据parentId获取文件
	 * @param parentId
	 * @return
	 */
	@RequestMapping(value = "/getAllChildren")
	@ResponseBody
	@IsDelete(ids = "#{parentId}")
	public ResultCode<List<DocumentExtend>> getAllChildren(
			@RequestParam(value = "parentId", required = false, defaultValue = "0") Long parentId){
		Long incId = UserInfoUtil.getIncorporation().getId().longValue();
		Long userId = UserInfoUtil.getUserInfoVO().getUser().getUserId().longValue();
		List<Long> userGroupIds = UserInfoUtil.getGroupIds();
		Long deptId = UserInfoUtil.getUserInfoVO().getDepartment().getId()
				.longValue();
		ResultCode<List<DocumentExtend>> resultCode = null;
		try {
			resultCode = organizedFileService.getAllChildren(incId, userId, userGroupIds, deptId, parentId);
		} catch (Exception e) {
			logger.error("get org all children list", e);
			resultCode = new ResultCode<>();
			resultCode.setCode(Messages.API_ERROR_CODE);
			resultCode.setMsg(Messages.API_ERROR_MSG);
		}
		return resultCode;
	}

	/**
	 * 组织文档删除接口
	 *
	 * @author jiadao
	 * @param docIds 文档id，多个用逗号相隔
	 */
	@RequestMapping(value = "/delete")
	@ResponseBody
	@IsDelete(ids = "#{docIds}")
    @IsLock(ids = "#{docIds}")
	public ResultCode<String> delDocBatch(HttpServletRequest request, HttpServletResponse response, String docIds) {
		ResultCode<String> result = new ResultCode<>();
		if(StringUtils.isBlank(docIds)) {
			result.setCode(Messages.MISSING_INPUT_CODE);
			result.setData(Messages.MISSING_INPUT_MSG);
			return result;
		}
		try {
			Long incId = UserInfoUtil.getIncorporation().getId().longValue();
			Long userId = UserInfoUtil.getUserInfoVO().getUser().getUserId().longValue();
			String userName = UserInfoUtil.getUserInfoVO().getUser().getUserName();
			List<Long> userGroupIds = UserInfoUtil.getGroupIds();
			Long deptId = UserInfoUtil.getUserInfoVO().getDepartment().getId().longValue();
            boolean isOrganizedAdmin = UserInfoUtil.isAdmin();
            Boolean flag = this.organizedFileService.delDocBatch(incId, userId, userName, userGroupIds, deptId, docIds, isOrganizedAdmin);
			result.setData(flag.toString());
		} catch (Exception e) {
			logger.error("delete organized doc error", e);
			result.setCode(Messages.API_ERROR_CODE);
			result.setMsg(Messages.API_ERROR_MSG);
			return result;
		}
		return result;
	}

    /**
     * 锁定组织文档(文件夹或者文件)
     * @param request
     * @param documentIds 逗号分隔
     * @return
     */
	@RequestMapping(value = "/lock", method = RequestMethod.POST)
	public ResultCode<String> lockOrgDocument(HttpServletRequest request, String documentIds) {
		if(StringUtils.isBlank(documentIds)) {
			throw new YCException(Messages.MISSING_INPUT_MSG, Messages.MISSING_INPUT_CODE);
		}
        ResultCode<String> resultCode = this.organizedFileService.lockOrgDocument(request, documentIds);
        return resultCode;
	}

    /**
     * 解锁组织文档(文件夹或者文件)
     * @param request
     * @param documentIds 逗号分隔
     * @return
     */
    @RequestMapping(value = "/unlock", method = RequestMethod.POST)
    public ResultCode<String> unLockOrgDocument(HttpServletRequest request, String documentIds) {
        if(StringUtils.isBlank(documentIds)) {
            throw new YCException(Messages.MISSING_INPUT_MSG, Messages.MISSING_INPUT_CODE);
        }
        ResultCode<String> resultCode = this.organizedFileService.unLockOrgDocument(request, documentIds);
        return resultCode;
    }
}
