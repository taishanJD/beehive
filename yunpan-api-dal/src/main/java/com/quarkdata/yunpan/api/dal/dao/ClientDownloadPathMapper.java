package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.dataobj.ClientDownloadPath;
import com.quarkdata.yunpan.api.model.dataobj.ClientDownloadPathExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ClientDownloadPathMapper {
    long countByExample(ClientDownloadPathExample example);

    int deleteByExample(ClientDownloadPathExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ClientDownloadPath record);

    int insertSelective(ClientDownloadPath record);

    List<ClientDownloadPath> selectByExample(ClientDownloadPathExample example);

    ClientDownloadPath selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ClientDownloadPath record, @Param("example") ClientDownloadPathExample example);

    int updateByExample(@Param("record") ClientDownloadPath record, @Param("example") ClientDownloadPathExample example);

    int updateByPrimaryKeySelective(ClientDownloadPath record);

    int updateByPrimaryKey(ClientDownloadPath record);

    ClientDownloadPath selectForUpdate(Integer id);
}