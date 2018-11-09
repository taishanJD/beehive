package com.quarkdata.yunpan.api.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.quarkdata.yunpan.api.dal.dao.*;
import com.quarkdata.yunpan.api.model.common.*;
import com.quarkdata.yunpan.api.model.dataobj.*;
import com.quarkdata.yunpan.api.model.dataobj.DocumentExample.Criteria;
import com.quarkdata.yunpan.api.model.vo.RecycleFileVO;
import com.quarkdata.yunpan.api.service.DeleteService;
import com.quarkdata.yunpan.api.service.FullTextService;
import com.quarkdata.yunpan.api.util.DateUtil;
import com.quarkdata.yunpan.api.util.S3Utils;
import com.quarkdata.yunpan.api.util.StringUtils;
import com.quarkdata.yunpan.api.util.StringUtils2;
import com.quarkdata.yunpan.api.util.common.config.Global;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.Map.Entry;

@Service
@Transactional(readOnly = false, rollbackFor = Exception.class)
public class DeleteServiceImpl extends BaseLogServiceImpl implements DeleteService {

	@Autowired
	DocumentMapper documentMapper;

	@Autowired
	DocumentMapper2 documentMapper2;

	@Autowired
	RecycleMapper recycleMapper;

	@Autowired
	RecycleMapper2 recycleMapper2;

	@Autowired
	DocumentVersionMapper documentVersionMapper;

	@Autowired
	DocumentVersionMapper2 documentVersionMapper2;

	@Autowired
	DocumentCephDeleteMapper documentCephDeleteMapper;

	@Autowired
	DocumentCephDeleteMapper2 documentCephDeleteMapper2;

	@Autowired
	private DocumentPermissionMapper2 documentPermissionMapper2;

	@Autowired
	IncConfigMapper incConfigMapper;

	@Autowired
	private FullTextService fullTextService;

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

	static Logger logger = Logger.getLogger(DeleteServiceImpl.class);

