package com.quarkdata.yunpan.api.service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author luohl
 * 收藏
 */
public interface CollectionService extends BaseLogService {
	
	/**
	 * 加入收藏
	 * @param request
	 * @param incId
	 * @param userId
	 * @param documentId
	 */
	void addCollection(HttpServletRequest request, Long incId, Long userId, Long documentId);
	
	/**
	 * 取消收藏
	 * @param request
	 * @param incId
	 * @param userId
	 * @param documentIds
	 */
	void deleteCollection(HttpServletRequest request, Long incId, Long userId, String documentIds);
	
	/**
	 * 获取收藏列表,附标签
	 * @param userId
	 * @return
	 */
	List<Map<String,Object>> getCollectionList(Long userId, Long incId, Long parentId, String documentName, Integer pageNum, Integer pageSize, List<Long>userGroupIds, Long deptId);
}
