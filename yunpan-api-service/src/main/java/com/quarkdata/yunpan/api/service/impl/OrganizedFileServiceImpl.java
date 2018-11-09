package com.quarkdata.yunpan.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.quarkdata.quark.share.model.dataobj.Department;
import com.quarkdata.quark.share.model.dataobj.Group;
import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.dal.dao.*;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.DepartmentApi;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.GroupApi;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.UsersApi;
import com.quarkdata.yunpan.api.model.common.*;
import com.quarkdata.yunpan.api.model.dataobj.*;
import com.quarkdata.yunpan.api.model.vo.*;
import com.quarkdata.yunpan.api.service.DocumentPermissionService;
import com.quarkdata.yunpan.api.service.OrganizedFileService;
import com.quarkdata.yunpan.api.util.Md5Encrypt;
import com.quarkdata.yunpan.api.util.common.Exception.YCException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Transactional(readOnly = true, rollbackFor = Exception.class)
@Service
public class OrganizedFileServiceImpl extends BaseLogServiceImpl implements OrganizedFileService {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${IMAGE_TYPE}")
	private String IMAGE_TYPE;

	@Value("${MEDIA_TYPE}")
	private String MEDIA_TYPE;

	@Autowired
	private DocumentMapper documentMapper;

	@Autowired
	private DocumentMapper2 documentMapper2;

	@Autowired
	RecycleMapper2 recycleMapper2;

	@Autowired
	private ExternalUserMapper externalUserMapper;

	@Autowired
	private DocumentVersionMapper documentVersionMapper;

	@Autowired
	private IncConfigMapper incConfigMapper;

	@Autowired
	private DocumentCephDeleteMapper documentCephDeleteMapper;

	@Autowired
	private DocumentPermissionMapper documentPermissionMapper;
	@Autowired
	private DocumentPermissionMapper2 documentPermissionMapper2;

	@Autowired
	private CommonFileServiceImpl commonFileService;
	
	@Autowired
	private UsersApi usersApi;
	
	@Autowired
	private GroupApi groupApi;
	
	@Autowired
	private DepartmentApi departmentApi;

	@Autowired
	private DocumentPermissionService documentPermissionService;

	@Autowired
	private DocumentVersionMapper2 documentVersionMapper2;

	private LogMapper logMapper;

	private LogDocumentRelMapper logDocumentRelMapper;

	@Resource
	public void setLogMapper(LogMapper logMapper) {
		this.logMapper = logMapper;
		super.setLogMapper(logMapper);
	}

	@Resource
	public void setLogDocumentRelMapper(LogDocumentRelMapper logDocumentRelMapper) {
		this.logDocumentRelMapper = logDocumentRelMapper;
		super.setLogDocumentRelMapper(logDocumentRelMapper);
	}

	@Override
	public List<Document> getOrganizedFiles(Long incId, Long userId,
			Long userGroupId, Long deptId, Long parentId) {

		List<Document> documents = documentMapper2.getOrganizedFiles(incId,
				userId, userGroupId, deptId, parentId);
		return documents;
	}

	@Override
	public ResultCode<List<DocumentExtend>> getOrganizedFilesCarryCol_tags(
			Long incId, Long userId, List<Long> userGroupIds, Long deptId,
			Long parentId, String documentName, Long isExact, boolean isOrganizedAdmin, Integer pageNum, Integer pageSize) {
		ResultCode<List<DocumentExtend>> rc = new ResultCode<List<DocumentExtend>>();
		//查找父路径
		Document document=documentMapper.selectByPrimaryKey(parentId);
		String parentPath=null;
		if (document!=null && document.getIdPath()!=null){
			parentPath=document.getIdPath();
		}

		List<DocumentExtend> documents = documentMapper2.getOrganizedAllFilesCarryCol_tags(incId,
				userId, userGroupIds, deptId, parentId, documentName,parentPath,isExact, null, null);
		//判断权限
		documents = getPermission(incId, userId,userGroupIds, deptId, isOrganizedAdmin, documents,parentPath);
		rc.setData(documents);
		return rc;
	}

	@Transactional(readOnly = false)
	@Override
	public ResultCode<DocumentExtend> uploadFile(HttpServletRequest request, MultipartFile file, String fileName,
										 int isOverwrite, Long parentId, Long userId, String userName,
										 Long incId, Integer deptId, List<Long> groupIds,
										 boolean isAdmin) throws IOException,YunPanApiException {

        ResultCode<DocumentExtend> rc = new ResultCode<DocumentExtend>();

        // 鉴权: 读写
		if(!this.commonFileService.checkDocumentPermission(UserInfoUtil.getIncorporation().getId(), UserInfoUtil.getUserInfoVO(), parentId, DocumentConstants.DOCUMENT_TYPE_ORGANIZED, DocumentConstants.DOCUMENT_PERMISSION_READANDWRITE)) {
			rc.setCode(Messages.API_AUTHEXCEPTION_CODE);
			rc.setMsg(Messages.API_AUTHEXCEPTION_MSG);
			return rc;
		}

        Date now = new Date();

        ResultCode<Integer> permissionResult = getDocumentPermissionOfMine(parentId, userId, groupIds, deptId.longValue(), isAdmin);
        if (permissionResult.getCode() != Messages.SUCCESS_CODE) {
            //父目录权限获取失败，返回内层传递出来的错误
            rc.setCode(permissionResult.getCode());
            rc.setMsg(permissionResult.getMsg());

            return rc;
        }

        int parentPermission = permissionResult.getData();
        int readPermission = Integer.parseInt(DocumentConstants.DOCUMENT_PERMISSION_READ);
        if (parentPermission < readPermission) {
            //父目录没有权限，返回错误
            rc.setCode(ApiMessages.NO_READ_PERMISSION_CODE);
            rc.setMsg(ApiMessages.NO_READ_PERMISSION_MSG);

            return rc;
        } else {
            //父目录至少有只读权限，判断是否存在同名文件
            String documentName = file.getOriginalFilename();
            // 以fileName为准
            if (StringUtils.isNotBlank(fileName)) {
                documentName = fileName;
            }
            DocumentExample example = new DocumentExample();
            example.createCriteria().andIncIdEqualTo(incId)
                    .andTypeEqualTo(DocumentConstants.DOCUMENT_TYPE_ORGANIZED)
                    .andIsDeleteEqualTo(DocumentConstants.DOCUMENT_IS_DELETE_NO)
                    .andParentIdEqualTo(parentId)
                    .andDocumentNameEqualTo(documentName);

            List<Document> documents = documentMapper.selectByExample(example);

            if (parentPermission == readPermission) {
                //父目录有只读权限
                if (CollectionUtils.isNotEmpty(documents)) {
                    Document document = documents.get(0);
                    String permission = documentPermissionService.getPermission(
                            document.getIdPath(), userId, groupIds, deptId.longValue());
                    if (StringUtils.isNotBlank(permission) && Integer.parseInt(permission) > readPermission) {
                        //文件有读写权限，但由于父目录只读，此时只能覆盖
                        if (isOverwrite != DocumentConstants.UPLOAD_IS_OVERWRITE_YES) {
                            //没加Overwrite标记，提示可覆盖
                            rc.setCode(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_CODE_OVERWRITE_ONLY);
                            rc.setMsg(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_MSG);

                            //rc.setData(DocumentConstants.DOCUMENT_OVERWRITE_ONLY);

                            return rc;
                        } else {
                            //加Overwrite标记，覆盖
                            return commonFileService.uploadExistFile(file, isOverwrite, userId,
                                    userName, incId, rc, now, documents,
                                    DocumentConstants.DOCUMENT_TYPE_ORGANIZED);
                        }
                    }
                }

                //此时要么是无同名文件，要么是同名文件无读写权限，都不能上传
                rc.setCode(ApiMessages.NO_WRITE_PERMISSION_CODE);
                rc.setMsg(ApiMessages.NO_WRITE_PERMISSION_MSG);

                return rc;
            } else {
                //父目录有读写权限
                if (CollectionUtils.isNotEmpty(documents)) {
                    //有同名文件
                    Document document = documents.get(0);
                    String permission = documentPermissionService.getPermission(
                            document.getIdPath(), userId, groupIds, deptId.longValue());
                    if (StringUtils.isNotBlank(permission) && Integer.parseInt(permission) > readPermission) {
                        //文件有读写权限
                        if (isOverwrite != DocumentConstants.UPLOAD_IS_OVERWRITE_YES) {
                            //没加Overwrite标记，提示可重命名，可覆盖
                            rc.setCode(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_CODE);
                            rc.setMsg(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_MSG);

                            //rc.setData(DocumentConstants.DOCUMENT_OVERWRITE_AND_RENAME);

                            return rc;
                        } else {
                            //加Overwrite标记，覆盖
                            return commonFileService.uploadExistFile(file, isOverwrite, userId,
                                    userName, incId, rc, now, documents,
                                    DocumentConstants.DOCUMENT_TYPE_ORGANIZED);
                        }
                    } else {
                        //文件没读写权限，但父目录有读写权限，所以可以重命名后上传
                        if (isOverwrite != DocumentConstants.UPLOAD_IS_OVERWRITE_YES) {
                            //没加Overwrite标记，提示可重命名后上传
                            rc.setCode(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_CODE_RENAME_ONLY);
                            rc.setMsg(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_MSG);

                            //rc.setData(DocumentConstants.DOCUMENT_RENAME_ONLY);

                            return rc;
                        } else {
                            //加Overwrite标记，出错
                            rc.setCode(ApiMessages.NO_WRITE_PERMISSION_CODE);
                            rc.setMsg(ApiMessages.NO_WRITE_PERMISSION_MSG);

                            return rc;
                        }
                    }
                } else {
                    //没同名文件，上传新文件
                    logger.info("upload new file ...");
                    // 上传一个新的文档
                    Document doc = commonFileService.uploadNewDocument(request, file, parentId, userId,
                            userName, incId, now, documentName,
                            DocumentConstants.DOCUMENT_TYPE_ORGANIZED);
					DocumentExtend documentExtend = new DocumentExtend();
					BeanUtils.copyProperties(doc,documentExtend);
					rc.setData(documentExtend);
                    //再次检查是否有重复记录，如果有，抛出异常
                    documents = documentMapper.selectByExample(example);
                    if (documents.size() > 1) {
                        throw new DuplicateKeyException("文件夹ID：" + parentId + "中已存在" + documentName);
                    }
                    return rc;
                }
            }
        }
    }