	@Override
	public ResultCode<List<String>> delDocBatch(HttpServletRequest request, String docIdStr, long updateUserId, String updateUsename, long incId) {

		ResultCode<List<String>> result = new ResultCode<List<String>>();

		List<Long> docIds = StringUtils2.convertStringParamToList(docIdStr);

		if (docIds.isEmpty()) {
			result.setCode(Messages.MISSING_INPUT_CODE);
			result.setMsg(Messages.MISSING_INPUT_MSG);
			result.setData(null);
			return result;
		}
		/*for(Long docId: docIds) {
			Document document = this.documentMapper.selectByPrimaryKey(docId);
			if(document != null) {
				// 个人文件
				if(document.getType().equals(DocumentConstants.DOCUMENT_TYPE_PERSONAL)
						&& !document.getCreateUser().toString().equals(UserInfoUtil.getUserInfoVO().getUser().getUserId().toString())) {
					result.setCode(Messages.API_AUTHEXCEPTION_CODE);
					result.setMsg(Messages.API_AUTHEXCEPTION_MSG);
					return result;
				} else if(document.getType().equals(DocumentConstants.DOCUMENT_TYPE_ORGANIZED)) {
					// 组织文件
					result.setCode(Messages.API_AUTHEXCEPTION_CODE);
					result.setMsg(Messages.API_AUTHEXCEPTION_MSG);
					return result;
				} else if(document.getType().equals(DocumentConstants.DOCUMENT_TYPE_ARCHIVE)) {
					// 归档文件
					String[] docIdArr = new String[1];
					docIdArr[0] = docId.toString();
                    Integer userId = UserInfoUtil.getUserInfoVO().getUser().getUserId();
                    List<Long> groupIds = UserInfoUtil.getGroupIds();
                    Department department = UserInfoUtil.getUserInfoVO().getDepartment();

                    String recentPermission = this.documentPermissionMapper2.getRecentPermission(docIdArr, userId.longValue(), groupIds, department == null ? null : department.getId().longValue());
                    if(recentPermission == null || recentPermission.compareTo(DocumentConstants.DOCUMENT_PERMISSION_READANDWRITE) < 0) {
                        result.setCode(Messages.API_AUTHEXCEPTION_CODE);
                        result.setMsg(Messages.API_AUTHEXCEPTION_MSG);
                        return result;
                    }
                }
			}
		}*/

		try {
			logger.info("批量删除开始！");

			logger.info("---文档分类开始");
			// 获取docIds中所有文件与文件夹，包括所有子文件与子文件夹，用于更新document表
			List<Long> allDocIdList = new ArrayList<>();
			// 获取所有待删的版本文件id，用于更新document_version表
			List<Long> allVersionId = new ArrayList<>();

			for (Long docId : docIds) {
				Document document = documentMapper.selectByPrimaryKey(docId);
				// 获取doc是文件还是文件夹
				String docType = document.getDocumentType();
				if (!Constants.DOCUMENT_FOLDER_TYPE.equals(docType)) {
					allDocIdList.add(docId);
					List<Long> versionIdList = documentVersionMapper2.getAllNotDelVersionIdByDocId(docId);
					allVersionId.addAll(versionIdList);
				} else {
					// 获取一个文件夹内部所有尚未删除的文档的idList
					List<Long> docIdListInThisFolder = documentMapper2
							.getAllNotDeleteChildDocIdByFolderId(String.valueOf(docId));
					allDocIdList.addAll(docIdListInThisFolder);
					if (!docIdListInThisFolder.isEmpty()) {
						// 获取该文件夹中所有文件的所有尚未删除的版本id
						List<Long> versionIdList = documentVersionMapper2
								.getAllNotDelVersionIdByDocIds(docIdListInThisFolder);
						allVersionId.addAll(versionIdList);
					}
				}
			}
			logger.info("------文档分类完成");

			// if (!allDocIdList.isEmpty()) {
			// for (Long id : allDocIdList) {
			// Document document = documentMapper.selectByPrimaryKey(id);
			// Long documentVersionId = document.getDocumentVersionId();
			// if (null != documentVersionId) {
			// allVersionId.add(id);
			// }
			// }
			// } // versionDocIdList 获取结束
			// logger.info("------获取所有版本文档结束");

			logger.info("document表操作开始");
			int num1 = documentMapper2.delDocByBatch(allDocIdList, updateUserId, updateUsename);
			logger.info("document表操作结束，数目：" + num1);

			logger.info("recycle表操作开始");
			List<Recycle> recycleList = getRecycleAddList(docIds, updateUserId, incId);
			int num3 = recycleMapper2.addRecycleByBatch(recycleList);
			logger.info("recycle表操作完成，数目：" + num3);

			if (!allVersionId.isEmpty()) {
				logger.info("document_version表操作开始");
				int num2 = documentVersionMapper2.delDocVersionByIds(allVersionId, updateUserId);
				logger.info("document_version表操作结束，数目：" + num2);
			}

			logger.info("doc移入回收站操作完成！");

			// 记录操作日志
			for(Long id: docIds) {
				Document document = this.documentMapper.selectByPrimaryKey(id);
				this.addDocumentLog(request, document.getId().toString(), ActionType.DELETE, document.getDocumentName(), document.getUpdateTime());
			}

//			List<String> docNames = recycleMapper2.getDocNames(docIds);
//			result.setData(docNames);
		} catch (Exception e) {
			logger.error("delete doc error", e);
			result.setCode(Messages.API_ERROR_CODE);
			result.setMsg(Messages.API_ERROR_MSG);
			result.setData(null);
			return result;
		}
		return result;
	}

