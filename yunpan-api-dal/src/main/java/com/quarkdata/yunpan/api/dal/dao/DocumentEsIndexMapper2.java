package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.vo.DocumentEsIndexExtend;

import java.util.List;

public interface DocumentEsIndexMapper2 {
    List<DocumentEsIndexExtend> selectDocumentIndexByVersionId(long incId);
}
