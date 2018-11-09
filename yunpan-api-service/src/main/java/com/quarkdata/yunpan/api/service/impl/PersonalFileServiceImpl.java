package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.dal.dao.*;
import com.quarkdata.yunpan.api.model.common.*;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.dataobj.DocumentExample;
import com.quarkdata.yunpan.api.model.dataobj.DocumentPermission;
import com.quarkdata.yunpan.api.model.vo.DocumentExtend;
import com.quarkdata.yunpan.api.model.vo.DocumentZtreeVO;
import com.quarkdata.yunpan.api.service.PersonalFileService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Transactional(readOnly = true, rollbackFor = Exception.class)
@Service
public class PersonalFileServiceImpl extends BaseLogServiceImpl implements PersonalFileService {

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
	private DocumentVersionMapper documentVersionMapper;

	@Autowired
	private IncConfigMapper incConfigMapper;

	@Autowired
	private DocumentCephDeleteMapper documentCephDeleteMapper;
	
	@Autowired
	private CommonFileServiceImpl commonFileService;

	@Autowired
	private DocumentPermissionMapper2 documentPermissionMapper2;

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
	public List<Document> getPersonalFiles(Long incId, Long userId,Long parentId) {

		DocumentExample example = new DocumentExample();
		example.createCriteria().andIncIdEqualTo(incId)
				.andCreateUserEqualTo(userId)
				.andTypeEqualTo(DocumentConstants.DOCUMENT_TYPE_PERSONAL)
				.andParentIdEqualTo(DocumentConstants.DOCUMENT_ROOT_PARENTID)
				.andIsDeleteEqualTo(DocumentConstants.DOCUMENT_IS_DELETE_NO);
		example.setOrderByClause("update_time DESC");

		List<Document> files = documentMapper.selectByExample(example);
		return files;
	}

	@Override
	public ResultCode<List<DocumentExtend>> getPersonalFilesCarryCol_tags(Long incId, Long userId, Long parentId, String documentName,Long isExact, Integer pageNum, Integer pageSize) {
		ResultCode<List<DocumentExtend>> rc = new ResultCode<List<DocumentExtend>>();
		//先根据parentId把此对应的document查出来
		Document document=documentMapper.selectByPrimaryKey(parentId);
		List<DocumentExtend> documents=new ArrayList<>();
		Integer startNum = null;
		if(pageNum != null && pageNum > 0 && pageSize != null && pageSize > 0 ) {
			startNum = (pageNum - 1) * pageSize;
		} else {
			pageSize = null;
		}
		String parentPath=null;
		if (document!=null && document.getIdPath()!=null){
			parentPath=document.getIdPath();
		}
		if (StringUtils.isBlank(documentName) || isExact == 1){
			documents = documentMapper2.getPersonalFilesCarryCol_tags(incId, userId, parentId,documentName, isExact, startNum, pageSize);
		}else{
			if (parentPath==null){
				parentPath="/";
			}
			documents=documentMapper2.getPersonalFilesByDocName(incId, userId, parentId,documentName,parentPath, startNum, pageSize);
		}
		rc.setData(documents);
		return rc;
	}


