package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.dataobj.DocumentVersion;
import com.quarkdata.yunpan.api.model.dataobj.DocumentVersionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DocumentVersionMapper {
    long countByExample(DocumentVersionExample example);

    int deleteByExample(DocumentVersionExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DocumentVersion record);

    int insertSelective(DocumentVersion record);

    List<DocumentVersion> selectByExample(DocumentVersionExample example);

    DocumentVersion selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") DocumentVersion record, @Param("example") DocumentVersionExample example);

    int updateByExample(@Param("record") DocumentVersion record, @Param("example") DocumentVersionExample example);

    int updateByPrimaryKeySelective(DocumentVersion record);

    int updateByPrimaryKey(DocumentVersion record);

    DocumentVersion selectForUpdate(Long id);
}