package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.yunpan.api.dal.dao.DocumentShareMapper2;
import com.quarkdata.yunpan.api.dal.dao.LogDocumentRelMapper;
import com.quarkdata.yunpan.api.dal.dao.LogMapper;
import com.quarkdata.yunpan.api.dal.dao.LogMapper2;
import com.quarkdata.yunpan.api.model.common.ActionType;
import com.quarkdata.yunpan.api.model.common.DocumentConstants;
import com.quarkdata.yunpan.api.model.common.ListResult;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.dataobj.Log;
import com.quarkdata.yunpan.api.model.dataobj.LogDocumentRel;
import com.quarkdata.yunpan.api.model.vo.LogAddVO;
import com.quarkdata.yunpan.api.model.vo.LogQueryVO;
import com.quarkdata.yunpan.api.service.DocumentPermissionService;
import com.quarkdata.yunpan.api.service.DocumentService;
import com.quarkdata.yunpan.api.service.LogService;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Transactional(rollbackFor = Exception.class)
@Service
public class LogServiceImpl extends BaseLogServiceImpl implements LogService {

	@Autowired
	private DocumentService documentService;

	@Autowired
	private DocumentPermissionService documentPermissionService;

	@Autowired
	private DocumentShareMapper2 documentShareMapper2;

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

	@Autowired
	private LogMapper2 logMapper2;

	@Override
	public void add(Log log) {
		logMapper.insert(log);
	}

	@Override
	public ResultCode<ListResult<Log>> getLogList(Integer incId, Integer pageNum, Integer pageSize, LogQueryVO logQueryVO) {
		ResultCode<ListResult<Log>> resultCode = new ResultCode<>();
		ListResult<Log> result= new ListResult<>();
		Map<String , Object> params = new HashMap<>();
		params.put("incId", incId);
		params.put("day", logQueryVO.getDays());
		params.put("operator", logQueryVO.getOperator().trim());
		params.put("startTime", logQueryVO.getStartTime());
		params.put("endTime", logQueryVO.getEndTime() == null ? null : new DateTime(logQueryVO.getEndTime()).plusDays(1).minusMillis(1).toDate());
		if(pageNum != null && pageSize != null) {
			params.put("startNum", (pageNum - 1) * pageSize);
			params.put("endNum", pageSize);
		} else {
			params.put("startNum", null);
			params.put("endNum", null);
		}
		List<Log> logs = this.logMapper2.selectLogWithConditionsForPage(params);
		Integer total = this.logMapper2.countLogWithConditions(params);
		result.setListData(logs);
		result.setTotalNum(total);
		if(pageNum != null && pageSize != null) {
			result.setPageNum(pageNum);
			result.setPageSize(pageSize);
		}
		resultCode.setData(result);

		return resultCode;
	}

	@Override
	public void addLogDocumentRel(LogDocumentRel logDocumentRel) {
		this.logDocumentRelMapper.insert(logDocumentRel);
	}

	@Override
	public ResultCode<ListResult<Log>> exportLog(HttpServletRequest request, Integer incId, LogQueryVO logQueryVO) {
		ResultCode<ListResult<Log>> logList = this.getLogList(incId, null, null, logQueryVO);
		if(logList.getData().getListData() != null && logList.getData().getListData().size() > 0) {
			// 记录操作日志
			this.addOtherLog(request, ActionType.EXPORT, "导出日志报表", new Date());
		}
		return logList;
	}

	@Override
	public void addPreviewLogByDocumentVersionId(HttpServletRequest request, String documentVersionId) {
        Document document = this.documentService.getDocumentByVersion(Long.parseLong(documentVersionId));
        // 记录预览日志
        this.addDocumentLog(request, document.getId().toString(), ActionType.PREVIEW, document.getDocumentName(), new Date());
    }

    @Override
    public void addPreviewLogByDocumentId(HttpServletRequest request, List<String> documentIds_list) {
	    if(documentIds_list != null && documentIds_list.size() > 0) {
            for(String docId: documentIds_list) {
                Document document = this.documentService.getDocumentById(Long.parseLong(docId));
                // 记录预览日志
                this.addDocumentLog(request, document.getId().toString(), ActionType.PREVIEW, document.getDocumentName(), new Date());
            }
        }
    }

	@Override
	public void addPreviewLogs(HttpServletRequest request, Integer incId, Integer userId, List<Long> groupIds, Long deptId, List<LogAddVO> logAddVOs) {
		if(CollectionUtils.isNotEmpty(logAddVOs)) {
			List<LogAddVO> newLogAddVOs = new ArrayList<>();
			for(LogAddVO logAddVO: logAddVOs) {
				Document document = this.documentService.getDocumentById(logAddVO.getDocumentId());
				if(document != null && DocumentConstants.DOCUMENT_TYPE_PERSONAL.equals(document.getType())) {
					if(userId.toString().equals(document.getCreateUser().toString())) {
						// 个人文件
						this.addDocumentLog(request, logAddVO.getDocumentId().toString(), ActionType.PREVIEW, document.getDocumentName(), logAddVO.getActionTime());
					} else {
						// 判断是否与我共享
						Map<String, Object> params = new HashMap<>();
						params.put("incId", incId);
						params.put("userId", userId);
						params.put("groupIds", groupIds);
						params.put("deptId", deptId);
						params.put("docId", document.getId());
						Integer iShare = documentShareMapper2.isSharedToMe(params);
						if(iShare != null && !iShare.equals(0)) {
							this.addDocumentLog(request, logAddVO.getDocumentId().toString(), ActionType.PREVIEW, document.getDocumentName(), logAddVO.getActionTime());
						}
					}
				} else if(document != null) {
					// 判断权限
					String permission = this.documentPermissionService.getPermission(document.getIdPath(), Long.parseLong(userId.toString()), groupIds, deptId);
					if(permission != null) {
						this.addDocumentLog(request, logAddVO.getDocumentId().toString(), ActionType.PREVIEW, document.getDocumentName(), logAddVO.getActionTime());
					}
				}
			}
		}
	}

}
