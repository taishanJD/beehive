package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.dataobj.DocumentPermissionInherit;
import com.quarkdata.yunpan.api.model.dataobj.DocumentPermissionInheritExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DocumentPermissionInheritMapper {
    long countByExample(DocumentPermissionInheritExample example);

    int deleteByExample(DocumentPermissionInheritExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DocumentPermissionInherit record);

    int insertSelective(DocumentPermissionInherit record);

    List<DocumentPermissionInherit> selectByExample(DocumentPermissionInheritExample example);

    DocumentPermissionInherit selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") DocumentPermissionInherit record, @Param("example") DocumentPermissionInheritExample example);

    int updateByExample(@Param("record") DocumentPermissionInherit record, @Param("example") DocumentPermissionInheritExample example);

    int updateByPrimaryKeySelective(DocumentPermissionInherit record);

    int updateByPrimaryKey(DocumentPermissionInherit record);

    DocumentPermissionInherit selectForUpdate(Long id);
}