	@Transactional(readOnly = false)
	@Override
	public ResultCode<DocumentExtend> uploadFileApp(HttpServletRequest request, MultipartFile file, String fileName,
										 int isOverwrite, Long parentId, Long userId, String userName,
										 Long incId, Integer deptId, List<Long> groupIds,
										 boolean isAdmin) throws IOException,YunPanApiException {

		ResultCode<DocumentExtend> rc = new ResultCode<DocumentExtend>();

		// 鉴权: 读写
		if(!this.commonFileService.checkDocumentPermission(UserInfoUtil.getIncorporation().getId(), UserInfoUtil.getUserInfoVO(), parentId, DocumentConstants.DOCUMENT_TYPE_ORGANIZED, DocumentConstants.DOCUMENT_PERMISSION_READANDWRITE)) {
			rc.setCode(Messages.API_AUTHEXCEPTION_CODE);
			rc.setMsg(Messages.API_AUTHEXCEPTION_MSG);
			return rc;
		}

		Date now = new Date();

		ResultCode<Integer> permissionResult = getDocumentPermissionOfMine(parentId, userId, groupIds, deptId.longValue(), isAdmin);
		if (permissionResult.getCode() != Messages.SUCCESS_CODE) {
			//父目录权限获取失败，返回内层传递出来的错误
			rc.setCode(permissionResult.getCode());
			rc.setMsg(permissionResult.getMsg());

			return rc;
		}

		int parentPermission = permissionResult.getData();
		int readPermission = Integer.parseInt(DocumentConstants.DOCUMENT_PERMISSION_READ);
		if (parentPermission < readPermission) {
			//父目录没有权限，返回错误
			rc.setCode(ApiMessages.NO_READ_PERMISSION_CODE);
			rc.setMsg(ApiMessages.NO_READ_PERMISSION_MSG);

			return rc;
		} else {
			//父目录至少有只读权限，判断是否存在同名文件
			String documentName = file.getOriginalFilename();
			// 以fileName为准
			if (StringUtils.isNotBlank(fileName)) {
				documentName = fileName;
			}
			DocumentExample example = new DocumentExample();
			example.createCriteria().andIncIdEqualTo(incId)
					.andTypeEqualTo(DocumentConstants.DOCUMENT_TYPE_ORGANIZED)
					.andIsDeleteEqualTo(DocumentConstants.DOCUMENT_IS_DELETE_NO)
					.andParentIdEqualTo(parentId)
					.andDocumentNameEqualTo(documentName);

			List<Document> documents = documentMapper.selectByExample(example);

			if (parentPermission == readPermission) {
				//父目录有只读权限
				if (CollectionUtils.isNotEmpty(documents)) {
					Document document = documents.get(0);
					String permission = documentPermissionService.getPermission(
							document.getIdPath(), userId, groupIds, deptId.longValue());
					if (StringUtils.isNotBlank(permission) && Integer.parseInt(permission) > readPermission) {
						if(this.IMAGE_TYPE.contains(document.getDocumentType().toLowerCase()) || this.MEDIA_TYPE.contains(document.getDocumentType().toLowerCase())) {
							isOverwrite = DocumentConstants.UPLOAD_IS_OVERWRITE_YES;
						}
						//文件有读写权限，但由于父目录只读，此时只能覆盖
						if (isOverwrite != DocumentConstants.UPLOAD_IS_OVERWRITE_YES) {
							//没加Overwrite标记，提示可覆盖
							rc.setCode(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_CODE_OVERWRITE_ONLY);
							rc.setMsg(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_MSG);

							//rc.setData(DocumentConstants.DOCUMENT_OVERWRITE_ONLY);

							return rc;
						} else {
							//加Overwrite标记，覆盖
							return commonFileService.uploadExistFile(file, isOverwrite, userId,
									userName, incId, rc, now, documents,
									DocumentConstants.DOCUMENT_TYPE_ORGANIZED);
						}
					}
				}

				//此时要么是无同名文件，要么是同名文件无读写权限，都不能上传
				rc.setCode(ApiMessages.NO_WRITE_PERMISSION_CODE);
				rc.setMsg(ApiMessages.NO_WRITE_PERMISSION_MSG);

				return rc;
			} else {
				//父目录有读写权限
				if (CollectionUtils.isNotEmpty(documents)) {
					//有同名文件
					Document document = documents.get(0);
					String permission = documentPermissionService.getPermission(
							document.getIdPath(), userId, groupIds, deptId.longValue());
					if(DocumentConstants.DOCUMENT_ROOT_PARENTID.equals(parentId)) {
						//app在根目录下上传遇到同名文件则直接自动重命名上传
						logger.info("upload new file in org root dir ...");
						// 上传一个新的文档
						documentName = this.commonFileService.generateDirName(document.getDocumentType(), parentId, incId, documentName, DocumentConstants.DOCUMENT_TYPE_ORGANIZED, userId);
						Document doc = commonFileService.uploadNewDocument(request, file, parentId, userId,
								userName, incId, now, documentName,
								DocumentConstants.DOCUMENT_TYPE_ORGANIZED);
						DocumentExtend documentExtend = new DocumentExtend();
						BeanUtils.copyProperties(doc,documentExtend);
						rc.setData(documentExtend);
						return rc;
					}
					if (StringUtils.isNotBlank(permission) && Integer.parseInt(permission) > readPermission) {
						//文件有读写权限
						if(this.IMAGE_TYPE.contains(document.getDocumentType().toLowerCase()) || this.MEDIA_TYPE.contains(document.getDocumentType().toLowerCase())) {
							isOverwrite = DocumentConstants.UPLOAD_IS_OVERWRITE_YES;
						}
						if (isOverwrite != DocumentConstants.UPLOAD_IS_OVERWRITE_YES) {
							//没加Overwrite标记，提示可重命名，可覆盖
							rc.setCode(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_CODE);
							rc.setMsg(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_MSG);

							//rc.setData(DocumentConstants.DOCUMENT_OVERWRITE_AND_RENAME);

							return rc;
						} else {
							//加Overwrite标记，覆盖
							return commonFileService.uploadExistFile(file, isOverwrite, userId,
									userName, incId, rc, now, documents,
									DocumentConstants.DOCUMENT_TYPE_ORGANIZED);
						}
					} else {
						//文件没读写权限，但父目录有读写权限，所以可以重命名后上传
						if (isOverwrite != DocumentConstants.UPLOAD_IS_OVERWRITE_YES) {
							//没加Overwrite标记，提示可重命名后上传
							rc.setCode(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_CODE_RENAME_ONLY);
							rc.setMsg(ApiMessages.UPLOADED_DOCUMENT_ALREADY_EXISTED_MSG);

							//rc.setData(DocumentConstants.DOCUMENT_RENAME_ONLY);

							return rc;
						} else {
							//加Overwrite标记，出错
							rc.setCode(ApiMessages.NO_WRITE_PERMISSION_CODE);
							rc.setMsg(ApiMessages.NO_WRITE_PERMISSION_MSG);

							return rc;
						}
					}
				} else {
					//没同名文件，上传新文件
					logger.info("upload new file ...");
					// 上传一个新的文档
					Document doc = commonFileService.uploadNewDocument(request, file, parentId, userId,
							userName, incId, now, documentName,
							DocumentConstants.DOCUMENT_TYPE_ORGANIZED);
					DocumentExtend documentExtend = new DocumentExtend();
					BeanUtils.copyProperties(doc,documentExtend);
					rc.setData(documentExtend);
					//再次检查是否有重复记录，如果有，抛出异常
					documents = documentMapper.selectByExample(example);
					if (documents.size() > 1) {
						throw new DuplicateKeyException("文件夹ID：" + parentId + "中已存在" + documentName);
					}

					return rc;
				}
			}
		}
	}

	private void checkPrivilege(Long parentId, Long userId, Integer deptId,
			List<Long> groupIds, boolean isOrganizedAdmin)  throws YunPanApiException{
		if (parentId <= 0) {
			throw new IllegalArgumentException("parentId:" + parentId);
		}
		// 检查parentId是否存在
		Document document = documentMapper.selectByPrimaryKey(parentId);
		if (document == null) {
			throw new RuntimeException("上传到的目录不存在，目录id:" + parentId);
		}
		checkWritePrivilege(parentId, userId, deptId, groupIds,
				isOrganizedAdmin);
	}