	@Transactional(readOnly = false)
	@Override
	public ResultCode<DocumentExtend> uploadFile(HttpServletRequest request, MultipartFile file, String fileName,
										 int isOverwrite, Long parentId, Long userId, String userName,
										 Long incId) throws IOException {

		ResultCode<DocumentExtend> rc = new ResultCode<DocumentExtend>();
		// 鉴权: 读写
		Boolean flag = this.commonFileService.checkDocumentPermission(Integer.parseInt(incId.toString()), UserInfoUtil.getUserInfoVO(), parentId, DocumentConstants.DOCUMENT_TYPE_PERSONAL, DocumentConstants.DOCUMENT_PERMISSION_READANDWRITE);
		if(!flag) {
			rc.setCode(Messages.API_AUTHEXCEPTION_CODE);
			rc.setMsg(Messages.API_AUTHEXCEPTION_MSG);
			return rc;
		}
		Date now = new Date();

		String documentName = file.getOriginalFilename();
		// 以fileName为准
		if (StringUtils.isNotBlank(fileName)) {
			documentName = fileName;
		}
		DocumentExample example = new DocumentExample();
		example.createCriteria()
				.andTypeEqualTo(DocumentConstants.DOCUMENT_TYPE_PERSONAL)
				.andIsDeleteEqualTo(DocumentConstants.DOCUMENT_IS_DELETE_NO)
				.andCreateUserEqualTo(userId)
				.andParentIdEqualTo(parentId)
				.andDocumentNameEqualTo(documentName);

		List<Document> documents = documentMapper.selectByExample(example);

		if (CollectionUtils.isNotEmpty(documents)) {
			logger.info("upload exist file ...");
			rc = commonFileService.uploadExistFile(file, isOverwrite, userId, userName, incId,
					rc, now, documents,DocumentConstants.DOCUMENT_TYPE_PERSONAL);
			if (!rc.equals(Messages.SUCCESS_CODE)) {
				return rc;
			}
		} else {
			logger.info("upload new file ...");
			// 上传一个新的文档
			Document doc = commonFileService.uploadNewDocument(request, file, parentId, userId, userName, incId, now, documentName, DocumentConstants.DOCUMENT_TYPE_PERSONAL);
			DocumentExtend documentExtend = new DocumentExtend();
			BeanUtils.copyProperties(doc,documentExtend);
			rc.setData(documentExtend);
			permissionMe(documentName, incId, userId, parentId);

			//再次检查是否有重复记录，如果有，抛出异常
			documents = documentMapper.selectByExample(example);
			if (documents.size() > 1) {
				throw new DuplicateKeyException("文件夹ID：" + parentId + "中已存在" + documentName);
			}
		}
		return rc;
	}

	@Transactional(readOnly = false)
	@Override
	public ResultCode<DocumentExtend> uploadFileApp(HttpServletRequest request, MultipartFile file, String fileName,
										 int isOverwrite, Long parentId, Long userId, String userName,
										 Long incId) throws IOException {

		ResultCode<DocumentExtend> rc = new ResultCode<DocumentExtend>();
		// 鉴权: 读写
		Boolean flag = this.commonFileService.checkDocumentPermission(Integer.parseInt(incId.toString()), UserInfoUtil.getUserInfoVO(), parentId, DocumentConstants.DOCUMENT_TYPE_PERSONAL, DocumentConstants.DOCUMENT_PERMISSION_READANDWRITE);
		if(!flag) {
			rc.setCode(Messages.API_AUTHEXCEPTION_CODE);
			rc.setMsg(Messages.API_AUTHEXCEPTION_MSG);
			return rc;
		}
		Date now = new Date();

		String documentName = file.getOriginalFilename();
		// 以fileName为准
		if (StringUtils.isNotBlank(fileName)) {
			documentName = fileName;
		}
		DocumentExample example = new DocumentExample();
		example.createCriteria()
				.andTypeEqualTo(DocumentConstants.DOCUMENT_TYPE_PERSONAL)
				.andIsDeleteEqualTo(DocumentConstants.DOCUMENT_IS_DELETE_NO)
				.andCreateUserEqualTo(userId)
				.andParentIdEqualTo(parentId)
				.andDocumentNameEqualTo(documentName);

		List<Document> documents = documentMapper.selectByExample(example);

		if (CollectionUtils.isNotEmpty(documents)
				&& !this.IMAGE_TYPE.contains(documents.get(0).getDocumentType().toLowerCase())
				&& !this.MEDIA_TYPE.contains((documents.get(0).getDocumentType().toLowerCase()))) {
			logger.info("upload exist file ...");
			rc = commonFileService.uploadExistFile(file, isOverwrite, userId, userName, incId,
					rc, now, documents,DocumentConstants.DOCUMENT_TYPE_PERSONAL);
			if (!rc.equals(Messages.SUCCESS_CODE)) {
				return rc;
			}
		} else {
			if(CollectionUtils.isNotEmpty(documents)) {
				Document document = documents.get(0);
				// 文档重命名后上传
				documentName = this.commonFileService.generateDirName(document.getDocumentType(), parentId, incId, documentName,  DocumentConstants.DOCUMENT_TYPE_PERSONAL, userId);
			}
			logger.info("upload new file ...");
			// 上传一个新的文档
			Document doc = commonFileService.uploadNewDocument(request, file, parentId, userId, userName, incId, now, documentName, DocumentConstants.DOCUMENT_TYPE_PERSONAL);
			DocumentExtend documentExtend = new DocumentExtend();
			BeanUtils.copyProperties(doc,documentExtend);
			rc.setData(documentExtend);
			permissionMe(documentName, incId, userId, parentId);

			//再次检查是否有重复记录，如果有，抛出异常
			documents = documentMapper.selectByExample(example);
			if (documents.size() > 1) {
				throw new DuplicateKeyException("文件夹ID：" + parentId + "中已存在" + documentName);
			}
		}
		return rc;
	}

