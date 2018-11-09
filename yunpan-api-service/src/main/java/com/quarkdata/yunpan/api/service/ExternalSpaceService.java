package com.quarkdata.yunpan.api.service;

import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.common.YunPanApiException;
import com.quarkdata.yunpan.api.model.vo.DocumentExtend;
import com.quarkdata.yunpan.api.model.vo.DocumentPrivilegeVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * 外部共享空间service
 *
 * @author hanhy
 * 	2017年12月12日
 */
public interface ExternalSpaceService extends BaseLogService {
    /**
     * 根据文档id查询外部账号
     * @param documentId
     * @return
     */
    ResultCode<Users> getExternalUser(String documentId);

    /**
     * 根据文档id删除外部账号
     * @param documentId
     */
    ResultCode<String> deleteExternalUser(String documentId);


}