	private void checkWritePrivilege(Long parentId, Long userId,
			Integer deptId, List<Long> groupIds, boolean isOrganizedAdmin) throws YunPanApiException {
		// 需要当前角色，当前的登录人id,用户组id,部门id，去看有没有写权限
		if (!isOrganizedAdmin) {
			boolean hasPrivilege = commonFileService
					.recursionCheckUploadPrivilege(userId, deptId, groupIds,
							parentId);
			if (!hasPrivilege) {// 没有write权限
				throw new YunPanApiException(ApiMessages.NO_WRITE_PERMISSION_CODE,ApiMessages.NO_WRITE_PERMISSION_MSG);
			}
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ResultCode<DocumentExtend> createOrganizedSpace(HttpServletRequest request, String documentName,
														   List<DocumentPrivilegeVO> permissions, List<DocumentPrivilegeVOfGenerateAccounts> permissionsOfGenerateAccounts, Long userId,
														   String userName, Long incId, boolean isAdmin) throws YunPanApiException {
		// 权限检查
		if (!isAdmin) {
			throw new YunPanApiException(ApiMessages.NO_ORG_ADMIN_PERMISSION_CODE,ApiMessages.NO_ORG_ADMIN_PERMISSION_MSG);
		}
		ResultCode<DocumentExtend> resultCode = new ResultCode<>();
		ResultCode<Document> createResult = commonFileService.createOrganizedSpace(
				request, documentName, DocumentConstants.DOCUMENT_ROOT_PARENTID, userId,
				userName, incId, DocumentConstants.DOCUMENT_TYPE_ORGANIZED,
				permissions, permissionsOfGenerateAccounts);
		permissionMe(documentName, incId, userId, 0l, DocumentConstants.DOCUMENT_PERMISSION_SUPER_MANAGE);
		permissionMe(documentName, incId, userId, 0l, DocumentConstants.DOCUMENT_PERMISSION_CREATE);

		DocumentExtend documentExtend = new DocumentExtend();
		BeanUtils.copyProperties(createResult.getData(), documentExtend);
		resultCode.setData(documentExtend);

		//记录操作日志
		Document document = this.documentMapper.selectByPrimaryKey(createResult.getData().getId());
		this.addDocumentLog(request, document.getId().toString(), ActionType.CREATE_ORGANIZED_SPACE, document.getDocumentName(), document.getCreateTime());

		return resultCode;
	}

	@Transactional(readOnly = false)
	@Override
	public ResultCode<DocumentExtend> createDir(HttpServletRequest request, String documentName, Long parentId,
												Long userId, String userName, Long incId, Integer deptId, List<Long> groupIds, boolean isAdmin,
                                                List<DocumentPrivilegeVO> permissions, List<DocumentPrivilegeVOfGenerateAccounts> permissionsOfGenerateAccounts) throws YunPanApiException{
        ResultCode<DocumentExtend> resultCode = new ResultCode<>();

		// 鉴权: 读写权限
		Boolean flag = this.commonFileService.checkDocumentPermission(UserInfoUtil.getIncorporation().getId(), UserInfoUtil.getUserInfoVO(), parentId, DocumentConstants.DOCUMENT_TYPE_ORGANIZED, DocumentConstants.DOCUMENT_PERMISSION_READANDWRITE);
		if(!flag) {
			resultCode.setCode(Messages.API_AUTHEXCEPTION_CODE);
			resultCode.setMsg(Messages.API_AUTHEXCEPTION_MSG);
			return resultCode;
		}

		if(RoleConstants.SYSTEM_ADMIN_ROLE_ID.equals(UserInfoUtil.getUserInfoVO().getRole().getId())
				|| this.commonFileService.isOrganizedSpaceCreator(incId, userId, isAdmin, parentId)) {
			// 判断是否有同名目录
			DocumentExample example = new DocumentExample();
			example.createCriteria()
					.andIncIdEqualTo(incId)
					.andTypeEqualTo(DocumentConstants.DOCUMENT_TYPE_ORGANIZED)
					.andIsDeleteEqualTo(DocumentConstants.DOCUMENT_IS_DELETE_NO)
					.andParentIdEqualTo(parentId)
					.andDocumentNameEqualTo(documentName);

			List<Document> documents = documentMapper.selectByExample(example);

			if(CollectionUtils.isNotEmpty(documents)) {
				//查到同名了，返回同名目录的权限
				Document document = documents.get(0);

				String permission = documentPermissionService.getPermission(
						document.getIdPath(), userId, groupIds, deptId.longValue());
				DocumentExtend documentExtend = new DocumentExtend();

				documentExtend.setId(document.getId());
				documentExtend.setPermission(permission);

				resultCode.setCode(ApiMessages.CREATE_DIR_ALREADY_EXISTED_CODE);
				resultCode.setMsg(ApiMessages.CREATE_DIR_ALREADY_EXISTED_MSG);
				resultCode.setData(documentExtend);

				return resultCode;
			}
			try {
				ResultCode<Document> createResult = commonFileService.createDir(documentName, parentId, userId, userName, incId,
						DocumentConstants.DOCUMENT_TYPE_ORGANIZED);
				// 创建文件夹时如果是单独设置权限则添加权限数据
				if(CollectionUtils.isNotEmpty(permissions) || CollectionUtils.isNotEmpty(permissionsOfGenerateAccounts)) {
					this.setDocumentPrivilege(request, createResult.getData().getId(),
							permissions, permissionsOfGenerateAccounts, incId, false, userId, groupIds, null == deptId ? null : Long.parseLong(deptId.toString()));
				}

				resultCode.setCode(createResult.getCode());
				resultCode.setMsg(createResult.getMsg());

				DocumentExtend documentExtend = new DocumentExtend();
				PropertyUtils.copyProperties(documentExtend, createResult.getData());

				resultCode.setData(documentExtend);

				// 记录操作日志
				Document document = createResult.getData();
				this.addDocumentLog(request, document.getId().toString(),
						ActionType.CREATE_FOLDER, document.getDocumentName(), document.getCreateTime());

			} catch (YunPanApiException e) {
				if (e.getErrCode() == ApiMessages.CREATE_DIR_ALREADY_EXISTED_CODE) {
					// 遇到同名目录了，需要判断目录的权限
					//虽然前面判断过了，但出现这种情况的可能还是有的

					documents = documentMapper.selectByExample(example);

					//查到同名了，返回同名目录的权限
					Document document = documents.get(0);

					String permission = documentPermissionService.getPermission(
							document.getIdPath(), userId, groupIds, deptId.longValue());
					DocumentExtend documentExtend = new DocumentExtend();

					documentExtend.setId(document.getId());
					documentExtend.setPermission(permission);

					resultCode.setCode(ApiMessages.CREATE_DIR_ALREADY_EXISTED_CODE);
					resultCode.setMsg(ApiMessages.CREATE_DIR_ALREADY_EXISTED_MSG);
					resultCode.setData(documentExtend);
				} else {
					throw e;
				}
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				throw new YunPanApiException(Messages.SERVICE_INTERNAL_EXCEPTION_CODE,
						Messages.SERVICE_INTERNAL_EXCEPTION_MSG);
			}
			return resultCode;
		} else {

			checkPrivilege(parentId, userId, deptId, groupIds, isAdmin);

			ResultCode<Integer> permissionResult =
					getDocumentPermissionOfMine(parentId, userId, groupIds, deptId.longValue(), isAdmin);
			if (permissionResult.getCode() != Messages.SUCCESS_CODE) {
				//父目录权限获取失败，返回内层传递出来的错误
				resultCode.setCode(permissionResult.getCode());
				resultCode.setMsg(permissionResult.getMsg());

				return resultCode;
			}

			int parentPermission = permissionResult.getData();
			int readPermission = Integer.parseInt(DocumentConstants.DOCUMENT_PERMISSION_READ);
			if (parentPermission < readPermission) {
				//父目录没有权限，返回错误
				resultCode.setCode(ApiMessages.NO_WRITE_PERMISSION_CODE);
				resultCode.setMsg(ApiMessages.NO_WRITE_PERMISSION_MSG);

				return resultCode;
			} else {
				//父目录至少有读写的权限，判断是否有同名目录
				DocumentExample example = new DocumentExample();
				example.createCriteria()
						.andIncIdEqualTo(incId)
						.andTypeEqualTo(DocumentConstants.DOCUMENT_TYPE_ORGANIZED)
						.andIsDeleteEqualTo(DocumentConstants.DOCUMENT_IS_DELETE_NO)
						.andParentIdEqualTo(parentId)
						.andDocumentNameEqualTo(documentName);

				List<Document> documents = documentMapper.selectByExample(example);

				if(CollectionUtils.isNotEmpty(documents)) {
					//查到同名了，返回同名目录的权限
					Document document = documents.get(0);

					String permission = documentPermissionService.getPermission(
							document.getIdPath(), userId, groupIds, deptId.longValue());
					DocumentExtend documentExtend = new DocumentExtend();

					documentExtend.setId(document.getId());
					documentExtend.setPermission(permission);

					resultCode.setCode(ApiMessages.CREATE_DIR_ALREADY_EXISTED_CODE);
					resultCode.setMsg(ApiMessages.CREATE_DIR_ALREADY_EXISTED_MSG);
					resultCode.setData(documentExtend);

					return resultCode;
				}

				if(parentPermission == readPermission) {
					//父目录有只读权限，不能创建
					//此时也没有同名目录存在
					resultCode.setCode(ApiMessages.NO_WRITE_PERMISSION_CODE);
					resultCode.setMsg(ApiMessages.NO_WRITE_PERMISSION_MSG);
				} else {
					//父目录有读写权限
					try {
						ResultCode<Document> createResult = commonFileService.createDir(documentName, parentId, userId, userName, incId,
								DocumentConstants.DOCUMENT_TYPE_ORGANIZED);
						// 创建文件夹时如果是单独设置权限则添加权限数据
						if(CollectionUtils.isNotEmpty(permissions) || CollectionUtils.isNotEmpty(permissionsOfGenerateAccounts)) {
							if(permissions == null) {
								permissions = new ArrayList<>();
							}
							DocumentPrivilegeVO creatorPri = new DocumentPrivilegeVO();
							creatorPri.setReceiverType(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER);
							creatorPri.setReceiverId(userId);
							creatorPri.setPermission(DocumentConstants.DOCUMENT_PERMISSION_MANAGE);
							permissions.add(creatorPri);
							this.setDocumentPrivilege(request, createResult.getData().getId(),
									permissions, permissionsOfGenerateAccounts, incId, false, userId, groupIds, null == deptId ? null : Long.parseLong(deptId.toString()));
						}

						resultCode.setCode(createResult.getCode());
						resultCode.setMsg(createResult.getMsg());

						DocumentExtend documentExtend = new DocumentExtend();
						PropertyUtils.copyProperties(documentExtend, createResult.getData());

						resultCode.setData(documentExtend);

						// 记录操作日志
						Document document = createResult.getData();
						this.addDocumentLog(request, document.getId().toString(),
								ActionType.CREATE_FOLDER, document.getDocumentName(), document.getCreateTime());

					} catch (YunPanApiException e) {
						if (e.getErrCode() == ApiMessages.CREATE_DIR_ALREADY_EXISTED_CODE) {
							// 遇到同名目录了，需要判断目录的权限
							//虽然前面判断过了，但出现这种情况的可能还是有的

							documents = documentMapper.selectByExample(example);

							//查到同名了，返回同名目录的权限
							Document document = documents.get(0);

							String permission = documentPermissionService.getPermission(
									document.getIdPath(), userId, groupIds, deptId.longValue());
							DocumentExtend documentExtend = new DocumentExtend();

							documentExtend.setId(document.getId());
							documentExtend.setPermission(permission);

							resultCode.setCode(ApiMessages.CREATE_DIR_ALREADY_EXISTED_CODE);
							resultCode.setMsg(ApiMessages.CREATE_DIR_ALREADY_EXISTED_MSG);
							resultCode.setData(documentExtend);
						} else {
							throw e;
						}
					} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
						throw new YunPanApiException(Messages.SERVICE_INTERNAL_EXCEPTION_CODE,
								Messages.SERVICE_INTERNAL_EXCEPTION_MSG);
					}
				}
				return resultCode;
			}
		}
    }

	@Transactional(readOnly = false)
	@Override
	public ResultCode<String> setDocumentPrivilege(HttpServletRequest request, Long documentId,
												   List<DocumentPrivilegeVO> permissions, List<DocumentPrivilegeVOfGenerateAccounts> permissionsOfGenerateAccounts, Long incId,
												   boolean isOrganizedAdmin, Long userId, List<Long> groupIds, Long deptId) throws YunPanApiException {
		ResultCode<String> rc = new ResultCode<String>();

		Document document = documentMapper.selectByPrimaryKey(documentId);
		if(document == null) {
			rc.setCode(ApiMessages.DOCUMENT_NOT_EXIST_CODE);
			rc.setMsg(ApiMessages.DOCUMENT_NOT_EXIST_MSG);
			return rc;
		} else {
			if(!this.commonFileService.isOrganizedSpaceCreator(incId, userId, isOrganizedAdmin, documentId)) {
				//判断是否有管理权限
				String permission = documentPermissionService.getPermission(
						document.getIdPath(), userId, groupIds, deptId.longValue());

				if (StringUtils.isBlank(permission) || permission.compareTo(DocumentConstants.DOCUMENT_PERMISSION_MANAGE) < 0) {
					//没权限，返回没权限提示
					rc.setCode(ApiMessages.NO_MANAGE_PERMISSION_CODE);
					rc.setMsg(ApiMessages.NO_MANAGE_PERMISSION_MSG);
					return rc;
				}
			}
		}

		//获取设置之前的Permission
		DocumentPermissionExample oldExample = new DocumentPermissionExample();
		DocumentPermissionExample.Criteria oldCriteria = oldExample.createCriteria();
		oldCriteria.andDocumentIdEqualTo(documentId)
				.andPermissionNotEqualTo(DocumentConstants.DOCUMENT_PERMISSION_CREATE)
				.andPermissionNotEqualTo(DocumentConstants.DOCUMENT_PERMISSION_SUPER_MANAGE)
				.andPermissionNotEqualTo(DocumentConstants.DOCUMENT_PERMISSION_ASSOCIATION_READ);
		List<DocumentPermission> oldPermissions = this.documentPermissionMapper.selectByExample(oldExample);

		// 设置文档权限，先将之前的权限清除，再赋予新的权限(-1权限清除与否看其所有子文件是否单独设置过权限;)
		if("dir".equals(document.getDocumentType())) {


			DocumentPermissionExample example = new DocumentPermissionExample();
			DocumentPermissionExample.Criteria criteria = example.createCriteria();
			criteria.andDocumentIdEqualTo(documentId)
					.andPermissionNotEqualTo(DocumentConstants.DOCUMENT_PERMISSION_CREATE)
					.andPermissionNotEqualTo(DocumentConstants.DOCUMENT_PERMISSION_SUPER_MANAGE);
			// 判断是否需要清除关联只读权限: 看其所有子文件是否单独设置过权限
			DocumentExample example_child = new DocumentExample();
			example_child.createCriteria().andIncIdEqualTo(incId).andIdPathLike("%/" + documentId + "/%");
			List<Document> sons = this.documentMapper.selectByExample(example_child);
			if(CollectionUtils.isNotEmpty(sons)) {
				List<Long> sonIds = new ArrayList<>();
				for(Document son: sons) {
					if(!documentId.equals(son.getId())) {
						sonIds.add(son.getId());
					}
				}
				if(CollectionUtils.isNotEmpty(sonIds)) {
					DocumentPermissionExample permissionExample = new DocumentPermissionExample();
					permissionExample.createCriteria().andIncIdEqualTo(incId).andDocumentIdIn(sonIds);
					List<DocumentPermission> childSinglePermissions = this.documentPermissionMapper.selectByExample(permissionExample);
					if(CollectionUtils.isNotEmpty(childSinglePermissions)) {
						criteria.andPermissionNotEqualTo(DocumentConstants.DOCUMENT_PERMISSION_ASSOCIATION_READ);
					}
				}
			}

			logger.info( "delete DocumentPermission,condition is --> documentId : {}", documentId);

			documentPermissionMapper.deleteByExample(example);
		} else {
			DocumentPermissionExample example = new DocumentPermissionExample();
			example.createCriteria().andDocumentIdEqualTo(documentId);

			logger.info( "delete DocumentPermission,condition is --> documentId : {}", documentId);

			documentPermissionMapper.deleteByExample(example);
		}

		List<String> parentIds = Arrays.asList(document.getIdPath().split("/"));
		List<Long> newParentIds = new ArrayList<>();
		// List<String> newParentIdPaths = new ArrayList<>();
		if(parentIds != null && parentIds.size() > 0) {
			for(String parentId: parentIds) {
				if(StringUtils.isNotBlank(parentId) && (!parentId.equals(document.getId().toString()))) {
					// newParentIdPaths.add(this.documentMapper.selectByPrimaryKey(Long.parseLong(parentId)).getIdPath());
					newParentIds.add(Long.parseLong(parentId));
				}
			}
		}

		// 获取组织空间创建者
		String idPath = document.getIdPath();
		String organizedSpaceId = idPath.substring(1, idPath.indexOf("/", 1));
		Document organizedSpace = this.documentMapper.selectByPrimaryKey(Long.parseLong(organizedSpaceId));
		if(CollectionUtils.isNotEmpty(permissions)) {
            // 过滤调用者传过来的创建者权限,避免出现重复数据
            ListIterator<DocumentPrivilegeVO> iterator = permissions.listIterator();
            while(iterator.hasNext()) {
                DocumentPrivilegeVO next = iterator.next();
                if((organizedSpace != null && next.getReceiverType().equals(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER) && next.getReceiverId().toString().equals(organizedSpace.getCreateUser().toString()))
						|| next.getPermission().equals(DocumentConstants.DOCUMENT_PERMISSION_CREATE)
						|| next.getPermission().equals(DocumentConstants.DOCUMENT_PERMISSION_SUPER_MANAGE)) {
                    iterator.remove();
                }
            }

            // 注:过滤掉创建者之后如果要设置的内部用户权限和外部用户权限都为空，则给当前登录用户添加一条读写权限，让单独设置权限的操作生效，否则本次设置权限失败，原有继承父级权限的用户照样可以看见该文件
			if(CollectionUtils.isEmpty(permissions) && CollectionUtils.isEmpty(permissionsOfGenerateAccounts)) {
				if(permissions == null) {
					permissions = new ArrayList<>();
				}
				DocumentPrivilegeVO currentUserPermission = new DocumentPrivilegeVO();
				currentUserPermission.setReceiverType(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER);
				currentUserPermission.setReceiverId(userId);
				currentUserPermission.setPermission(DocumentConstants.DOCUMENT_PERMISSION_READANDWRITE);
				permissions.add(currentUserPermission);
			}

            for (DocumentPrivilegeVO dp : permissions) {

                DocumentPermission record = new DocumentPermission();
                record.setIncId(incId);
                record.setDocumentId(documentId);
                BeanUtils.copyProperties(dp, record);
                if(CollectionUtils.isEmpty(oldPermissions))
					oldPermissions.remove(record);
                logger.info("insert into DocumentPermission,obj is : {}",
                        JSON.toJSONString(record));

                documentPermissionMapper2.insert(record);

                // 获取接收者所在组和部门
                List<Group> dpGroups = this.groupApi.getGroupByUserId(dp.getReceiverId());
                List<Long> dpGroupIds = new ArrayList<>();
                if(CollectionUtils.isNotEmpty(dpGroups)) {
                    for(Group group: dpGroups) {
                        dpGroupIds.add(group.getId().longValue());
                    }
                }
                Department dpDepartment = this.departmentApi.getDepartmentByUserId(dp.getReceiverId());

                // 查询父级文档对当前登录用户是否有权限(关联只读), 没有的话设置关联只读权限,为跨级显示赋权用
				if(CollectionUtils.isNotEmpty(newParentIds)){
					for(Long parentId: newParentIds) {
						List<DocumentPermission> directPermissions = this.documentPermissionService.getDirectPermission(incId, dp.getReceiverId(), dpGroupIds, dpDepartment == null ? null : dpDepartment.getId().longValue(), parentId);
						if(CollectionUtils.isEmpty(directPermissions)) {
							DocumentPermission record_parent = new DocumentPermission();
							record_parent.setIncId(incId);
							record_parent.setDocumentId(parentId);
							record_parent.setReceiverId(dp.getReceiverId());
							record_parent.setReceiverType(dp.getReceiverType());
							record_parent.setPermission(DocumentConstants.DOCUMENT_PERMISSION_ASSOCIATION_READ);
							this.documentPermissionMapper2.insert(record_parent);
						} else {
							List<String> directPermissionList = new ArrayList<>();
							for(DocumentPermission documentPermission: directPermissions) {
								directPermissionList.add(documentPermission.getPermission());
							}
							if(!directPermissionList.contains(DocumentConstants.DOCUMENT_PERMISSION_ASSOCIATION_READ)) {
								DocumentPermission record_parent = new DocumentPermission();
								record_parent.setIncId(incId);
								record_parent.setDocumentId(parentId);
								record_parent.setReceiverId(dp.getReceiverId());
								record_parent.setReceiverType(dp.getReceiverType());
								record_parent.setPermission(DocumentConstants.DOCUMENT_PERMISSION_ASSOCIATION_READ);
								this.documentPermissionMapper2.insert(record_parent);
							}
						}
					}
				}
            }
        }

		// 处理系统生成账户赋权问题
		if(CollectionUtils.isNotEmpty(permissionsOfGenerateAccounts)) {
			for(DocumentPrivilegeVOfGenerateAccounts docPrivilegeVOfGenerateAccounts: permissionsOfGenerateAccounts) {
				// 1.将生成的账户插入数据库
				if(StringUtils.isNotBlank(docPrivilegeVOfGenerateAccounts.getReceiverName())
						&& StringUtils.isNotBlank(docPrivilegeVOfGenerateAccounts.getReceiverPassword())) {
					Users user_save = null;
					Boolean isExist = false;
					// 查询该系统账号是否已经添加过, 如果添加过,仅更新权限和有效期数据
					ResultCode<List<Users>> userByUsername = this.usersApi.getUserByUsername(request, docPrivilegeVOfGenerateAccounts.getReceiverName());
					if(userByUsername != null && CollectionUtils.isNotEmpty(userByUsername.getData())) {
						user_save = userByUsername.getData().get(0);
						isExist = true;
					} else {
						Users user=new Users();
						user.setUserName(docPrivilegeVOfGenerateAccounts.getReceiverName());
						user.setPassword(Md5Encrypt.md5(docPrivilegeVOfGenerateAccounts.getReceiverPassword()));
						user.setSource(Constants.USER_SOURCE_GENERATE_ACCOUNT);
						user.setIncid(incId.intValue());
						user.setUserStatus(UserStatus.USER_STATUS_ACTIVATED);
						user_save = this.usersApi.save(user).getData();
					}

					if(user_save != null) {
						if(isExist) {
							// 更新
							// 2.更新该账户权限
							DocumentPermission record_permission = new DocumentPermission();
							record_permission.setPermission(docPrivilegeVOfGenerateAccounts.getPermission());

							if(CollectionUtils.isEmpty(oldPermissions))
								oldPermissions.remove(record_permission);

							DocumentPermissionExample example_permission = new DocumentPermissionExample();
							DocumentPermissionExample.Criteria criteria_permission = example_permission.createCriteria();
							criteria_permission.andIncIdEqualTo(incId).andDocumentIdEqualTo(documentId).andReceiverTypeEqualTo("3").andReceiverIdEqualTo(user_save.getUserId().longValue());
							this.documentPermissionMapper.updateByExampleSelective(record_permission, example_permission);
							// 3.更新账号有效期数据
							ExternalUser record_expiryDate = new ExternalUser();
							record_expiryDate.setCreateUser(userId);
							record_expiryDate.setCreateTime(new Date());
							record_expiryDate.setExpiryDate(docPrivilegeVOfGenerateAccounts.getExpiryDate());
							ExternalUserExample example_expiryDate = new ExternalUserExample();
							ExternalUserExample.Criteria criteria_expiryDate = example_expiryDate.createCriteria();
							criteria_expiryDate.andIncIdEqualTo(incId).andUserIdEqualTo(user_save.getUserId().longValue());
							this.externalUserMapper.updateByExampleSelective(record_expiryDate, example_expiryDate);
						} else {
							// 新建
							// 2.给该账户赋权
							DocumentPermission record = new DocumentPermission();
							record.setIncId(incId);
							record.setDocumentId(documentId);
							record.setReceiverId(user_save.getUserId().longValue());
							record.setReceiverType("3");
							record.setPermission(docPrivilegeVOfGenerateAccounts.getPermission());

							if(CollectionUtils.isEmpty(oldPermissions))
								oldPermissions.remove(record);
							this.documentPermissionMapper2.insert(record);
							this.logger.info("insert into DocumentPermission,obj is : {}",
									JSON.toJSONString(record));

							// 3.保存账号有效期数据
							ExternalUser externalUser = new ExternalUser();
							externalUser.setIncId(incId);
							externalUser.setUserId(user_save.getUserId().longValue());
							externalUser.setCreateUser(userId);
							externalUser.setCreateTime(new Date());
							externalUser.setExpiryDate(docPrivilegeVOfGenerateAccounts.getExpiryDate());
							this.externalUserMapper.insert(externalUser);
						}


						// 查询父级文档对当前登录用户是否有权限, 没有的话设置关联只读权限,为跨级显示赋权用
						if(CollectionUtils.isNotEmpty(newParentIds)){
							for(Long parentId: newParentIds) {
								List<DocumentPermission> directPermissions = this.documentPermissionService.getDirectPermission(incId, user_save.getUserId().longValue(), null, null, parentId);
								if(CollectionUtils.isEmpty(directPermissions)) {
									DocumentPermission record_parent = new DocumentPermission();
									record_parent.setIncId(incId);
									record_parent.setDocumentId(parentId);
									record_parent.setReceiverId(user_save.getUserId().longValue());
									record_parent.setReceiverType(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_GENERATE_ACCOUNT);
									record_parent.setPermission(DocumentConstants.DOCUMENT_PERMISSION_ASSOCIATION_READ);
									this.documentPermissionMapper2.insert(record_parent);
								} else {
									List<String> directPermissionList = new ArrayList<>();
									for(DocumentPermission documentPermission: directPermissions) {
										directPermissionList.add(documentPermission.getPermission());
									}
									if(!directPermissionList.contains(DocumentConstants.DOCUMENT_PERMISSION_ASSOCIATION_READ)) {
										DocumentPermission record_parent = new DocumentPermission();
										record_parent.setIncId(incId);
										record_parent.setDocumentId(parentId);
										record_parent.setReceiverId(user_save.getUserId().longValue());
										record_parent.setReceiverType(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_GENERATE_ACCOUNT);
										record_parent.setPermission(DocumentConstants.DOCUMENT_PERMISSION_ASSOCIATION_READ);
										this.documentPermissionMapper2.insert(record_parent);
									}
								}
							}
						}
					}
				}
			}
		}

		if( !CollectionUtils.isEmpty(oldPermissions)){
			//获取文件路径
			String[] docIdStr = idPath.substring(1, idPath.length() - 2).split("/");
			for(DocumentPermission per : oldPermissions){
				//查询路径上每一级的最大Permission如果为-1则删除
				List<DocumentPermission> tempPermissions =  this.documentPermissionMapper2.getDocumentMaxPermission(docIdStr,per.getReceiverId(), per.getReceiverType());
				if( !CollectionUtils.isEmpty(tempPermissions)){
					for(DocumentPermission delete :tempPermissions){
						if(StringUtils.equals(delete.getPermission() , DocumentConstants.DOCUMENT_PERMISSION_ASSOCIATION_READ)){

							this.documentPermissionMapper.deleteByPrimaryKey(delete.getId());
						}
					}
				}
			}
		}
		//如果当前用户没有了管理或则以上的权限，赋予管理权限
		String permission = documentPermissionService.getPermission(
				documentMapper.selectByPrimaryKey(documentId).getIdPath(), userId, groupIds, deptId);
		//String maxPermission = documentMapper2.getMaxPermission(documentId, incId, userId, groupIds, deptId);
		if (StringUtils.isBlank(permission) ||
				permission.compareTo(DocumentConstants.DOCUMENT_PERMISSION_MANAGE)<0){
			if (StringUtils.isNotBlank(permission)){
				DocumentPermissionExample documentPermissionExample = new DocumentPermissionExample();
				DocumentPermissionExample.Criteria criteria = documentPermissionExample.createCriteria();
				criteria.andDocumentIdEqualTo(documentId)
						.andReceiverIdEqualTo(userId)
						.andReceiverTypeEqualTo("0");
				documentPermissionMapper.deleteByExample(documentPermissionExample);
			}
			DocumentPermission onlyOne = new DocumentPermission();
			onlyOne.setIncId(incId);
			onlyOne.setDocumentId(documentId);
			onlyOne.setReceiverId(userId);
			onlyOne.setReceiverType(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER);
			onlyOne.setPermission(DocumentConstants.DOCUMENT_PERMISSION_MANAGE);
			this.documentPermissionMapper2.insert(onlyOne);
		}
		// 记录操作日志
		this.addDocumentLog(request, document.getId().toString(), ActionType.SET_DOCUMENT_PRIVILEGE, document.getDocumentName(), new Date());

		return rc;
	}

	@Override
	public ResultCode<List<DocumentExtend>> adminFileList(Long incId, Long parentId, Long userId, String documentName, Long isExact, Integer pageNum, Integer pageSize) {
		ResultCode<List<DocumentExtend>> rc =new ResultCode<List<DocumentExtend>>();
        Document parentDocument = this.documentMapper.selectByPrimaryKey(parentId);
        List<DocumentExtend> lists;
		if (pageNum!=null&&pageSize!=null){
			 lists = documentMapper2.getAdminFileList(incId, userId, parentId, parentDocument == null ? null : parentDocument.getIdPath(), documentName, isExact, (pageNum - 1) * pageSize, pageSize);
		}else {
			lists= documentMapper2.getAdminFileList(incId, userId, parentId, parentDocument == null ? null : parentDocument.getIdPath(), documentName, isExact, null, null);
		}
		rc.setData(lists);
		return rc;
	}

	@Override
	public ResultCode<List<DocumentPermissionVO>> getDocDocumentPermissions(
			Long docId, Long incId) {
		return this.commonFileService.getDocumentPermissionsByDocId(docId, incId);
	}

	public ResultCode<Integer> getDocumentPermissionOfMine(Long docId, Long userId, List<Long> userGroupIds, Long deptId, boolean isAdmin){
		ResultCode<Integer> rc = new ResultCode<>();

		Document document = documentMapper.selectByPrimaryKey(docId);
		if(document == null) {
			if(DocumentConstants.DOCUMENT_ROOT_PARENTID.equals(docId)) {
				if(isAdmin) {
					rc.setData(Integer.parseInt(DocumentConstants.DOCUMENT_PERMISSION_CREATE));
				}
			} else {
				rc.setCode(ApiMessages.DOCUMENT_NOT_EXIST_CODE);
				rc.setMsg(ApiMessages.DOCUMENT_NOT_EXIST_MSG);
			}
		} else {
			//判断是否有读写权限
			String permission = documentPermissionService.getPermission(
					document.getIdPath(), userId, userGroupIds, deptId);

			if(permission == null) {
				rc.setData(-1);
			} else {
				rc.setData(Integer.valueOf(permission));
			}
		}
		return rc;
	}

	private void permissionMe(String documentName, Long incId, Long userId, Long parentId, String permission) {
		long documentId = documentMapper2.getDocumentIdByName(documentName, incId, userId, parentId, DocumentConstants.DOCUMENT_TYPE_ORGANIZED);
		DocumentPermission dPermission = new DocumentPermission();
		dPermission.setDocumentId(documentId);
		dPermission.setIncId(incId);
		dPermission.setReceiverId(userId);
		dPermission.setReceiverType(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER);
		// 给组织空间创建者赋予超级管理权限,可以看见组织空间下所有文件
		dPermission.setPermission(permission);
		documentPermissionMapper2.insert(dPermission);
	}

	@Override
	public ResultCode<List<DocumentZtreeVO>> getGreaterThanWritePrivilegeIncDirectoryByParentId(Long incId, Long userId, List<Long> userGroupIds, Long deptId, Long parentId, String documentName, boolean isAdmin) {
		ResultCode<List<DocumentZtreeVO>> rc = new ResultCode<List<DocumentZtreeVO>>();
		//查找父路径
		Document document=documentMapper.selectByPrimaryKey(parentId);
		String parentPath=null;
		if (document!=null && document.getIdPath()!=null){
			parentPath=document.getIdPath();
		}
		List<DocumentZtreeVO> documents = documentMapper2.getGreaterThanWritePrivilegeIncDirectoryByParentId(incId,
				userId, userGroupIds, deptId, parentId, documentName, parentPath, 0L);

		// 判断权限
		if(CollectionUtils.isNotEmpty(documents)) {
			// 判断当前用户是否是组织空间创建者,如果是,返回所有文件
			if (isAdmin) {
				if (StringUtils.isNotBlank(parentPath)) {
					String organizedSpaceId = parentPath.substring(1, parentPath.indexOf("/", 1));
					DocumentPermissionExample documentPermissionExample = new DocumentPermissionExample();
					documentPermissionExample.createCriteria()
							.andIncIdEqualTo(incId)
							.andDocumentIdEqualTo(Long.parseLong(organizedSpaceId))
							.andReceiverTypeEqualTo(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER)
							.andReceiverIdEqualTo(userId)
							.andPermissionEqualTo(DocumentConstants.DOCUMENT_PERMISSION_SUPER_MANAGE);
					List<DocumentPermission> permissionsOfSuperManage = this.documentPermissionMapper.selectByExample(documentPermissionExample);
					if (CollectionUtils.isNotEmpty(permissionsOfSuperManage)) {
						for (DocumentZtreeVO documentZtreeVO : documents) {
							documentZtreeVO.setPermission(DocumentConstants.DOCUMENT_PERMISSION_SUPER_MANAGE);
						}
						if(document != null && document.getParentId() == 0) {
							// 保留组织空间根节点
							DocumentZtreeVO documentZtreeVO = new DocumentZtreeVO();
							documentZtreeVO.setId(parentId);
							documentZtreeVO.setIncId(incId);
							documentZtreeVO.setName("全部文件");
							documentZtreeVO.setIsParent(true);
							documentZtreeVO.setPermission(DocumentConstants.DOCUMENT_PERMISSION_SUPER_MANAGE);
							documentZtreeVO.setSetPermission(true);
							documentZtreeVO.setType("0");
							documents.add(documentZtreeVO);
						}
						rc.setData(documents);
						return rc;
					}
				}
			}

			ListIterator<DocumentZtreeVO> iter = documents.listIterator();
			while (iter.hasNext()) {
				DocumentZtreeVO documentZtreeVO = iter.next();
				String permission = documentPermissionService.getPermission(
						documentZtreeVO.getIdPath(), userId, userGroupIds, deptId);
				if (StringUtils.isBlank(permission)) {
					iter.remove();
				} else {
					if (!permission.equals("-1")) {
						documentZtreeVO.setPermission(permission);
					} else {
						// 判断该关联权限是否是直接与该文档关联的
						List<DocumentPermission> documentPermissions = new ArrayList<>();

						DocumentPermissionExample docPermissionExample_user = new DocumentPermissionExample();
						List<String> types = new ArrayList<>();
						types.add("0");
						types.add("3");
						docPermissionExample_user.createCriteria().andIncIdEqualTo(incId)
								.andDocumentIdEqualTo(documentZtreeVO.getId())
								.andReceiverIdEqualTo(userId).andReceiverTypeIn(types);
						List<DocumentPermission> documentPermissions_user = this.documentPermissionMapper.selectByExample(docPermissionExample_user);
						if (!CollectionUtils.isEmpty(documentPermissions_user)) {
							documentPermissions.addAll(documentPermissions_user);
						}

						if (CollectionUtils.isNotEmpty(userGroupIds)) {
							DocumentPermissionExample docPermissionExample_group = new DocumentPermissionExample();
							docPermissionExample_group.createCriteria().andIncIdEqualTo(incId)
									.andDocumentIdEqualTo(documentZtreeVO.getId())
									.andReceiverIdIn(userGroupIds).andReceiverTypeEqualTo("1");
							List<DocumentPermission> documentPermissions_group = this.documentPermissionMapper.selectByExample(docPermissionExample_group);
							if (!CollectionUtils.isEmpty(documentPermissions_group)) {
								documentPermissions.addAll(documentPermissions_group);
							}
						}

						DocumentPermissionExample docPermissionExample_dept = new DocumentPermissionExample();
						docPermissionExample_dept.createCriteria().andIncIdEqualTo(incId)
								.andDocumentIdEqualTo(documentZtreeVO.getId())
								.andReceiverIdEqualTo(deptId).andReceiverTypeEqualTo("2");
						List<DocumentPermission> documentPermissions_dept = this.documentPermissionMapper.selectByExample(docPermissionExample_dept);
						if (!CollectionUtils.isEmpty(documentPermissions_dept)) {
							documentPermissions.addAll(documentPermissions_dept);
						}

						if (CollectionUtils.isEmpty(documentPermissions)) {
							iter.remove();
						} else {
							documentZtreeVO.setPermission(permission);
						}
					}
				}
			}
		}

		if(document != null && document.getParentId() == 0) {
			// 保留组织空间根节点
			DocumentZtreeVO documentZtreeVO = new DocumentZtreeVO();
			documentZtreeVO.setId(parentId);
			documentZtreeVO.setIncId(incId);
			documentZtreeVO.setName("全部文件");
			documentZtreeVO.setIsParent(true);
			String permission = this.documentPermissionService.getPermission(document.getIdPath(), userId, userGroupIds, deptId);
			documentZtreeVO.setPermission(permission);
			documentZtreeVO.setSetPermission(true);
			documentZtreeVO.setType("0");
			documents.add(documentZtreeVO);
		}
		rc.setData(documents);
		return rc;
	}

	@Override
	public ResultCode<String> getFileCount(Long incId, Long userId,List<Long> userGroupIds,Long deptId) {
		ResultCode<String> result = new ResultCode<String>();
		result.setData("0");
		Long count = documentMapper2.getOrganizedFileCount(incId, userId, userGroupIds, deptId);
		result.setData(String.valueOf(count));
		return result;
	}

	@Override
	public ResultCode<List<DocumentExtend>> getAllChildren(Long incId, Long userId, List<Long> userGroupIds, Long deptId, Long parentId) {
		ResultCode<List<DocumentExtend>> result = new ResultCode<List<DocumentExtend>>();
		try {
			Document document=documentMapper.selectByPrimaryKey(parentId);
			String parentPath=null;
			if (document!=null && document.getIdPath()!=null){
				parentPath=document.getIdPath();
			}
			List<DocumentExtend> documents = documentMapper2.getOrganizedFileChildren(parentId == 0 ? null : String.valueOf(parentId), incId, userId, userGroupIds, deptId);
			documents = this.getPermission(incId, userId, userGroupIds, deptId, true, documents, parentPath);
			result.setData(documents);
		}
		catch (Exception e){
			logger.error(e.toString());
			throw e;
		}
		return  result;
	}

	/**
	 * 批量删除组织文件
	 * @param incId
	 * @param userId
	 * @param userGroupIds
	 * @param deptId
	 * @param docIds
	 * @param isAdmin
	 * @return true-全部删除 false-部分删除
	 */
	@Transactional(readOnly = false)
	@Override
	public Boolean delDocBatch(Long incId, Long userId, String userName, List<Long> userGroupIds, Long deptId, String docIds, boolean isAdmin) {
		if(StringUtils.isNotBlank(docIds)) {
			String[] docIdArr = docIds.split(",");
			List<OrganizedDocumentDeleteVO> documentsToBeDelete = new ArrayList<>();
			List<Long> ids_documentsToBeDelete = new ArrayList<>();
			List<Long> allVersionId = null;

			if(docIdArr != null && docIdArr.length > 0) {
				for(String docId: docIdArr) {
					Document document = this.documentMapper.selectByPrimaryKey(Long.parseLong(docId));
					String organizedSpaceId = document.getIdPath().substring(1, document.getIdPath().indexOf("/", 1));
					DocumentPermissionExample documentPermissionExample = new DocumentPermissionExample();
					documentPermissionExample.createCriteria()
							.andIncIdEqualTo(incId)
							.andDocumentIdEqualTo(Long.parseLong(organizedSpaceId))
							.andReceiverTypeEqualTo(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER)
							.andReceiverIdEqualTo(userId)
							.andPermissionEqualTo(DocumentConstants.DOCUMENT_PERMISSION_SUPER_MANAGE);
					List<DocumentPermission> permissionsOfSuperManage = this.documentPermissionMapper.selectByExample(documentPermissionExample);
					if (RoleConstants.SYSTEM_ADMIN_ROLE_ID.equals(UserInfoUtil.getUserInfoVO().getRole().getId())
							|| (isAdmin && CollectionUtils.isNotEmpty(permissionsOfSuperManage))) {
						// 系统管理员和组织空间创建者不再判断删除权限
						if(document.getDocumentType().equals(DocumentConstants.DOCUMENT_DOCUMENT_TYPE_DIR)) {
							List<DocumentExtend> children = this.documentMapper2.getOrganizedFileChildren(docId, incId, userId, userGroupIds, deptId);
							if(children != null && children.size() > 0) {
								for(DocumentExtend doc: children) {
									OrganizedDocumentDeleteVO organizedDocumentDeleteVO = new OrganizedDocumentDeleteVO();
									BeanUtils.copyProperties(doc, organizedDocumentDeleteVO);
									organizedDocumentDeleteVO.setCanBeDelete(true);
									documentsToBeDelete.add(organizedDocumentDeleteVO);
								}
							}
						} else {
							OrganizedDocumentDeleteVO organizedDocumentDeleteVO = new OrganizedDocumentDeleteVO();
							BeanUtils.copyProperties(document, organizedDocumentDeleteVO);
							organizedDocumentDeleteVO.setCanBeDelete(true);
							documentsToBeDelete.add(organizedDocumentDeleteVO);
						}
					} else{
						if(document != null) {
							List<OrganizedDocumentDeleteVO> childrenVO = null;
							if(document.getDocumentType().equals("dir")) {
								List<DocumentExtend> children = this.documentMapper2.getOrganizedFileChildren(docId, incId, userId, userGroupIds, deptId);
								childrenVO = new ArrayList<>();
								if(children != null && children.size() > 0) {
									for(DocumentExtend doc: children) {
										OrganizedDocumentDeleteVO docVO = new OrganizedDocumentDeleteVO();
										BeanUtils.copyProperties(doc, docVO);
										childrenVO.add(docVO);
									}
									for(OrganizedDocumentDeleteVO organizedDocumentDeleteVO: childrenVO) {
										String permission = this.documentPermissionService.getPermission(organizedDocumentDeleteVO.getIdPath(), userId, userGroupIds, deptId);
										if(permission != null && permission.compareTo(DocumentConstants.DOCUMENT_PERMISSION_READANDWRITE) >= 0) {
											// 具有删除权限:读写
											organizedDocumentDeleteVO.setCanBeDelete(true);
										} else {
											organizedDocumentDeleteVO.setCanBeDelete(false);
										}
									}
									labelA:
									for(OrganizedDocumentDeleteVO organizedDocumentDeleteVO: childrenVO) {
										if(organizedDocumentDeleteVO.getDocumentType().equals("dir")) {
											// 判断其子文件是否有不可删除的, 如果有将该文件夹修改为不可删除
											for(OrganizedDocumentDeleteVO orgDocDeleteVO: childrenVO) {
												if(orgDocDeleteVO.getIdPath().contains(organizedDocumentDeleteVO.getId().toString()) && orgDocDeleteVO.getCanBeDelete() == false) {
													organizedDocumentDeleteVO.setCanBeDelete(false);
													continue labelA;
												}
											}
										}
									}
								}
								if(CollectionUtils.isNotEmpty(childrenVO)) {
									documentsToBeDelete.addAll(childrenVO);
								}
							} else {
								OrganizedDocumentDeleteVO documentDeleteVO = new OrganizedDocumentDeleteVO();
								BeanUtils.copyProperties(document, documentDeleteVO);
								String permission_parent = this.documentPermissionService.getPermission(document.getIdPath(), userId, userGroupIds, deptId);
								if(permission_parent != null && permission_parent.compareTo(DocumentConstants.DOCUMENT_PERMISSION_READANDWRITE) >= 0) {
									documentDeleteVO.setCanBeDelete(true);
								} else {
									documentDeleteVO.setCanBeDelete(false);
								}
								documentsToBeDelete.add(documentDeleteVO);
							}
						}
					}
				}
			}
			List<Long> ids_to_be_recycle = new ArrayList<>();
			if(CollectionUtils.isNotEmpty(documentsToBeDelete)) {
				List<String> tempIds = new ArrayList<>();
				for(OrganizedDocumentDeleteVO organizedDocumentDeleteVO: documentsToBeDelete) {
					if(organizedDocumentDeleteVO.getCanBeDelete() == true) {
						ids_documentsToBeDelete.add(organizedDocumentDeleteVO.getId());
						String idPath= organizedDocumentDeleteVO.getIdPath();
						String[] ids = idPath.split("/");
						if(Collections.disjoint(tempIds, Arrays.asList(ids))) {
							ids_to_be_recycle.add(organizedDocumentDeleteVO.getId());
							tempIds.add(organizedDocumentDeleteVO.getId().toString());
						}
					}
				}
			}
			if(CollectionUtils.isNotEmpty(ids_documentsToBeDelete)) {
				// 获取所有待删的版本文件id，用于更新document_version表
				allVersionId = new ArrayList<>();
				for(Long docId: ids_documentsToBeDelete) {
					List<Long> allVersionRecordsOfCurrentDocument = this.documentVersionMapper2.getAllNotDelVersionIdByDocId(docId);
					if(CollectionUtils.isNotEmpty(allVersionRecordsOfCurrentDocument)) {
						allVersionId.addAll(allVersionRecordsOfCurrentDocument);
					}
				}
			}
			if(CollectionUtils.isNotEmpty(ids_documentsToBeDelete)) {
				logger.info("批量删除组织文档开始！");
				logger.info("document表操作开始");
				int num1 = documentMapper2.delDocByBatch(ids_documentsToBeDelete, userId, userName);
				logger.info("document表操作结束，数目：" + num1);

				logger.info("recycle表操作开始");
				List<Recycle> recycleList = getRecycleAddList(ids_to_be_recycle, userId, incId);
				int num3 = recycleMapper2.addRecycleByBatch(recycleList);
				logger.info("recycle表操作完成，数目：" + num3);

				logger.info("删除父文件夹的关联只读权限开始");

				//去除所要删除文件夹的所有权限
				DocumentPermissionExample newExample = new DocumentPermissionExample();
				DocumentPermissionExample.Criteria newCriteria = newExample.createCriteria();
				newCriteria.andIncIdEqualTo(incId)
						.andDocumentIdIn(ids_documentsToBeDelete)
						.andPermissionNotEqualTo("3")
						.andPermissionNotEqualTo("99");
				documentPermissionMapper.deleteByExample(newExample);

				//去除上层文件的关联只读权限
				if (CollectionUtils.isNotEmpty(recycleList)){
					for (Recycle recycle : recycleList) {
						String[] split = recycle.getDocumentIdPath().split("/");
						for (int i =split.length-1;i>=0;i--) {
							if (StringUtils.isNotBlank(split[i]) && (Long.parseLong(split[i])!=recycle.getDocumentId())){

								//获取当前的文件
								Document document = documentMapper.selectByPrimaryKey(Long.parseLong(split[i]));

								//判断删除的是否为文件夹，如果是执行操作
								if (document.getDocumentType().equals(DocumentConstants.DOCUMENT_DOCUMENT_TYPE_DIR)) {
									//查询当前父文件（祖文件夹）的所有子文件列表
									DocumentExample example_child = new DocumentExample();
									example_child.createCriteria().andIncIdEqualTo(incId).andParentIdEqualTo(document.getId())
											.andIsDeleteEqualTo(DocumentConstants.DOCUMENT_IS_DELETE_NO);
									List<Document> sons = documentMapper.selectByExample(example_child);
									List<Long> sonIds = new ArrayList<>();
									for (Document son : sons) {
										sonIds.add(son.getId());
									}

									//查询当前父目录的关联只读权限列表
									DocumentPermissionExample parentPermissionExample = new DocumentPermissionExample();
									parentPermissionExample.createCriteria().andIncIdEqualTo(incId).andDocumentIdEqualTo(document.getId())
											.andPermissionEqualTo(DocumentConstants.DOCUMENT_PERMISSION_ASSOCIATION_READ);
									List<DocumentPermission> parentPermissions = this.documentPermissionMapper.selectByExample(parentPermissionExample);

									//遍历权限列表中的每一个权限拥有者
									if (CollectionUtils.isNotEmpty(parentPermissions)) {
										for (DocumentPermission p : parentPermissions) {
											//判断对此文件夹下的其他文件有权限
											DocumentPermissionExample permissionExample = new DocumentPermissionExample();
											DocumentPermissionExample.Criteria criteria1 = permissionExample.createCriteria();
											criteria1.andIncIdEqualTo(incId)
													.andReceiverTypeEqualTo(p.getReceiverType())
													.andReceiverIdEqualTo(p.getReceiverId());
											if (CollectionUtils.isNotEmpty(sonIds)){
												criteria1.andDocumentIdIn(sonIds);
											}
											List<DocumentPermission> childSinglePermissions = this.documentPermissionMapper.selectByExample(permissionExample);
											if (CollectionUtils.isEmpty(childSinglePermissions)) {

												logger.info("删除当前权限拥有者的关联只读权限");
												DocumentPermissionExample example = new DocumentPermissionExample();
												DocumentPermissionExample.Criteria criteria = example.createCriteria();
												criteria.andDocumentIdEqualTo(document.getId())
														.andPermissionEqualTo(DocumentConstants.DOCUMENT_PERMISSION_ASSOCIATION_READ)
														.andReceiverTypeEqualTo(p.getReceiverType())
														.andReceiverIdEqualTo(p.getReceiverId());
												documentPermissionMapper.deleteByExample(example);
											}
										}
									}
								}
							}
						}
					}
				}
				logger.info("删除父（祖）文件夹的关联只读权限结束");

				if (!allVersionId.isEmpty()) {
					logger.info("document_version表操作开始");
					int num2 = documentVersionMapper2.delDocVersionByIds(allVersionId, userId);
					logger.info("document_version表操作结束，数目：" + num2);
				}
				logger.info("组织doc移入回收站操作完成！");

			}
			if(CollectionUtils.isNotEmpty(documentsToBeDelete)) {
				if(CollectionUtils.isNotEmpty(ids_documentsToBeDelete)) {
					if(documentsToBeDelete.size() > ids_documentsToBeDelete.size()) {
						return false;
					} else {
						return true;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		return null;
	}

	private List<Recycle> getRecycleAddList(List<Long> docIds, long updateUserId, long incId) {

		List<Recycle> list = new ArrayList<>();
		// 先统一加, 不添加某一文件夹下的
		for (Long docId : docIds) {
			Document document = documentMapper.selectByPrimaryKey(docId);
			String documentType = document.getDocumentType();
			Long docVersionId = document.getDocumentVersionId();
			if (!Constants.DOCUMENT_FOLDER_TYPE.equals(documentType) && null != docVersionId) {
				List<Long> versionIdList = documentVersionMapper2.getAllNotDelVersionIdByDocId(docId);
				for (long versionId : versionIdList) {
					if (versionId != docVersionId) {
						Recycle recycle = new Recycle();
						recycle.setIncId(incId);
						recycle.setCreateUser(updateUserId);
						recycle.setCreateTime(new Date());
						recycle.setIsDelete("0");
						recycle.setDocumentId(docId);
						recycle.setDocumentVersionId(versionId);
						recycle.setIsVisiable("0");
						recycle.setDocumentParentId(document.getParentId());
						recycle.setDocumentIdPath(document.getIdPath());
						list.add(recycle);
					}
				}
			}
			Recycle recycle = new Recycle();
			recycle.setIncId(incId);
			recycle.setCreateUser(updateUserId);
			recycle.setCreateTime(new Date());
			recycle.setIsDelete("0");
			recycle.setDocumentId(docId);
			recycle.setDocumentVersionId(docVersionId);
			recycle.setIsVisiable("1");
			recycle.setDocumentParentId(document.getParentId());
			recycle.setDocumentIdPath(document.getIdPath());
			list.add(recycle);
		}
		return list;
	}

	@Override
	public ResultTwoDataCode<List<DocumentPermissionVO>, List<Long>> getDocDocumentPermissionsToTheRoot(Long docId, Long incId) {
		ResultTwoDataCode<List<DocumentPermissionVO>, List<Long>> objectObjectResultTwoDataCode = new ResultTwoDataCode<>();
		List<DocumentPermissionVO> result = null;
		List<Long> ids = new ArrayList<>();
		String[] idArr = this.documentMapper.selectByPrimaryKey(docId).getIdPath().split("/");
		for(String id: idArr) {
			if(StringUtils.isNotBlank(id)) {
				ids.add(Long.parseLong(id));
			}
		}
		Collections.sort(ids, Collections.<Long>reverseOrder());
		for(Long id: ids) {
			DocumentPermissionExample example=new DocumentPermissionExample();
			example.createCriteria()
					.andDocumentIdEqualTo(id)
					.andIncIdEqualTo(incId)
					.andPermissionNotEqualTo(DocumentConstants.DOCUMENT_PERMISSION_ASSOCIATION_READ)
					.andPermissionNotEqualTo(DocumentConstants.DOCUMENT_PERMISSION_SUPER_MANAGE);
			List<DocumentPermission> permissions = documentPermissionMapper.selectByExample(example);
			if(CollectionUtils.isNotEmpty(permissions)) {
				// 获取组织空间创建者的权限
				Long orgSpaceId = ids.get(ids.size() - 1);
				DocumentPermissionExample orgSpaceExample = new DocumentPermissionExample();
				orgSpaceExample.createCriteria()
						.andIncIdEqualTo(incId)
						.andDocumentIdEqualTo(orgSpaceId)
						.andReceiverTypeEqualTo(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER)
						.andPermissionEqualTo(DocumentConstants.DOCUMENT_PERMISSION_CREATE);
				List<DocumentPermission> documentPermissions = this.documentPermissionMapper.selectByExample(orgSpaceExample);
				if(CollectionUtils.isNotEmpty(documentPermissions)) {
					permissions.add(documentPermissions.get(0));
				}
				permissions = this.filterPermissions(permissions);
				result = this.commonFileService.encapsulateDocumentPermission(incId, docId, permissions);
				break;
			}
		}
		//获取系统管理员
		ResultCode<List<Long>> adminUserListByFilter = usersApi.getAdminUserListByFilter();
		//过滤权限中是否有系统管理员（除了文件创建者）
		List<DocumentPermissionVO> endResult = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(result)){
			for (DocumentPermissionVO vo:result) {
				if (!(adminUserListByFilter.getData().contains(vo.getId()) &&
						!vo.getPermission().equals(DocumentConstants.DOCUMENT_PERMISSION_CREATE))){
					endResult.add(vo);
				}
			}
		}
		objectObjectResultTwoDataCode.setData(endResult);
		objectObjectResultTwoDataCode.setSecondData(adminUserListByFilter.getData());
		return objectObjectResultTwoDataCode;
	}

	/**
	 * 过滤重复权限(脏数据)
	 * @param permissions
	 * @return
	 */
	private List<DocumentPermission> filterPermissions(List<DocumentPermission> permissions) {
		List<DocumentPermission> permissions_new = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(permissions)) {
			// 分类
			List<DocumentPermission> permissions_user = new ArrayList<>();
			List<DocumentPermission> permissions_user_generate = new ArrayList<>();
			List<DocumentPermission> permissions_group = new ArrayList<>();
			List<DocumentPermission> permissions_dept = new ArrayList<>();
			for(DocumentPermission documentPermission: permissions) {
				if(documentPermission.getReceiverType().equals(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER)) {
					permissions_user.add(documentPermission);
				}
				if(documentPermission.getReceiverType().equals(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_GENERATE_ACCOUNT)) {
					permissions_user_generate.add(documentPermission);
				}
				if(documentPermission.getReceiverType().equals(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_GROUP)) {
					permissions_group.add(documentPermission);
				}
				if(documentPermission.getReceiverType().equals(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_DEPT)) {
					permissions_dept.add(documentPermission);
				}
			}
			if(CollectionUtils.isNotEmpty(permissions_user)) {
				permissions_user = this.sortAndDistinctPermissions(permissions_user);
				permissions_new.addAll(permissions_user);
			}
			if(CollectionUtils.isNotEmpty(permissions_user_generate)) {
				permissions_user_generate = this.sortAndDistinctPermissions(permissions_user_generate);
				permissions_new.addAll(permissions_user_generate);
			}
			if(CollectionUtils.isNotEmpty(permissions_group)) {
				permissions_group = this.sortAndDistinctPermissions(permissions_group);
				permissions_new.addAll(permissions_group);
			}
			if(CollectionUtils.isNotEmpty(permissions_dept)) {
				permissions_dept = this.sortAndDistinctPermissions(permissions_dept);
				permissions_new.addAll(permissions_dept);
			}
		}
		return permissions_new;
	}

	/**
	 * 权限按最大值去重
	 * @param permissions
	 * @return
	 */
	private List<DocumentPermission> sortAndDistinctPermissions(List<DocumentPermission> permissions) {
		if(CollectionUtils.isNotEmpty(permissions)) {
			Collections.sort(permissions, new Comparator<DocumentPermission>() {
				@Override
				public int compare(DocumentPermission o1, DocumentPermission o2) {
					// 按照权限降序排序
					if(o1.getPermission().compareTo(o2.getPermission()) < 0) {
						return 1;
					}
					if(o1.getPermission().compareTo(o2.getPermission()) == 0) {
						return 0;
					}
					return -1;
				}
			});
			// 去重
			if(CollectionUtils.isNotEmpty(permissions)) {
				for(int i = 0; i < permissions.size() - 1; i++) {
					for(int j = permissions.size() - 1; j > i; j--) {
						if(permissions.get(j).getReceiverType().equals(permissions.get(i).getReceiverType())
								&& permissions.get(j).getReceiverId().equals(permissions.get(i).getReceiverId())) {
							permissions.remove(j);
						}
					}
				}
			}
		}
		return permissions;
	}

	@Override
	public List<DocumentExtend> getPermission(Long incId, Long userId, List<Long> userGroupIds, Long deptId,
											  boolean isAdmin,List<DocumentExtend>documents, String parentPath){
		if(CollectionUtils.isNotEmpty(documents)) {
			// 如果当前用户是系统管理员或组织空间创建者,返回所有文件
			if (isAdmin) {
				if(RoleConstants.SYSTEM_ADMIN_ROLE_ID.equals(UserInfoUtil.getUserInfoVO().getRole().getId())) {

					return documents;
				} else if (StringUtils.isNotBlank(parentPath)) {
					String organizedSpaceId = parentPath.substring(1, parentPath.indexOf("/", 1));
					DocumentPermissionExample documentPermissionExample = new DocumentPermissionExample();
					documentPermissionExample.createCriteria()
							.andIncIdEqualTo(incId)
							.andDocumentIdEqualTo(Long.parseLong(organizedSpaceId))
							.andReceiverTypeEqualTo(DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_USER)
							.andReceiverIdEqualTo(userId)
							.andPermissionEqualTo(DocumentConstants.DOCUMENT_PERMISSION_SUPER_MANAGE);
					List<DocumentPermission> permissionsOfSuperManage = this.documentPermissionMapper.selectByExample(documentPermissionExample);
					if (CollectionUtils.isNotEmpty(permissionsOfSuperManage)) {
						for (DocumentExtend documentExtend : documents) {
							documentExtend.setPermission(DocumentConstants.DOCUMENT_PERMISSION_SUPER_MANAGE);
						}
						return documents;
					}
				}
			}

			ListIterator<DocumentExtend> iter = documents.listIterator();
			while (iter.hasNext()) {
				DocumentExtend documentExtend = iter.next();
				// 看当前文件是否在共享空间根目录,如果是,只对上传者可见
				if((!documentExtend.getDocumentType().equals(DocumentConstants.DOCUMENT_DOCUMENT_TYPE_DIR)) && documentExtend.getParentId().equals(DocumentConstants.DOCUMENT_ROOT_PARENTID)) {
					if(!documentExtend.getCreateUser().equals(userId)) {
						iter.remove();
					} else {
						documentExtend.setPermission(DocumentConstants.DOCUMENT_PERMISSION_SUPER_MANAGE);
					}
				} else {
					String permission = documentPermissionService.getPermission(
							documentExtend.getIdPath(), userId, userGroupIds, deptId);
					if (StringUtils.isBlank(permission)) {
						iter.remove();
					} else {
						if (!permission.equals("-1")) {
							documentExtend.setPermission(permission);
						} else {
							// 判断该关联权限是否是直接与该文档关联的
							List<DocumentPermission> documentPermissions = new ArrayList<>();

							DocumentPermissionExample docPermissionExample_user = new DocumentPermissionExample();
							List<String> types = new ArrayList<>();
							types.add("0");
							types.add("3");
							docPermissionExample_user.createCriteria().andIncIdEqualTo(incId)
									.andDocumentIdEqualTo(documentExtend.getId())
									.andReceiverIdEqualTo(userId).andReceiverTypeIn(types);
							List<DocumentPermission> documentPermissions_user = this.documentPermissionMapper.selectByExample(docPermissionExample_user);
							if (!CollectionUtils.isEmpty(documentPermissions_user)) {
								documentPermissions.addAll(documentPermissions_user);
							}

							if (CollectionUtils.isNotEmpty(userGroupIds)) {
								DocumentPermissionExample docPermissionExample_group = new DocumentPermissionExample();
								docPermissionExample_group.createCriteria().andIncIdEqualTo(incId)
										.andDocumentIdEqualTo(documentExtend.getId())
										.andReceiverIdIn(userGroupIds).andReceiverTypeEqualTo("1");
								List<DocumentPermission> documentPermissions_group = this.documentPermissionMapper.selectByExample(docPermissionExample_group);
								if (!CollectionUtils.isEmpty(documentPermissions_group)) {
									documentPermissions.addAll(documentPermissions_group);
								}
							}

							DocumentPermissionExample docPermissionExample_dept = new DocumentPermissionExample();
							docPermissionExample_dept.createCriteria().andIncIdEqualTo(incId)
									.andDocumentIdEqualTo(documentExtend.getId())
									.andReceiverIdEqualTo(deptId).andReceiverTypeEqualTo("2");
							List<DocumentPermission> documentPermissions_dept = this.documentPermissionMapper.selectByExample(docPermissionExample_dept);
							if (!CollectionUtils.isEmpty(documentPermissions_dept)) {
								documentPermissions.addAll(documentPermissions_dept);
							}

							if (CollectionUtils.isEmpty(documentPermissions)) {
								iter.remove();
							} else {
								documentExtend.setPermission(permission);
							}
						}
					}
				}

			}
		}
		return documents;
	}

    @Transactional(readOnly = false)
	@Override
	public ResultCode<String> lockOrgDocument(HttpServletRequest request, String documentIds) {
		ResultCode<String> resultCode = new ResultCode<>();
        String[] documentIdArr = documentIds.split(",");
        Users user = UserInfoUtil.getUserInfoVO().getUser();
        for (String docId: documentIdArr) {
            Document document = this.documentMapper.selectByPrimaryKey(Long.parseLong(docId));
            // TODO 验证权限
            if(DocumentConstants.DOCUMENT_TYPE_ORGANIZED.equals(document.getType())) {
                if(DocumentConstants.IS_LOCK_NO.equals(document.getIsLock())) {
                    if(DocumentConstants.DOCUMENT_DOCUMENT_TYPE_DIR.equals(document.getDocumentType())) {
                        // 锁定文件夹
                        Document record = new Document();
                        record.setIsLock(DocumentConstants.IS_LOCK_YES);
                        record.setLockTime(new Date());
                        record.setLockUser(user.getUserId().longValue());
                        record.setLockUsername(user.getUserName());
                        DocumentExample example = new DocumentExample();
                        example.createCriteria().andIncIdEqualTo(user.getIncid().longValue()).andIdPathLike(document.getIdPath() + "%");
                        this.documentMapper.updateByExampleSelective(record, example);
                    } else {
                        // 锁定单独文件
                        Document record = new Document();
                        record.setId(document.getId());
                        record.setIsLock(DocumentConstants.IS_LOCK_YES);
                        record.setLockTime(new Date());
                        record.setLockUser(user.getUserId().longValue());
                        record.setLockUsername(user.getUserName());
                        this.documentMapper.updateByPrimaryKeySelective(record);
                    }
                } else {
                    throw new YCException(Messages.LOCK_DOCUMENT_STATUS_ERROR_MSG, Messages.LOCK_DOCUMENT_STATUS_ERROR_CODE);
                }
            } else {
                throw new YCException(Messages.LOCK_DOCUMENT_TYPE_ERROR_MSG, Messages.LOCK_DOCUMENT_TYPE_ERROR_CODE);
            }
        }
        return resultCode;
    }

    @Transactional(readOnly = false)
    @Override
    public ResultCode<String> unLockOrgDocument(HttpServletRequest request, String documentIds) {
        ResultCode<String> resultCode = new ResultCode<>();
        String[] documentIdArr = documentIds.split(",");
        Users user = UserInfoUtil.getUserInfoVO().getUser();
        for (String docId: documentIdArr) {
            Document document = this.documentMapper.selectByPrimaryKey(Long.parseLong(docId));
            if(DocumentConstants.DOCUMENT_TYPE_ORGANIZED.equals(document.getType())) {
                if(DocumentConstants.IS_DELETE_YES.equals(document.getIsLock())) {
                    if(document.getLockUser().toString().equals(user.getUserId().toString()) || UserInfoUtil.isSystemAdmin()) {
                        if(DocumentConstants.DOCUMENT_DOCUMENT_TYPE_DIR.equals(document.getDocumentType())) {
                            // 解锁文件夹
                            this.documentMapper2.unLockOrgDocument(user.getIncid(), document.getIdPath(), null);
                        } else {
                            // 解锁单独文件
                            this.documentMapper2.unLockOrgDocument(user.getIncid(), null, document.getId());
                        }
                    } else {
                        throw new YCException(Messages.UNLOCK_DOCUMENT_NO_AUTH_ERROR_MSG, Messages.UNLOCK_DOCUMENT_NO_AUTH_ERROR_CODE);
                    }
                } else {
                    throw new YCException(Messages.UNLOCK_DOCUMENT_STATUS_ERROR_MSG, Messages.UNLOCK_DOCUMENT_STATUS_ERROR_CODE);
                }
            } else {
                throw new YCException(Messages.LOCK_DOCUMENT_TYPE_ERROR_MSG, Messages.LOCK_DOCUMENT_TYPE_ERROR_CODE);
            }
        }
        return resultCode;
    }

}
