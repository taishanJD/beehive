package com.quarkdata.yunpan.api.dal.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.quarkdata.yunpan.api.model.dataobj.Recycle;
import com.quarkdata.yunpan.api.model.vo.RecycleFileVO;

public interface RecycleMapper2 {

	/**
	 * 批量增加recycle
	 * 
	 * @author jiadao
	 */
	int addRecycleByBatch(@Param("recycleList") List<Recycle> recycleList);

	/**
	 * 获取某文件夹存在在recycle中的子一级文档列表
	 * 
	 * @author jiadao
	 */
	List<Long> getDocIdListByDocParentIdInRecycle(@Param("folderId") Long folderId);

	/**
	 * 获取回收站中所有可见的文档id列表
	 * 
	 * @author jiadao
	 */
	List<Long> getVisibleDocIdList(@Param("incId") Long incId, @Param("userId") Long userId);

	/**
	 * 获取回收站中所有可见的文档列表
	 * 
	 * @author jiadao
	 */
	List<RecycleFileVO> getVisibleDocList(@Param("incId") Long incId, @Param("userId") Long userId,
			@Param("filter") String filter);

	// List<RecycleFileVO> getVisibleDocListByFilter(@Param("incId")Long
	// incId,@Param("userId")Long userId,@Param("filter")String filter);

	/**
	 * 批量彻底删除文档
	 * 
	 * @author jiadao
	 */
	int removeDocCompletely(@Param("docIdList") List<Long> docIdList);

	/**
	 * 获取存在在回收站中某文件夹下的所有子文档
	 * 
	 * @author jiadao
	 */
	List<Long> getAllChildDocIdByDocParentIdInRecycle(@Param("folderId") String folderId);

	/**
	 * 获取recycle表中doc id 对应的version id
	 */
	List<Long> getVersionIdFromDocId(@Param("docId") Long docId);

	/**
	 * 获取文件名
	 */
	List<String> getDocNames(@Param("docIds") List<Long> docIds);

	/**
	 * 获取回收站中已经保存了30天的文档的incId
	 */
	List<Long> getDeletingIncId();

	/**
	 * 获取回收站中某inc下已经保存了30天的文档id
	 */
	List<Long> getDeletingDocIdByIncId(@Param("incId") Long incId);
}
