package com.quarkdata.yunpan.api.service;

import com.quarkdata.yunpan.api.model.dataobj.IncConfig;

/**
 * Created by xiexl on 2018/1/16.
 */
public interface FileService {

    void update(IncConfig config);
    byte[] showLogo(IncConfig config);

}