	@Override
	public boolean hasParentDocDeleted(List<Long> docIds) {
		List<String> isDeleteList = documentMapper2.getParentDocIsDeleteByDocIds(docIds);
		return isDeleteList.contains("1");
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

	/**
	 * 删除文件的一个版本，会直接彻底删除该版本
	 *
	 * @author jiadao
	 * @date 2017-12-19
	 * @param docVersionIdStr
	 *            文档版本id
	 * @param request
	 * @param userId
	 *            操作人
	 * @return
	 */
	@Override
	public ResultCode<String> delDocumentVersion(HttpServletRequest request, Long incId, Long userId, String docVersionIdStr) {
		ResultCode<String> result = new ResultCode<String>();

		try {
			if (StringUtils.isNotBlank(docVersionIdStr)) {

				logger.info("开始删除版本 id==" + docVersionIdStr);
				Long docVersionId = Long.valueOf(docVersionIdStr);
				logger.info("开始修改doc_version表中的字段！");
				int data1 = documentVersionMapper2.delDocVersionById(docVersionId, userId);
				logger.info("doc_version表操作完毕！num == " + data1);

				// 将该docVersionId放入document_ceph_delete表中
				List<Long> list = new ArrayList<>();
				list.add(docVersionId);
				logger.info("开始添加 " + docVersionId + " 进doc_ceph_delete表中!");
				// List<Long> docVersionIds =
				// getAllDocVersionIdsInRecycle(list);
				addDelDocsToCephDelete(userId, incId, list);
				logger.info("添加完成！");
				logger.info("版本 id==" + docVersionIdStr + " 删除结束！");
				// 组装日志详情：文件名+版本+版本号
				DocumentVersion documentVersion = documentVersionMapper.selectByPrimaryKey(docVersionId);
				int version = documentVersion.getVersion();
//				String name = documentVersionMapper2.getFileNameByVersionId(docVersionId);
//				String logDetail = name + " 版本 " + version;
//				result.setData(logDetail);

				// 记录操作日志
				Long documentId = documentVersion.getDocumentId();
				Document document = this.documentMapper.selectByPrimaryKey(documentId);
				this.addDocumentLog(request, document.getId().toString(), ActionType.DELETE_DOC_VERSION, document.getDocumentName() + " 版本 " + version, new Date());

			} else {
				result.setCode(Messages.MISSING_INPUT_CODE);
				result.setMsg(Messages.API_ERROR_MSG);
				result.setData(null);
			}
		} catch (Exception e) {
			logger.error("delete doc version error", e);
			result.setCode(Messages.API_ERROR_CODE);
			result.setMsg(Messages.API_ERROR_MSG);
			return result;
		}
		return result;
	}

	@Override
	public List<RecycleFileVO> getDocListInRecycle(Long incId, Long userId, String filter, String folderId) {
		// List<Document> list = new ArrayList<>();
		List<Long> docIds = recycleMapper2.getVisibleDocIdList(incId, userId);

		if (docIds.isEmpty() || null == docIds) {
			return null;
		}
		if (StringUtils.isNotBlank(folderId)) {
			return this.getDocListInFolderInRecycleBy(Long.valueOf(folderId), filter,docIds);
		}
		if (StringUtils.isNotBlank(filter)){
			List<RecycleFileVO> lRecycleFileVOs = new ArrayList<>();
			ArrayList<Document> documentArrayList = new ArrayList<>();
			ArrayList<Document> returnList = new ArrayList<>();
			HashMap<Long, Integer> map = new HashMap<>();
			for (Long id :
					docIds) {
				DocumentExample documentExample = new DocumentExample();
				Criteria criteria = documentExample.createCriteria();
				criteria.andDocumentNameLike("%" + filter + "%")
						.andIdPathLike("%" + id + "%");
				documentExample.setOrderByClause("document_type asc,update_time desc");
				List<Document> documents = documentMapper.selectByExample(documentExample);
				if (CollectionUtils.isNotEmpty(documents)){
					for (Document d :
							documents) {
						if (map.get(d.getId())!=null){
							map.put(d.getId(),map.get(d.getId())+1);
						}else {
							map.put(d.getId(),1);
						}
					}
				}
				documentArrayList.addAll(documents);
			}
			if (CollectionUtils.isNotEmpty(documentArrayList)){
				for (Document doc :
						documentArrayList) {
					if (map.keySet().contains(doc.getId())){
						returnList.add(doc);
						map.remove(doc.getId());
					}
				}
			}
			for (Document document : returnList) {
				RecycleFileVO recycleFileVO = new RecycleFileVO();
				recycleFileVO.setId(document.getId());
				recycleFileVO.setDocumentName(document.getDocumentName());
				recycleFileVO.setParentId(document.getParentId());
				if (null == document.getSize()) {
					recycleFileVO.setSize(0);
				} else {
					recycleFileVO.setSize(document.getSize());
				}
				recycleFileVO.setDocumentType(document.getDocumentType());
				recycleFileVO.setDeleteTime(DateUtil.formatDateTime(document.getUpdateTime()));
				recycleFileVO.setDeleteTimeStamp(document.getUpdateTime());
				recycleFileVO.setRemainTime(
						DateUtil.daysBetween(new Date(), DateUtil.getDatePlusDays(document.getUpdateTime(), 29)));
				lRecycleFileVOs.add(recycleFileVO);
			}
			return lRecycleFileVOs;
		}else {
			List<RecycleFileVO> lRecycleFileVOs = recycleMapper2.getVisibleDocList(incId, userId, filter);
			return lRecycleFileVOs;
		}
	}

	@Override
	public List<RecycleFileVO> getDocListInFolderInRecycleBy(Long folderId, String filter,List<Long> docIds) {
		List<RecycleFileVO> lRecycleFileVOs = new ArrayList<>();
		List<Document> list=new ArrayList<>();
		if (StringUtils.isNotBlank(filter)){
			String[] split1 = documentMapper.selectByPrimaryKey(folderId).getIdPath().split("/");
			Long parentIdInRecycleByFoler=null;
			Long parentIdInRecycleByChild=null;
			for (String s :
					split1) {
				if (StringUtils.isNotBlank(s) && docIds.contains(Long.parseLong(s))){
					parentIdInRecycleByFoler=Long.parseLong(s);
				}
			}
			DocumentExample documentExample = new DocumentExample();
			Criteria criteria = documentExample.createCriteria();
			criteria.andDocumentNameLike("%" + filter + "%")
					.andIdPathLike("%" + folderId + "%")
					.andIdNotEqualTo(folderId);
			documentExample.setOrderByClause("document_type asc,update_time desc");
			List<Document> documents = documentMapper.selectByExample(documentExample);
			if (CollectionUtils.isNotEmpty(documents)){
				for (Document d :
						documents) {
					String[] split = d.getIdPath().split("/");
					for (String s :
							split) {
						if (StringUtils.isNotBlank(s) && docIds.contains(Long.parseLong(s))){
							parentIdInRecycleByChild=Long.parseLong(s);
						}
					}
					if (parentIdInRecycleByChild.equals(parentIdInRecycleByFoler)){
						list.add(d);
					}
				}
			}
		}else {
			// 在doc表中查找的某docId下的一级子doc
			List<Long> childDocIdInDoc = documentMapper2.getDocIdListByDocParentIdInDocument(folderId);
			// 在recycle表中查找存在的某docId下的一级子doc
			List<Long> childDocIdInRecycle = recycleMapper2.getDocIdListByDocParentIdInRecycle(folderId);
			// 取差集
			childDocIdInDoc.removeAll(childDocIdInRecycle);

			if (childDocIdInDoc.isEmpty() || null == childDocIdInDoc) {
				return null;
			}

			DocumentExample example = new DocumentExample();
			Criteria criteria = example.createCriteria();
			criteria.andIdIn(childDocIdInDoc);
			if (StringUtils.isNotBlank(filter)) {
				criteria.andDocumentNameLike("%" + filter + "%");
			}
			example.setOrderByClause("document_type asc,update_time desc");
			list = documentMapper.selectByExample(example);
		}
		for (Document document : list) {
			RecycleFileVO recycleFileVO = new RecycleFileVO();
			recycleFileVO.setId(document.getId());
			recycleFileVO.setDocumentName(document.getDocumentName());
			recycleFileVO.setParentId(folderId);
			if (null == document.getSize()) {
				recycleFileVO.setSize(0);
			} else {
				recycleFileVO.setSize(document.getSize());
			}
			recycleFileVO.setDocumentType(document.getDocumentType());
			recycleFileVO.setDeleteTime(DateUtil.formatDateTime(document.getUpdateTime()));
			recycleFileVO.setDeleteTimeStamp(document.getUpdateTime());
			recycleFileVO.setRemainTime(
					DateUtil.daysBetween(new Date(), DateUtil.getDatePlusDays(document.getUpdateTime(), 29)));
			lRecycleFileVOs.add(recycleFileVO);
		}
		return lRecycleFileVOs;
	}

	@Override
	public List<RecycleFileVO> getDocListInFolderInRecycle(Long folderId, String filter) {
		List<RecycleFileVO> lRecycleFileVOs = new ArrayList<>();

		// 在doc表中查找的某docId下的一级子doc
		List<Long> childDocIdInDoc = documentMapper2.getDocIdListByDocParentIdInDocument(folderId);
		// 在recycle表中查找存在的某docId下的一级子doc
		List<Long> childDocIdInRecycle = recycleMapper2.getDocIdListByDocParentIdInRecycle(folderId);
		// 取差集
		childDocIdInDoc.removeAll(childDocIdInRecycle);

		if (childDocIdInDoc.isEmpty() || null == childDocIdInDoc) {
			return null;
		}

		DocumentExample example = new DocumentExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdIn(childDocIdInDoc);
		if (StringUtils.isNotBlank(filter)) {
			criteria.andDocumentNameLike("%" + filter + "%");
		}
		example.setOrderByClause("document_type asc,update_time desc");
		List<Document> list = documentMapper.selectByExample(example);
		for (Document document : list) {
			RecycleFileVO recycleFileVO = new RecycleFileVO();
			recycleFileVO.setId(document.getId());
			recycleFileVO.setDocumentName(document.getDocumentName());
			recycleFileVO.setParentId(folderId);
			if (null == document.getSize()) {
				recycleFileVO.setSize(0);
			} else {
				recycleFileVO.setSize(document.getSize());
			}
			recycleFileVO.setDocumentType(document.getDocumentType());
			recycleFileVO.setDeleteTime(DateUtil.formatDateTime(document.getUpdateTime()));
			recycleFileVO.setDeleteTimeStamp(document.getUpdateTime());
			recycleFileVO.setRemainTime(
					DateUtil.daysBetween(new Date(), DateUtil.getDatePlusDays(document.getUpdateTime(), 29)));
			lRecycleFileVOs.add(recycleFileVO);
		}
		return lRecycleFileVOs;
	}

	@Override
	public ResultCode<List<String>> removeDocCompletelyBatch(HttpServletRequest request, Long incId, Long userId, String docIdsStr) {
		ResultCode<List<String>> result = new ResultCode<List<String>>();

		try {
			List<Long> docIds = StringUtils2.convertStringParamToList(docIdsStr);
			result = removeDocCompletelyBatch(incId, userId, docIds);

			// 记录操作日志
			for(Long id: docIds) {
				Document document = this.documentMapper.selectByPrimaryKey(id);
				this.addDocumentLog(request, document.getId().toString(), ActionType.DELETE_FROM_RECYCLE, document.getDocumentName(), document.getUpdateTime());
			}

		} catch (Exception e) {
			logger.error("delete doc completely error", e);
			result.setCode(Messages.API_ERROR_CODE);
			result.setMsg(Messages.API_ERROR_MSG);
			return result;
		}
		return result;
	}

	private ResultCode<List<String>> removeDocCompletelyBatch(Long incId, Long userId, List<Long> docIds) {
		ResultCode<List<String>> result = new ResultCode<List<String>>();

		try {
			if (docIds.isEmpty()) {
				result.setCode(Messages.MISSING_INPUT_CODE);
				result.setMsg(Messages.MISSING_INPUT_MSG);
				result.setData(null);
				return result;
			}
			logger.info("开始彻底删除");
			logger.info("recyle表操作开始！！");
			int data1 = recycleMapper2.removeDocCompletely(docIds);
			logger.info("recycle表操作完毕！！" + data1);

			// 获取所有的docVersionId,添加到doc_ceph_del表中用以彻底删除
			List<Long> docVersionIds = getAllDocVersionIdsInRecycle(docIds);
			logger.info("doc_ceph_del表操作开始！");
			addDelDocsToCephDelete(userId, incId, docVersionIds);
			logger.info("doc_ceph_del表操作结束！");
			logger.info("彻底删除完毕！");
			// 文档名，日志用，
			List<String> docNames = recycleMapper2.getDocNames(docIds);
			result.setData(docNames);

		} catch (Exception e) {
			logger.error("delete doc completely error", e);
			result.setCode(Messages.API_ERROR_CODE);
			result.setMsg(Messages.API_ERROR_MSG);
			return result;
		}
		return result;
	}

	@Override
	public ResultCode<List<String>> recoverDocBatch(HttpServletRequest request, String docIdsStr, long updateUser) {
		ResultCode<List<String>> result = new ResultCode<List<String>>();

		try {
			// 所有需要在doc表中操作的文档id集合
			List<Long> docIdListInDoc = new ArrayList<>();
			// 所有需要在doc_version表中操作的文档id集合
			List<Long> docIdListInDocVersion = new ArrayList<>();

			List<Long> docIdList = StringUtils2.convertStringParamToList(docIdsStr);

			if (docIdList.isEmpty()) {
				result.setCode(Messages.MISSING_INPUT_CODE);
				result.setMsg(Messages.MISSING_INPUT_MSG);
				result.setData(null);
				return result;
			}

			// 判断是否含有父目录也被删除的情况
			if (hasParentDocDeleted(docIdList)) {
				result.setCode(Messages.HAS_PARENT_DOC_DELETED_CODE);
				result.setMsg(Messages.HAS_PARENT_DOC_DELETED_MSG);
				result.setData(null);
				return result;
			}

			// 所有需要在doc表中操作的文档id集合
			for (Long docId : docIdList) {
				Document document = documentMapper.selectByPrimaryKey(docId);
				if (Constants.DOCUMENT_FOLDER_TYPE.equals(document.getDocumentType())) {
					// document表中获取某文件夹下所有的文档
					List<Long> docIdInDoc = documentMapper2.getAllChildDocIdByFolderId(String.valueOf(docId));
					// 获取存在在recycle表中某文件夹下的文档
					List<Long> docIdInRecycle = recycleMapper2
							.getAllChildDocIdByDocParentIdInRecycle(String.valueOf(docId));
					docIdInDoc.removeAll(docIdInRecycle);
					docIdListInDoc.addAll(docIdInDoc);
				}
				docIdListInDoc.add(docId);
			} // doc表中操作准备完毕
			// 所有需要在version表中操作的文档id集合
			if (!docIdListInDoc.isEmpty()) {
				for (Long id : docIdListInDoc) {
					Document document = documentMapper.selectByPrimaryKey(id);
					Long documentVersionId = document.getDocumentVersionId();
					if (null != documentVersionId) {
						docIdListInDocVersion.add(id);
					}
				}
			} // version表中操作准备完毕
			logger.info("开始恢复文档 ：" + docIdsStr);
			// 还原操作

			if (!docIdListInDoc.isEmpty()) {
				logger.info("还原---doc表操作开始--");

				// 处理同名问题：
				// 1. 如果原目录下没有未删除的同名文件，直接还原;
				// 2. 否则，如果仅存在同名文件，不存在类似“filename(n).suffix”的文件（其中n为递增的数字），则重命名为filename(1).suffix;
				// 3. 否则，重命名为filename(n+1).suffix
				//
				// 注：对文件夹和没有后缀名的文件，忽略“.suffix”
				Set set = new HashSet();
				set.addAll(docIdListInDoc);
				ArrayList<Long> docIdListInDoc2 = new ArrayList<>();
				docIdListInDoc2.addAll(set);
				for(Long docId : docIdListInDoc2) {
					//判断同目录下是否有未删除的同名文档
					Document document = documentMapper.selectByPrimaryKey(docId);
					String documentName = document.getDocumentName();

					DocumentExample example = new DocumentExample();
					if (document.getType().equals("1")){
						example.createCriteria()
								.andIsDeleteEqualTo(DocumentConstants.DOCUMENT_IS_DELETE_NO)
								.andParentIdEqualTo(document.getParentId())
								.andDocumentNameEqualTo(documentName)
								.andTypeEqualTo(document.getType())
								.andCreateUserEqualTo(updateUser);
					}else {
						example.createCriteria()
								.andIsDeleteEqualTo(DocumentConstants.DOCUMENT_IS_DELETE_NO)
								.andParentIdEqualTo(document.getParentId())
								.andDocumentNameEqualTo(documentName)
								.andTypeEqualTo(document.getType());
					}


					List<Document> sameNameDocuments = documentMapper.selectByExample(example);
					if (CollectionUtils.isNotEmpty(sameNameDocuments)) {
						String namePattern;
						String newName;
						int newNum = 1;
						Long parentId = document.getParentId();
						String baseName = FilenameUtils.getBaseName(documentName);
						String suffix = FilenameUtils.getExtension(documentName);
						if (document.getDocumentType().equals(DocumentConstants.DOCUMENT_DOCUMENT_TYPE_DIR)) {
							//文件夹
							namePattern = "^"+documentName + "\\([0-9]+\\)";
						} else {
							//文件
							if(suffix.isEmpty()) {
								namePattern = "^"+baseName + "\\([0-9]+\\)";
							} else {
								namePattern = "^"+baseName + "\\([0-9]+\\)\\." + suffix;
							}
						}
						List<Document> samePatternDocuments = documentMapper2.getSamePatternDocuments(parentId, namePattern);



						//判断是否有类似“filename(n).suffix”的文件
						if (CollectionUtils.isNotEmpty(samePatternDocuments)) {
							List<Integer> numList = new ArrayList<>(samePatternDocuments.size());
							for (Document samePatternDocument : samePatternDocuments) {
								String name = FilenameUtils.getBaseName(samePatternDocument.getDocumentName());
								int num = Integer.parseInt(name.substring(name.lastIndexOf("(") + 1, name.length() - 1));

								numList.add(num);
							}
							Collections.sort(numList);
							newNum = numList.get(numList.size() - 1) + 1;
						}
						if (document.getDocumentType().equals(DocumentConstants.DOCUMENT_DOCUMENT_TYPE_DIR)) {
							//文件夹
							newName = documentName + "(" + newNum + ")";
						} else {
							//文件
							if(suffix.isEmpty()) {
								newName = baseName + "(" + newNum + ")";
							} else {
								newName = baseName + "(" + newNum + ")." + suffix;
							}
						}

						document.setDocumentName(newName);
					}

					document.setUpdateUser(updateUser);
					document.setUpdateTime(new Date());
					document.setIsDelete(DocumentConstants.IS_DELETE_NO);

					documentMapper.updateByPrimaryKey(document);

				}
				logger.info("还原---doc表操作结束--" + docIdList.size());

//				int data1 = documentMapper2.recoverDocByDocIds(updateUser, docIdListInDoc);
//				logger.info("还原---doc表操作结束--" + data1);
			}

			if (!docIdListInDocVersion.isEmpty()) {
				logger.info("还原---docversion表操作开始--");
				int data2 = documentVersionMapper2.recoverDocVersionByDocId(docIdListInDocVersion, updateUser);
				logger.info("还原---docversion表操作结束--" + data2);
			}

			logger.info("还原--recycle表操作开始--");
			RecycleExample example = new RecycleExample();
			example.createCriteria().andDocumentIdIn(docIdList);
			int data3 = recycleMapper.deleteByExample(example);
			logger.info("还原--recycle表操作结束--" + data3);
			logger.info("恢复完毕！");

			// 文档名，日志用，
//			List<String> docNames = recycleMapper2.getDocNames(docIdList);
//			result.setData(docNames);
			// 记录操作日志
			for(Long id: docIdList) {
				Document document = this.documentMapper.selectByPrimaryKey(id);
				this.addDocumentLog(request, document.getId().toString(), ActionType.RESTORE, document.getDocumentName(), document.getUpdateTime());
			}


		} catch (Exception e) {
			logger.error("doc recover error", e);
			result.setCode(Messages.API_ERROR_CODE);
			result.setMsg(Messages.API_ERROR_MSG);
			return result;
		}
		return result;
	}

	@Override
	public void addDelDocsToCephDelete(Long userId, Long incId, List<Long> docVersionIds) {
		if (null != docVersionIds && !docVersionIds.isEmpty()) {
			// 剔除已经存在在doc_ceph_delete表中的doc_version_id
			List<Long> exist = documentCephDeleteMapper2.getExistDocVersionIdsByDocVersionIds(docVersionIds);
			docVersionIds.removeAll(exist);

			for (Long docVersionId : docVersionIds) {
				if (null != docVersionId) {
					DocumentCephDelete dCephDelete = new DocumentCephDelete();
					dCephDelete.setIncId(incId);
					dCephDelete.setCreateUser(userId);
					dCephDelete.setCreateTime(new Date());
					dCephDelete.setDocumentVersionId(docVersionId);
					dCephDelete.setIsDelete("0");

					fullTextService.deleteDocumentIndex(Global.getConfig("index.prefixion")+ String.valueOf(incId),String.valueOf(docVersionId));
					documentCephDeleteMapper.insertSelective(dCephDelete);
				}
			}
		}
	}

	@Override
	public List<Long> getAllDocVersionIdsInRecycle(List<Long> docIds) {
		List<Long> allDocVersionId = new ArrayList<>();

		for (Long docId : docIds) {
			Document document = documentMapper.selectByPrimaryKey(docId);
			String docType = document.getDocumentType();
			if (!Constants.DOCUMENT_FOLDER_TYPE.equals(docType)) {
				List<Long> docVersionIds = recycleMapper2.getVersionIdFromDocId(docId);
				List<Long> exist = documentCephDeleteMapper2.getExistDocVersionIdsByDocVersionIds(docVersionIds);
				docVersionIds.removeAll(exist);
				allDocVersionId.addAll(docVersionIds);
			} else {
				List<Long> docVersionIds = new ArrayList<>();
				// document表中获取某文件夹下所有的文档
				List<Long> docIdInDoc = documentMapper2.getAllChildDocIdByFolderId(String.valueOf(docId));
				// 获取存在在recycle表中某文件夹下的文档
				List<Long> docIdInRecycle = recycleMapper2
						.getAllChildDocIdByDocParentIdInRecycle(String.valueOf(docId));
				docIdInDoc.removeAll(docIdInRecycle);
				if (null != docIdInDoc && !docIdInDoc.isEmpty()) {
					docVersionIds = documentVersionMapper2.getAllDocVersionIdByDocIds(docIdInDoc);
					List<Long> exist = documentCephDeleteMapper2.getExistDocVersionIdsByDocIds(docIdInDoc);
					docVersionIds.removeAll(exist);
				}
				allDocVersionId.addAll(docVersionIds);
			}
		}
		return allDocVersionId;
	}

	@Override
	public List<Long> getUnMarkedId(int count) {
		return documentCephDeleteMapper2.getUnMarkedId(count);
	}

	@Override
	public ResultCode<List<String>> autoDelete() {
		ResultCode<List<String>> result = new ResultCode<>();

		// 获取回收站中已经保存了30天的记录，包括所有的，区分企业，不区分用户
		Map<Long, List<Long>> map = makeRecycleMap();

		if (null != map && !map.isEmpty()) {
			for (Map.Entry<Long, List<Long>> entry : map.entrySet()) {
				long incId = entry.getKey();
				List<Long> docIds = entry.getValue();
				if (!docIds.isEmpty()) {
					// 开始彻底删除逻辑
					logger.info("开始进入彻底删除逻辑！incId = " + incId + ",  docIds = " + docIds);
					result = removeDocCompletelyBatch(incId, 0L, docIds);
					logger.info("已放入doc_ceph_delete表：" + result.getData());
				}
			}
		}
		return result;
	}

	@Override
	public void getDeletingDocVersion(int count) {
		Map<Long, List<DocumentVersion>> map = new HashMap<>();
		AmazonS3 connection = null;
		String host = "";
		String accessKey = "";
		String secrectKey = "";

		List<Long> unMarked = documentCephDeleteMapper2.getUnMarkedId(count);
		logger.info("ceph_delete表获取" + count + "条未删除数据！doc_ceph_delete ids ==" + unMarked);

		if (!unMarked.isEmpty()) {
			documentCephDeleteMapper2.markDocVersion(unMarked);
			logger.info("ceph_delete表添加删除标记!  doc_ceph_delete ids ==" + unMarked);

			map = makeCephDeleteMap(unMarked);

			if (null != map && !map.isEmpty()) {
				for (Entry<Long, List<DocumentVersion>> entry : map.entrySet()) {

					// 获取企业ceph配置,并获取ceph连接
					long incId = entry.getKey();
					IncConfigExample example = new IncConfigExample();
					example.createCriteria().andIncIdEqualTo(incId);
					List<IncConfig> incConfigs = incConfigMapper.selectByExample(example);
					if (!incConfigs.isEmpty()) {
						IncConfig incConfig = incConfigs.get(0);
						host = incConfig.getCephUrl();
						accessKey = incConfig.getCephAccessKey();
						secrectKey = incConfig.getCephSecretKey();
						logger.info("获取企业ceph配置：incId ==" + incId + ", host ==" + host + ", accessKey==" + accessKey
								+ ", secrectKey==" + secrectKey);
						connection = connectCeph(host, accessKey, secrectKey);
					}

					// 获取该企业下待删的 doc_version，并通过ceph删除
					List<DocumentVersion> documentVersions = entry.getValue();
					logger.info("获取该企业下待删的 doc_version ： incId==" + incId + ", documentVersions==" + documentVersions);
					if (!documentVersions.isEmpty()) {
						for (DocumentVersion documentVersion : documentVersions) {
							long docVersionId = documentVersion.getId();
							String bucket = documentVersion.getCephBucket();
							String key = documentVersion.getCephBucketKey();
							logger.info("删除ES Document" + docVersionId);
							fullTextService.deleteDocumentIndex(Global.getConfig("index.prefixion")+ String.valueOf(incId),String.valueOf(docVersionId));

							logger.info("下发删除指令：" + "文件版本id ==" + docVersionId + " ==> bucket名称--" + bucket + "，存储名称--"
									+ key);
							S3Utils.delObject(bucket, key, connection);
							logger.info(bucket + "--" + key + "删除指令已发出！");
						}
						logger.info("全部删除指令已发出，ceph集群会在大约两小时后开启删除线程执行删除！");
						logger.info("可登录ceph rgw节点 " + host
								+ "，通过命令 ：radosgw-admin gc list --cluster ceph --include-all  查看gc列表");
					}
				}
			}
		}
	}

	/**
	 * 包装recycle表中 incId及旗下的docId
	 *
	 * @return key: incId value: 该incId下的docId列表
	 */
	private Map<Long, List<Long>> makeRecycleMap() {
		Map<Long, List<Long>> map = new HashMap<>();
		List<Long> incIds = recycleMapper2.getDeletingIncId();
		if (null != incIds && !incIds.isEmpty()) {
			for (Long incId : (List<Long>) removeDuplicate(incIds)) {
				List<Long> docIds = recycleMapper2.getDeletingDocIdByIncId(incId);
				map.put(incId, docIds);
			}
		}
		return map;
	}

	/**
	 * 包装doc_ceph_delete表中 incId及旗下的docVersionId
	 *
	 * @return key: incId value: 该incId下的docVersionId列表
	 */
	private Map<Long, List<DocumentVersion>> makeCephDeleteMap(List<Long> ids) {
		Map<Long, List<DocumentVersion>> map = new HashMap<>();
		List<Long> incIds = documentCephDeleteMapper2.getDeletingIncId(ids);
		if (null != incIds && !incIds.isEmpty()) {
			for (Long incId : (List<Long>) removeDuplicate(incIds)) {
				List<DocumentVersion> documentVersions = documentCephDeleteMapper2.getDeletingDocVersionByIncId(incId,
						ids);
				map.put(incId, documentVersions);
			}
		}
		return map;
	}

	/**
	 * 去重复
	 */
	private List removeDuplicate(List list) {
		HashSet h = new HashSet(list);
		list.clear();
		list.addAll(h);
		return list;
	}

	/**
	 * 连接ceph集群
	 */
	private AmazonS3 connectCeph(String host, String accessKey, String secrectKey) {
		AmazonS3 connection = null;
		try {
			logger.info(
					"正在获取ceph连接 !  ceph host==" + host + "  accessKey==" + accessKey + "  secrectKey==" + secrectKey);
			connection = S3Utils.getConnect(accessKey, secrectKey, host);
			logger.info("ceph已连接，ceph connection == " + connection.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

}
