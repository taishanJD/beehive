package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.dataobj.DocumentCephDelete;
import com.quarkdata.yunpan.api.model.dataobj.DocumentCephDeleteExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DocumentCephDeleteMapper {
    long countByExample(DocumentCephDeleteExample example);

    int deleteByExample(DocumentCephDeleteExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DocumentCephDelete record);

    int insertSelective(DocumentCephDelete record);

    List<DocumentCephDelete> selectByExample(DocumentCephDeleteExample example);

    DocumentCephDelete selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") DocumentCephDelete record, @Param("example") DocumentCephDeleteExample example);

    int updateByExample(@Param("record") DocumentCephDelete record, @Param("example") DocumentCephDeleteExample example);

    int updateByPrimaryKeySelective(DocumentCephDelete record);

    int updateByPrimaryKey(DocumentCephDelete record);

    DocumentCephDelete selectForUpdate(Long id);
}