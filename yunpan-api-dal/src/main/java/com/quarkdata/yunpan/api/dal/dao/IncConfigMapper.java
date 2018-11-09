package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.dataobj.IncConfig;
import com.quarkdata.yunpan.api.model.dataobj.IncConfigExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface IncConfigMapper {
    long countByExample(IncConfigExample example);

    int deleteByExample(IncConfigExample example);

    int deleteByPrimaryKey(Long id);

    int insert(IncConfig record);

    int insertSelective(IncConfig record);

    List<IncConfig> selectByExampleWithBLOBs(IncConfigExample example);

    List<IncConfig> selectByExample(IncConfigExample example);

    IncConfig selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") IncConfig record, @Param("example") IncConfigExample example);

    int updateByExampleWithBLOBs(@Param("record") IncConfig record, @Param("example") IncConfigExample example);

    int updateByExample(@Param("record") IncConfig record, @Param("example") IncConfigExample example);

    int updateByPrimaryKeySelective(IncConfig record);

    int updateByPrimaryKeyWithBLOBs(IncConfig record);

    int updateByPrimaryKey(IncConfig record);

    IncConfig selectForUpdate(Long id);
}