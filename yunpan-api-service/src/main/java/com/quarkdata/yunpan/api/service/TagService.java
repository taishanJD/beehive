package com.quarkdata.yunpan.api.service;

import com.quarkdata.yunpan.api.model.dataobj.Tag;
import com.quarkdata.yunpan.api.model.vo.TagVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface TagService extends BaseLogService {
	
	/**
	 * 标签库中添加标签&文档添加新标签
	 *
	 * @param request
	 * @param incId
	 * @param userId
	 * @param tagName
	 */
	Long addTag(HttpServletRequest request, Long incId, Long userId, String tagName);
	
	/**
	 * 文档添加标签
	 *
	 * @param request
	 * @param incId
	 * @param userId
	 * @param tagId
	 * @param docId
	 */
	void addDocTag(HttpServletRequest request, Long incId, Long userId, Long tagId, Long docId);
	
	/**
	 * 标签库中删除标签
	 * 
	 * @param incId
	 * @param userId
	 * @param tagId
	 */
	void deleteTag(Long incId, Long userId, Long tagId);
	
	/**
	 * 删除文档标签
	 * @param request
	 * @param incId
	 * @param userId
	 * @param tagId
	 * @param docId
	 */
	void deleteDocTag(HttpServletRequest request, Long incId, Long userId, Long tagId, Long docId);
	
	/**
	 *  获取某用户的top10标签列表
	 *  @author jiadao
	 */
	List<Tag> getTopTenTagList(Long incId, Long userId);
	
	/**
	 * 获取某用户的所有标签，字典序排序
	 * @author jiadao
	 */
	List<Tag> getAllTagList(Long incId, Long userId,String filter);
	
	/**
	 * 获取使用了某个标签的所有文档
	 * @author jiadao
	 */
	List<TagVO> getDocListByTagId(Long incId, Long userId, List<Long> userGroupIds, Long deptId, Long tagId,String filter);
	
	/**
	 * 若tagName已存在，并且它的is_delete值为‘1’，修改为‘0’
	 */
	Long setIsDeleteByTagName(Long incId,Long userId,String tagName);
	
	/**
	 * 判断tag标签名是否已经存在
	 * @author jiadao
	 * @return  -1: tagName不存在 ；1：tagName存在但被删除；0：tagName存在且没被删除
	 */
	int isTagNameNotExist_1(Long incId, Long userId, String tagName);
	
	
	Tag getTagById(Long tagId);

	/**
	 * 更新tag
	 * @param tag
	 */
	void updateTag(Tag tag);
}
