package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.dataobj.DocumentPermission;
import com.quarkdata.yunpan.api.model.dataobj.DocumentPermissionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DocumentPermissionMapper {
    long countByExample(DocumentPermissionExample example);

    int deleteByExample(DocumentPermissionExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DocumentPermission record);

    int insertSelective(DocumentPermission record);

    List<DocumentPermission> selectByExample(DocumentPermissionExample example);

    DocumentPermission selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") DocumentPermission record, @Param("example") DocumentPermissionExample example);

    int updateByExample(@Param("record") DocumentPermission record, @Param("example") DocumentPermissionExample example);

    int updateByPrimaryKeySelective(DocumentPermission record);

    int updateByPrimaryKey(DocumentPermission record);

    DocumentPermission selectForUpdate(Long id);
}