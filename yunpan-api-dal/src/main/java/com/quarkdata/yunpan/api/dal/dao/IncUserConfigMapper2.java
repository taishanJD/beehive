package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.dataobj.IncUserConfig;
import com.quarkdata.yunpan.api.model.dataobj.IncUserConfigExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IncUserConfigMapper2 {
    IncUserConfig selectByUserIdAndIncId(@Param("userId")Long userId,@Param("incId")Long incId);
}
