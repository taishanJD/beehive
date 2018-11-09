package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.dataobj.IncUserConfig;
import com.quarkdata.yunpan.api.model.dataobj.IncUserConfigExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface IncUserConfigMapper {
    long countByExample(IncUserConfigExample example);

    int deleteByExample(IncUserConfigExample example);

    int deleteByPrimaryKey(Long id);

    int insert(IncUserConfig record);

    int insertSelective(IncUserConfig record);

    List<IncUserConfig> selectByExample(IncUserConfigExample example);

    IncUserConfig selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") IncUserConfig record, @Param("example") IncUserConfigExample example);

    int updateByExample(@Param("record") IncUserConfig record, @Param("example") IncUserConfigExample example);

    int updateByPrimaryKeySelective(IncUserConfig record);

    int updateByPrimaryKey(IncUserConfig record);

    IncUserConfig selectForUpdate(Long id);
}