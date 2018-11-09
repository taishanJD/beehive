package com.quarkdata.yunpan.api.service;

import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.ExternalLink;
import com.quarkdata.yunpan.api.model.vo.DocumentIdLinkVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * @author maorl
 * @date 12/19/17.
 */
public interface AccessLinkService extends BaseLogService {
    ResultCode accessLink(HttpServletRequest request, String linkCode, String authorization, String fetchCode);

    boolean isLegal(List<String> documentIds, String linkCode);

    ResultCode getOwner(String linkCode);

    ResultCode<List<DocumentIdLinkVO>> getSubFileLink(Long parentId, String linkCode);

    /**
     * 根据外链码获取外链对象
     * @param linkCode
     * @return
     */
    ExternalLink getLinkByLinkCode(String linkCode);

    void updateByPrimaryKey(ExternalLink link);
}
