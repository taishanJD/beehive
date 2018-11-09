package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.dataobj.Tag;
import com.quarkdata.yunpan.api.model.dataobj.TagExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagMapper2 {
   List<Tag> getTagList(@Param("incId") Long incId,@Param("docId")Long docId);
   
   
   /**
    * 删除某个标签
    * @author jiadao 
    */
   int delTagByTagId(@Param("incId")Long incId,@Param("userId")Long userId,@Param("tagId")Long tagId);
   /**
    * 获取top10 Tag列表
    * @author jiadao
    */
   List<Tag> getTopTenTagList(@Param("incId")Long incId,@Param("userId")Long userId,@Param("tagIds") List<Long> tagIds);
   
   /**
    * 获取所有Tag，以字典顺序排序
    * @author jiadao 
    */
   List<Tag> getAllTags(@Param("incId")Long incId,@Param("userId")Long userId,@Param("filter")String filter);
}