package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.dataobj.CollectDocumentRel;
import com.quarkdata.yunpan.api.model.dataobj.CollectDocumentRelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CollectDocumentRelMapper {
    long countByExample(CollectDocumentRelExample example);

    int deleteByExample(CollectDocumentRelExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CollectDocumentRel record);

    int insertSelective(CollectDocumentRel record);

    List<CollectDocumentRel> selectByExample(CollectDocumentRelExample example);

    CollectDocumentRel selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") CollectDocumentRel record, @Param("example") CollectDocumentRelExample example);

    int updateByExample(@Param("record") CollectDocumentRel record, @Param("example") CollectDocumentRelExample example);

    int updateByPrimaryKeySelective(CollectDocumentRel record);

    int updateByPrimaryKey(CollectDocumentRel record);

    CollectDocumentRel selectForUpdate(Long id);
}