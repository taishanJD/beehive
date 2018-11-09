package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.dataobj.ExternalUser;
import com.quarkdata.yunpan.api.model.dataobj.ExternalUserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ExternalUserMapper {
    long countByExample(ExternalUserExample example);

    Long deleteByExample(ExternalUserExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ExternalUser record);

    int insertSelective(ExternalUser record);

    List<ExternalUser> selectByExample(ExternalUserExample example);

    ExternalUser selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ExternalUser record, @Param("example") ExternalUserExample example);

    int updateByExample(@Param("record") ExternalUser record, @Param("example") ExternalUserExample example);

    int updateByPrimaryKeySelective(ExternalUser record);

    int updateByPrimaryKey(ExternalUser record);

    ExternalUser selectForUpdate(Long id);
}