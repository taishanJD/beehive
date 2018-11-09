package com.quarkdata.yunpan.api.dal.dao;


import com.quarkdata.yunpan.api.model.vo.DocumentLinkExtend;
import com.quarkdata.yunpan.api.model.vo.ShareAndLinkVO;

import java.util.List;
import java.util.Map;

public interface ExternalLinkMapper2 {
    List<DocumentLinkExtend> getLinks(Map<String, Object> params);

    /**
     * 获取我的分享(共享and外链)列表---我的外链
     * @param params
     * @return
     */
    List<ShareAndLinkVO> getLinkVOList(Map<String, Object> params);
}