package com.quarkdata.yunpan.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.quarkdata.yunpan.api.model.dataobj.DocumentVersion;

public interface DocumentCephDeleteMapper2 {
	
	/**
	 * update一个列表、并将其is_delete改为"1"，
	 * 表示该条记录将要通过ceph底层删除
	 * @author jiadao
	 */
	int markDocVersion(@Param("ids") List<Long> ids);
	
	/**
	 * 通过doc_ceph_delete id 获取对应的DocumentVersion
	 * @author jiadao
	 */
	List<DocumentVersion> getDeletingDocVersion(@Param("ids") List<Long> ids);
	
	/**
	 *  通过一组doc id，从doc_ceph_delete表中获取已经存在的doc_version_id<p/>
	 *
	 * @author jiadao
	 */
	List<Long> getExistDocVersionIdsByDocIds(@Param("docIds")List<Long> docIds);
	
	/**
	 *  通过一组doc version id，从doc_ceph_delete表中获取已经存在的doc_version_id<p/>
	 *
	 * @author jiadao
	 */
	List<Long> getExistDocVersionIdsByDocVersionIds(@Param("docVersionIds")List<Long> docVersionIds);
	
	/**
	 * 从彻底删除队列表中以时间先后顺序获取前count条数据，
	 * @author jiadao
	 * @Param("userId")Long userId,  @Param("incId")Long incId, 
	 */
	List<Long> getUnMarkedId(@Param("count")int count);
	
	
	/**
	 * 根据id获取incId
	 */
	List<Long> getDeletingIncId(@Param("ids") List<Long> ids);
	
	/**
	 * 根据id和incId获取doc_version
	 */
	List<DocumentVersion> getDeletingDocVersionByIncId(@Param("incId") Long incId,@Param("ids") List<Long> ids);
}
