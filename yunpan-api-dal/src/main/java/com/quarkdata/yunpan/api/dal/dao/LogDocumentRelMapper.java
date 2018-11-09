package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.dataobj.LogDocumentRel;
import com.quarkdata.yunpan.api.model.dataobj.LogDocumentRelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface LogDocumentRelMapper {
    long countByExample(LogDocumentRelExample example);

    int deleteByExample(LogDocumentRelExample example);

    int deleteByPrimaryKey(Long id);

    int insert(LogDocumentRel record);

    int insertSelective(LogDocumentRel record);

    List<LogDocumentRel> selectByExample(LogDocumentRelExample example);

    LogDocumentRel selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") LogDocumentRel record, @Param("example") LogDocumentRelExample example);

    int updateByExample(@Param("record") LogDocumentRel record, @Param("example") LogDocumentRelExample example);

    int updateByPrimaryKeySelective(LogDocumentRel record);

    int updateByPrimaryKey(LogDocumentRel record);

    LogDocumentRel selectForUpdate(Long id);
}