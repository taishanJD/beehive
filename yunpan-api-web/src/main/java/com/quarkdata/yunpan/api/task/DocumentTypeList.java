package com.quarkdata.yunpan.api.task;

import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.service.ArchivalFileService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by liuda
 * Date : 2018/3/2
 * 存放每日凌晨4点更新的所有文件类型
 */
public class DocumentTypeList {

    @Autowired
    private ArchivalFileService archivalFileService;

    private ResultCode resultCode;

    public ResultCode getResultCode() {
        if (resultCode==null){
            resultCode = archivalFileService.selectAllDocumentType();
        }
        return resultCode;
    }

    public void setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }
}
