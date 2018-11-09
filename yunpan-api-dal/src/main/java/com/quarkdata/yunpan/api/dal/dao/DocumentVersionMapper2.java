package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.dataobj.DocumentVersion;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface DocumentVersionMapper2 {

	// DocumentVersion selectVersionByDocId(@Param("docId") Long id);

	String getFileNameByVersionId(@Param("versionId") Long versionId);

	/**
	 * 通过文件id删除该文件的所有版本
	 * 
	 * @author jiadao
	 * @date 2017-12-19
	 */
	int delDocVersionByDocIdBatch(@Param("docIdList") List<Long> docIds, @Param("updateUser") Long updateUser);

	/**
	 * 查找某文件的所有版本id
	 * 
	 * @author jiadao
	 */
	List<Long> getAllDocVersionIdByDocId(@Param("docId") Long docId);

	/**
	 * 查找文件列表中所有文件的所有版本id
	 * 
	 * @author jiadao
	 */
	List<Long> getAllDocVersionIdByDocIds(@Param("docIds") List<Long> docIds);
	
	/**
	 * 通过文件版本id，删除某一条文件版本记录
	 * 
	 * @author jiadao
	 */
	int delDocVersionById(@Param("id") Long docVersionId, @Param("updateUser") Long updateUser);

	/**
	 * 通过版本id 批量删除版本记录
	 */
	int delDocVersionByIds(@Param("ids") List<Long> versionIds, @Param("updateUser") Long updateUser);
	
	/**
	 * 批量还原某文件的所有被删版本
	 * 
	 * @author jiadao
	 */
	int recoverDocVersionByDocId(@Param("docIdList") List<Long> docIds, @Param("updateUser") Long updateUser);

	/**
	 * 通过文件id，获取该文件所有尚未删除的版本id
	 * @author jiadao
	 */
	List<Long> getAllNotDelVersionIdByDocId(@Param("docId")Long docId);
	
	/**
	 * 通过文件id列表，获取该文件列表中的文件的所有尚未删除的版本id
	 * @author jiadao
	 */
	List<Long> getAllNotDelVersionIdByDocIds(@Param("docIds")List<Long> docIds);
	/**
	 * 保留最近N个历史版本
	 * 
	 * @param incId
	 * 
	 * @param number
	 * @return 要删除的文档历史版本记录ID的集合
	 */
	List<Long> updateDocumentHistoryVersionByNum(@Param("incId") Long incId, @Param("number") Integer number);

	/**
	 * 保留最近N天历史版本
	 * 
	 * @param incId
	 * 
	 * @param days
	 * @return 要删除的文档历史版本记录ID的集合
	 */
	List<Long> updateDocumentHistoryVersionByDays(@Param("incId") Long incId, @Param("days") Integer days);

	/**
	 * 根据ID批量删除document_version记录
	 * 
	 * @param incId
	 * 
	 * @param ids
	 */
	void deleteDocumentVersionByIds(Map<String, Object> params);

	/**
	 * 查最新版本号
	 * @param documentId
	 * @return
	 */
	DocumentVersion selectDocumentVersionById(Long documentId);

}