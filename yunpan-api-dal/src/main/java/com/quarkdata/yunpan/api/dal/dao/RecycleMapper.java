package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.dataobj.Recycle;
import com.quarkdata.yunpan.api.model.dataobj.RecycleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RecycleMapper {
    long countByExample(RecycleExample example);

    int deleteByExample(RecycleExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Recycle record);

    int insertSelective(Recycle record);

    List<Recycle> selectByExample(RecycleExample example);

    Recycle selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Recycle record, @Param("example") RecycleExample example);

    int updateByExample(@Param("record") Recycle record, @Param("example") RecycleExample example);

    int updateByPrimaryKeySelective(Recycle record);

    int updateByPrimaryKey(Recycle record);

    Recycle selectForUpdate(Long id);
}