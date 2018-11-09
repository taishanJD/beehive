package com.quarkdata.yunpan.api.dal.dao;

import org.apache.ibatis.annotations.Param;

public interface MessageUserDocumentRelMapper2 {
    int updateIsRead(@Param("incId") Long incId, @Param("userId") Long userId,@Param("msgId") Long msgId);
}
