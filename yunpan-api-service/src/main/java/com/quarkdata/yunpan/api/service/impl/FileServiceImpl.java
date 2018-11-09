package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.yunpan.api.dal.dao.IncConfigMapper;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.dataobj.IncConfig;
import com.quarkdata.yunpan.api.model.dataobj.IncConfigExample;
import com.quarkdata.yunpan.api.service.FileService;
import com.quarkdata.yunpan.api.util.common.Exception.YCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xiexl on 2018/1/16.
 */
@Service
public class FileServiceImpl implements FileService{
    @Autowired
    private IncConfigMapper incConfigMapper;

    /**
     * 测试logo显示
     * @param config
     * @return
     */
    @Override
    public byte[] showLogo(IncConfig config) {
        IncConfigExample incConfigExample = new IncConfigExample();
        incConfigExample.createCriteria().andIncIdEqualTo(config.getIncId());
        IncConfig incConfig = incConfigMapper.selectByExample(incConfigExample)
                .get(0);
        if(incConfig!=null && incConfig.getLogo()!=null)
        {
            return incConfig.getLogo();
        }
        else
        {
            throw new YCException(Messages.API_OBJECT_NOTFOUND_MSG,Messages.API_OBJECT_NOTFOUND_CODE);
        }
    }

    @Override
    public void update(IncConfig config) {
        IncConfigExample incConfigExample = new IncConfigExample();
        incConfigExample.createCriteria().andIncIdEqualTo(config.getIncId());
        IncConfig incConfig = incConfigMapper.selectByExample(incConfigExample)
                .get(0);
        if(incConfig!=null)
        {
            incConfig.setLogo(config.getLogo());
            incConfigMapper.updateByPrimaryKeySelective(incConfig);
        }
        else
        {
            throw new YCException(Messages.API_OBJECT_NOTFOUND_MSG,Messages.API_OBJECT_NOTFOUND_CODE);
        }


    }
}