	@Transactional(readOnly = false)
	@Override
	public ResultCode<Document> createDir(HttpServletRequest request, String documentName, Long parentId,
										  Long userId, String userName, Long incId) throws YunPanApiException {
		ResultCode<Document> resultCode = null;
		// 鉴权: 读写权限
		Boolean flag = this.commonFileService.checkDocumentPermission(UserInfoUtil.getIncorporation().getId(), UserInfoUtil.getUserInfoVO(), parentId, DocumentConstants.DOCUMENT_TYPE_PERSONAL, DocumentConstants.DOCUMENT_PERMISSION_READANDWRITE);
		if(!flag) {
			resultCode = new ResultCode<>();
			resultCode.setCode(Messages.API_AUTHEXCEPTION_CODE);
			resultCode.setMsg(Messages.API_AUTHEXCEPTION_MSG);
			return resultCode;
		}
		resultCode = commonFileService.createDir(documentName, parentId, userId, userName, incId, DocumentConstants.DOCUMENT_TYPE_PERSONAL);
		permissionMe(documentName, incId, userId, parentId);

		// 记录操作日志
		Document document = resultCode.getData();
		this.addDocumentLog(request, document.getId().toString(), ActionType.CREATE_FOLDER, document.getDocumentName(), document.getCreateTime());

		return resultCode;
	}
	
	private void permissionMe(String documentName, Long incId, Long userId, Long parentId) {
		long documentId = documentMapper2.getDocumentIdByName(documentName, incId, userId, parentId, DocumentConstants.DOCUMENT_TYPE_PERSONAL);
		DocumentPermission dPermission = new DocumentPermission();
		dPermission.setDocumentId(documentId);
		dPermission.setIncId(incId);
		dPermission.setReceiverId(userId);
		dPermission.setReceiverType("0");
		dPermission.setPermission("1");
		documentPermissionMapper2.insert(dPermission);
	}

	@Override
	public ResultCode<List<DocumentZtreeVO>> getPersonalDirectoryByParentId(Long incId, Long userId, Long parentId) {
		ResultCode<List<DocumentZtreeVO>> result = new ResultCode<List<DocumentZtreeVO>>();
		List<DocumentZtreeVO> documents = documentMapper2
				.getPersonalDirectoryByParentId(incId, userId, parentId);

		if (parentId == 0) {
			// 保留个人空间根节点
			DocumentZtreeVO documentZtreeVO = new DocumentZtreeVO();
			documentZtreeVO.setId(0L);
			documentZtreeVO.setIncId(incId);
			documentZtreeVO.setName("全部文件");
			documentZtreeVO.setIsParent(true);
			documentZtreeVO.setPermission("1");
			documentZtreeVO.setSetPermission(true);
			documentZtreeVO.setType("1");
			documents.add(documentZtreeVO);
		}

		result.setData(documents);

		return result;
	}

	@Override
	public ResultCode<String> getPersonalFileCount(Long incId, Long userId) {
		ResultCode<String> result = new ResultCode<String>();
		result.setData("0");
		List<String> ids = documentMapper2.getPersonalFileIdList(incId, userId);
		if(ids != null){
			result.setData(String.valueOf(ids.size()));
		}
		return result;
	}

	@Override
	public ResultCode<List<Document>> getAllChildren(Long incId, Long userId, Long parentId) {
		ResultCode<List<Document>> result = new ResultCode<List<Document>>();
		List<Document> documents = documentMapper2.getPersonalFileChildren(parentId == 0 ? null : String.valueOf(parentId), incId, userId);
		result.setData(documents);
		return result;
	}
}