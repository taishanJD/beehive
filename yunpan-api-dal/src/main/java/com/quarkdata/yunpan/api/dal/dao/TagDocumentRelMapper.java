package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.dataobj.TagDocumentRel;
import com.quarkdata.yunpan.api.model.dataobj.TagDocumentRelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TagDocumentRelMapper {
    long countByExample(TagDocumentRelExample example);

    int deleteByExample(TagDocumentRelExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TagDocumentRel record);

    int insertSelective(TagDocumentRel record);

    List<TagDocumentRel> selectByExample(TagDocumentRelExample example);

    TagDocumentRel selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TagDocumentRel record, @Param("example") TagDocumentRelExample example);

    int updateByExample(@Param("record") TagDocumentRel record, @Param("example") TagDocumentRelExample example);

    int updateByPrimaryKeySelective(TagDocumentRel record);

    int updateByPrimaryKey(TagDocumentRel record);

    TagDocumentRel selectForUpdate(Long id);
}