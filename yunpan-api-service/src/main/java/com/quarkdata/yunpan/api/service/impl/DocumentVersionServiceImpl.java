package com.quarkdata.yunpan.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.quarkdata.yunpan.api.dal.dao.DocumentMapper;
import com.quarkdata.yunpan.api.dal.dao.DocumentVersionMapper;
import com.quarkdata.yunpan.api.dal.dao.LogDocumentRelMapper;
import com.quarkdata.yunpan.api.dal.dao.LogMapper;
import com.quarkdata.yunpan.api.model.common.ActionType;
import com.quarkdata.yunpan.api.model.common.DocumentConstants;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.dataobj.DocumentExample;
import com.quarkdata.yunpan.api.model.dataobj.DocumentVersion;
import com.quarkdata.yunpan.api.model.dataobj.DocumentVersionExample;
import com.quarkdata.yunpan.api.model.vo.DocumentVersionVO;
import com.quarkdata.yunpan.api.service.DocumentVersionService;
import com.quarkdata.yunpan.api.util.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Transactional(readOnly = true, rollbackFor = Exception.class)
@Service
public class DocumentVersionServiceImpl extends BaseLogServiceImpl implements DocumentVersionService {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private DocumentMapper documentMapper;

	@Autowired
	private DocumentVersionMapper documentVersionMapper;

	@Autowired
	private CommonFileServiceImpl commonFileService;

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
	public ResultCode<List<DocumentVersionVO>> getDocumentVersions(
			Long documentId, Long incId) {

		ResultCode<List<DocumentVersionVO>> resultCode=new ResultCode<List<DocumentVersionVO>>();
		DocumentExample documentExample = new DocumentExample();

		documentExample.createCriteria().andIncIdEqualTo(incId)
				.andIdEqualTo(documentId)
				.andIsDeleteEqualTo(DocumentConstants.DOCUMENT_IS_DELETE_NO);

		DocumentVersionExample example = new DocumentVersionExample();

		example.createCriteria().andIncIdEqualTo(incId)
				.andDocumentIdEqualTo(documentId)
				.andIsDeleteEqualTo(DocumentConstants.DOCUMENT_IS_DELETE_NO);

		String docName = "";
		String type = "";
		List<Document> docs = documentMapper.selectByExample(documentExample);
		if(!CollectionUtils.isEmpty(docs) && docs.size() > 0){
			docName = docs.get(0).getDocumentName();
			type = docs.get(0).getDocumentType();
			if(StringUtils.isNotBlank(docName) && docName.contains(".")) {
				docName = docName.substring(0, docName.lastIndexOf("."));
			}
			if(StringUtils.isNotBlank(type)) {
				type = "." + type;
			}
		}
		List<DocumentVersion> docVersions = documentVersionMapper.selectByExample(example);
		List<DocumentVersionVO> resultData=new ArrayList<>();
		if(CollectionUtils.isNotEmpty(docVersions)){
			for(DocumentVersion docVersion:docVersions){
				DocumentVersionVO data=new DocumentVersionVO();
				data.setId(docVersion.getId());
				if(docVersion.getSize()!=null) {
					data.setSize(docVersion.getSize());
				}
				data.setDocumentId(documentId);
				data.setUpdateTime(docVersion.getUpdateTime());
				data.setUpdateUsername(docVersion.getUpdateUsername());
				data.setVersion(docVersion.getVersion());
				data.setDocumentName(docName + "-v" + new Double(docVersion.getVersion()) + type);
				resultData.add(data);
			}
		}
		
		resultCode.setData(resultData);
		return resultCode;
	}

	@Transactional(readOnly = false)
	@Override
	public ResultCode<String> resetVersion(HttpServletRequest request, Long documentId, Long toVersionId, Long userId, String userName) {
		ResultCode<String> resultCode=new ResultCode<String>();
		// 鉴权: 读写权限
		/*Boolean flag = this.commonFileService.checkDocumentPermission(UserInfoUtil.getIncorporation().getId(), UserInfoUtil.getUserInfoVO(), documentId, DocumentConstants.DOCUMENT_TYPE_ORGANIZED, DocumentConstants.DOCUMENT_PERMISSION_READANDWRITE);
		if(!flag) {
			resultCode.setCode(Messages.API_AUTHEXCEPTION_CODE);
			resultCode.setMsg(Messages.API_AUTHEXCEPTION_MSG);
			return resultCode;
		}*/
		Date now=new Date();
		
		DocumentVersion toVersion = documentVersionMapper.selectByPrimaryKey(toVersionId);
		
		DocumentVersion record=new DocumentVersion();
		
		BeanUtils.copyProperties(toVersion, record);

		Document document = documentMapper.selectByPrimaryKey(documentId);
		logger.info("update document,before document is : {}",JSON.toJSONString(document));
		DocumentVersion currentVersion = documentVersionMapper.selectByPrimaryKey(document.getDocumentVersionId());
		record.setVersion(currentVersion.getVersion()+1);
		record.setUpdateTime(now);
		record.setUpdateUser(userId);
		record.setUpdateUsername(userName);
		
		logger.info("reset version,before version is : {}",JSON.toJSONString(toVersion));
		documentVersionMapper.insert(record);
		logger.info("reset version,new version is : {}",JSON.toJSONString(record));
		
		//修改document
		document.setDocumentVersionId(record.getId());
		document.setSize(record.getSize());
		document.setUpdateTime(now);
		document.setUpdateUser(userId);
		document.setUpdateUsername(userName);
		
		documentMapper.updateByPrimaryKeySelective(document);
		logger.info("update document,after document is : {}",JSON.toJSONString(document));

		// 记录操作日志
		this.addDocumentLog(request, documentId.toString(), ActionType.RESTORING_HISTORICAL_VERSION,
				document.getDocumentName() + "恢复至版本" + toVersion.getVersion(), document.getUpdateTime());
		
		return resultCode;
	}

	@Override
	public DocumentVersion getDocumentVersionByVersionId(long id) {
		return this.documentVersionMapper.selectByPrimaryKey(id);
	}

}
