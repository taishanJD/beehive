package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.dataobj.DocumentEsIndex;
import com.quarkdata.yunpan.api.model.dataobj.DocumentEsIndexExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DocumentEsIndexMapper {
    long countByExample(DocumentEsIndexExample example);

    int deleteByExample(DocumentEsIndexExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DocumentEsIndex record);

    int insertSelective(DocumentEsIndex record);

    List<DocumentEsIndex> selectByExample(DocumentEsIndexExample example);

    DocumentEsIndex selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") DocumentEsIndex record, @Param("example") DocumentEsIndexExample example);

    int updateByExample(@Param("record") DocumentEsIndex record, @Param("example") DocumentEsIndexExample example);

    int updateByPrimaryKeySelective(DocumentEsIndex record);

    int updateByPrimaryKey(DocumentEsIndex record);

    DocumentEsIndex selectForUpdate(Long id);
}