package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.vo.TagVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagDocumentRelMapper2 {
	/**
	 *获取某用户的top 10标签id列表
	 *@author jiadao 
	 */
	List<Long> getTopTenTagIdList(@Param("incId") Long incId,
								  @Param("userId") Long userId);
	
	/**
	 * 获取设置了某标签的所有文档
	 * @author jiadao
	 */
	List<TagVO> getDocListByTagId(@Param("incId") Long incId,
								  @Param("userId") Long userId,
								  @Param("tagId")Long tagId,
								  @Param("filter")String filter);
}
