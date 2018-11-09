package com.quarkdata.yunpan.api.service;

import java.util.List;

import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.vo.RecycleFileVO;

import javax.servlet.http.HttpServletRequest;

public interface DeleteService extends BaseLogService {

	/**
	 * 批量删除文档（放入回收站）
	 * @author jiadao
	 * @date 2017-12-19
	 * @param docIdStr 待删的doc_id列表
	 * @param request
	 * @return
	 */
	ResultCode<List<String>> delDocBatch(HttpServletRequest request, String docIdStr, long updateUserId, String updateUsename, long incId);
	
	/**
	 * 判断一个文档列表中是否含有，它的上级目录被删除的文档，用于回收站文档还原。
	 * @author jiadao
	 * @date 2017-12-19
	 * @param docIds 待检测的doc id列表
	 * @return true: 有 false：没有
	 */
	boolean hasParentDocDeleted(List<Long> docIds);
	
	/**
	 * 删除一条文件版本<p/>
	 * 会直接从底层删除
	 * @author jiadao
	 */
	ResultCode<String> delDocumentVersion(HttpServletRequest request, Long incId, Long userId, String docVersionIdStr);
	
	/**
	 * 获取回收站中可见的doc列表
	 * @author jiadao
	 * @date 2017-12-19
	 */
	List<RecycleFileVO> getDocListInRecycle(Long incId,Long userId,String filter,String folderId);

	/**
	 * 查找指定文件
	 * @param folderId
	 * @param filter
	 * @param docIds
	 * @return
	 */
	List<RecycleFileVO> getDocListInFolderInRecycleBy(Long folderId,String filter,List<Long> docIds);

	/**
	 * 在回收站中点击文件夹，获取下一级文档目录
	 * @author jiadao
	 * @date 2017-12-19
	 * @param folderId 文件夹id
	 * @return 该文件夹下的子一级文档列表
	 */
	List<RecycleFileVO> getDocListInFolderInRecycle(Long folderId,String filter);
	
	/**
	 * 批量彻底删除文档（从回收站移除） 
	 * @author jiadao
	 * @date 2017-12-19
	 * @param docIds 待删文档列表
	 * @param request
	 * @return
	 */
	ResultCode<List<String>> removeDocCompletelyBatch(HttpServletRequest request, Long incId, Long userId, String docIdsStr);
	
	/**
	 * 批量还原文档
	 * @author jiadao
	 * @date 2017-12-19
	 * @param docIds 待还原文档列表
	 * @param request
	 * @return
	 * */
	ResultCode<List<String>> recoverDocBatch(HttpServletRequest request, String docIdsStr, long updateUser);
	
	/**
	 * 将需要彻底删除的文档版本id，放入doc_ceph_delete表中
	 * @author jiadao
	 * @Date 2017-12-22
	 * @param userId 操作人
	 * @param incId 企业id
	 * @param docVersionIds 待删除的文件版本id列表
	 */
	void addDelDocsToCephDelete(Long userId,Long incId, List<Long> docVersionIds);
	
	/**
	 * 从doc_ceph_delete表中以时间先后顺序，获取前count条未被标记删除的数据，
	 * @author jiadao
	 */
	List<Long> getUnMarkedId(int count);
	
	/**
	 *  获取回收站中一组文档下所有的子文档的versionID, 用于添加到doc_ceph_delete表中以彻底删除<p/>
	 *  返回的版本id中，首先剔除了提前删除的文档，其次剔除了提前删除的版本
	 * @author jiadao
	 */
	List<Long> getAllDocVersionIdsInRecycle(List<Long> docIds);
	
	/**
	 * 自动删除任务：recycle中的文档在规定时间(暂定30天)内没有操作，会自动放入document_ceph_delete表中等待底层删除
	 */
	ResultCode<List<String>> autoDelete();
	
	/**
	 * ceph底层删除任务
	 */
	void getDeletingDocVersion(int count);
 }
