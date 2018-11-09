package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.vo.CollectionVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CollectDocumentRelMapper2 {
	
	/**
	 * 获取文档名称
	 * @param documentId
	 * @return
	 */
	String getDocumentName(@Param("documentId") Long documentId);
	
	/**
	 * 获取个人收藏列表,附标签信息
	 * @param incId
	 * @param userId
	 * @return
	 */
	List<CollectionVO> getCollectionList(@Param("userId") Long userId,
										 @Param("incId") Long incId,
										 @Param("parentId") Long parentId,
										 @Param("documentName") String documentName,
										 @Param("startNum") Integer startNum,
										 @Param("pageSize") Integer pageSize);

	/**
	 * 获取文档类型
	 * @param documentId
	 * @return
	 */
	String getDocumentType(@Param("documentId") Long documentId);
	
	/**
	 * 判断文件是否与我共享
	 * @param documentId
	 * @param receiverId
	 * @return
	 */
	int isShared(@Param("documentId") Long documentId, @Param("receiverId") Long receiverId);
	
	/**
	 * 获取文档权限
	 * @param documentId
	 * @param receiverId
	 * @return
	 */
	List<String> getPermissionType(@Param("documentId") Long documentId, @Param("receiverId") Long receiverId);
}
