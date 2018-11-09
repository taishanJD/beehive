package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.dataobj.DocumentShare;
import com.quarkdata.yunpan.api.model.dataobj.DocumentShareExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DocumentShareMapper {
    long countByExample(DocumentShareExample example);

    int deleteByExample(DocumentShareExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DocumentShare record);

    int insertSelective(DocumentShare record);

    List<DocumentShare> selectByExample(DocumentShareExample example);

    DocumentShare selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") DocumentShare record, @Param("example") DocumentShareExample example);

    int updateByExample(@Param("record") DocumentShare record, @Param("example") DocumentShareExample example);

    int updateByPrimaryKeySelective(DocumentShare record);

    int updateByPrimaryKey(DocumentShare record);

    DocumentShare selectForUpdate(Long id);
}