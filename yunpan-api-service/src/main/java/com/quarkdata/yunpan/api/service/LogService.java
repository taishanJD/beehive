package com.quarkdata.yunpan.api.service;

import com.quarkdata.yunpan.api.model.common.ListResult;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.Log;
import com.quarkdata.yunpan.api.model.dataobj.LogDocumentRel;
import com.quarkdata.yunpan.api.model.vo.LogAddVO;
import com.quarkdata.yunpan.api.model.vo.LogQueryVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author yanyq1129@thundersoft.com
 * @Description 日志相关service
 */
public interface LogService extends BaseLogService {

	/**
	 * 保存日志到数据库
	 * 
	 * @param log
	 */
	void add(Log log);

	/**
	 * 日志信息综合分页查询
	 *
	 * @param incId
	 * @param pageNum
	 * @param pageSize
	 * @param logQueryVO
	 */
	ResultCode<ListResult<Log>> getLogList(Integer incId, Integer pageNum, Integer pageSize, LogQueryVO logQueryVO);

	/**
	 * 保存文档操作日志的关系记录
	 * @param logDocumentRel
	 */
    void addLogDocumentRel(LogDocumentRel logDocumentRel);

	/**
	 * 导出日志报表
	 *
	 * @param request
	 * @param incId
	 * @param logQueryVO
	 * @return
	 */
	ResultCode<ListResult<Log>> exportLog(HttpServletRequest request, Integer incId, LogQueryVO logQueryVO);

	/**
	 * 预览文档不需要下载时记录预览日志
	 * @param request
	 * @param documentVersionId
	 */
	void addPreviewLogByDocumentVersionId(HttpServletRequest request, String documentVersionId);

	/**
	 * 预览外链文档不需要下载时记录预览日志
	 * @param request
	 * @param documentIds_list
	 */
	void addPreviewLogByDocumentId(HttpServletRequest request, List<String> documentIds_list);

	/**
	 * app添加预览日志
	 * @param incId
	 * @param userId
	 * @param logAddVOs
	 */
    void addPreviewLogs(HttpServletRequest request, Integer incId, Integer userId, List<Long> groupIds, Long deptId, List<LogAddVO> logAddVOs);
}
