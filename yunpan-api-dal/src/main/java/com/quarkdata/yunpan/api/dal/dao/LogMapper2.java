package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.dataobj.Log;

import java.util.List;
import java.util.Map;

public interface LogMapper2 {
    /**
     * 多条件分页查询
     * @param params
     * @return
     */
    List<Log> selectLogWithConditionsForPage(Map<String, Object> params);

    /**
     * 多条件统计
     * @param params
     * @return
     */
    Integer countLogWithConditions(Map<String, Object> params);
}