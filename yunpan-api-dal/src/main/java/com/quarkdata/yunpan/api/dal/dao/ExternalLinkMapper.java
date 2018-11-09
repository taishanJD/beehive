package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.dataobj.ExternalLink;
import com.quarkdata.yunpan.api.model.dataobj.ExternalLinkExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ExternalLinkMapper {
    long countByExample(ExternalLinkExample example);

    int deleteByExample(ExternalLinkExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ExternalLink record);

    int insertSelective(ExternalLink record);

    List<ExternalLink> selectByExample(ExternalLinkExample example);

    ExternalLink selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ExternalLink record, @Param("example") ExternalLinkExample example);

    int updateByExample(@Param("record") ExternalLink record, @Param("example") ExternalLinkExample example);

    int updateByPrimaryKeySelective(ExternalLink record);

    int updateByPrimaryKey(ExternalLink record);

    ExternalLink selectForUpdate(Long id);
}