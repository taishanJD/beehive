package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.dataobj.MessageUserDocumentRel;
import com.quarkdata.yunpan.api.model.dataobj.MessageUserDocumentRelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MessageUserDocumentRelMapper {
    long countByExample(MessageUserDocumentRelExample example);

    int deleteByExample(MessageUserDocumentRelExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MessageUserDocumentRel record);

    int insertSelective(MessageUserDocumentRel record);

    List<MessageUserDocumentRel> selectByExample(MessageUserDocumentRelExample example);

    MessageUserDocumentRel selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MessageUserDocumentRel record, @Param("example") MessageUserDocumentRelExample example);

    int updateByExample(@Param("record") MessageUserDocumentRel record, @Param("example") MessageUserDocumentRelExample example);

    int updateByPrimaryKeySelective(MessageUserDocumentRel record);

    int updateByPrimaryKey(MessageUserDocumentRel record);

    MessageUserDocumentRel selectForUpdate(Long id);
}