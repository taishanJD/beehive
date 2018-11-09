package com.quarkdata.yunpan.api.service;

import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.DocumentVersion;
import com.quarkdata.yunpan.api.model.vo.DocumentVersionVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface DocumentVersionService extends BaseLogService {

	/**
	 * 获取文档版本
	 * @param documentId
	 * @param incId
	 * @return
	 */
	ResultCode<List<DocumentVersionVO>> getDocumentVersions(Long documentId,Long incId);

	/**
	 * 还原到历史版本
	 *
	 * @param request
	 * @param documentId
	 * @param toVersionId
	 * @return
	 */
	ResultCode<String> resetVersion(HttpServletRequest request, Long documentId, Long toVersionId, Long userId, String userName);

	/**
	 * 根据ID获取文档版本对象
	 * @param id
	 * @return
	 */
	DocumentVersion getDocumentVersionByVersionId(long id);
}
