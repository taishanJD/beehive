package com.quarkdata.yunpan.api.log;

import com.quarkdata.yunpan.api.model.common.ActionDetail;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author yanyq1129@thundersoft.com
 * @Description 保存用户操作日志工具类(非系统日志)
 *
 */
public class LogUtils {
	/**
	 * 单条操作
	 * 
	 * @param request
	 * @param actionType
	 * @param detail
	 */
	public static void addLog(HttpServletRequest request, Integer actionType, String detail) {
		request.setAttribute(LogParameters.actionType, actionType);
		ActionDetail actionDetail = new ActionDetail();
		actionDetail.setDetail(detail);
		request.setAttribute(LogParameters.actionDetail, actionDetail);
	}

	/**
	 * 批量操作
	 * 
	 * @param request
	 * @param actionType
	 * @param details
	 */
	public static void addLogs(HttpServletRequest request, Integer actionType, List<String> details) {
		request.setAttribute(LogParameters.actionType, actionType);
		ActionDetail actionDetail = new ActionDetail();
		actionDetail.setDetails(details);
		request.setAttribute(LogParameters.actionDetail, actionDetail);
	}

    /**
     * 添加文档相关操作日志
     * @param documentId
     * @param actionType
     * @param detail
     */
	public static void addDocumentLog(HttpServletRequest request, String documentId, String actionType, String detail) {
        request.setAttribute(LogParameters.documentId, documentId);
        request.setAttribute(LogParameters.actionType, actionType);
        ActionDetail actionDetail = new ActionDetail();
        actionDetail.setDetail(detail);
        request.setAttribute(LogParameters.actionDetail, actionDetail);
	}
}
