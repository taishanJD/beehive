package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.yunpan.api.dal.dao.ClientDownloadPathMapper;
import com.quarkdata.yunpan.api.dal.rest.onespace.OneSpaceApi;
import com.quarkdata.yunpan.api.dal.rest.onespace.OneSpaceApiConstants;
import com.quarkdata.yunpan.api.model.common.Constants;
import com.quarkdata.yunpan.api.model.dataobj.ClientDownloadPath;
import com.quarkdata.yunpan.api.model.dataobj.ClientDownloadPathExample;
import com.quarkdata.yunpan.api.service.ClientDownloadPathService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yanyq1129@thundersoft.com on 2018/7/25.
 */
@Service
@Transactional(readOnly = true)
public class ClientDownloadPathServiceImpl implements ClientDownloadPathService {

    @Autowired
    private ClientDownloadPathMapper clientDownloadPathMapper;

    @Autowired
    private OneSpaceApi oneSpaceApi;

    @Override
    public Map<String, String> getClientDownloadPathByPlatform(String id, String domain, String platform) {
        Map<String, String> pathMap = new HashMap<>();
        String[] platforms = platform.split(",");
        for (String pf : platforms) {
            if (Constants.CLIENT_ANDROID.equals(pf.toLowerCase())) {
                // 安卓客户端从oneSpace获取
                String clientDownloadPath = this.oneSpaceApi.getClientDownloadPath(id, domain, OneSpaceApiConstants.ClientFlag.CLIENT_ANDROID);
                pathMap.put(Constants.CLIENT_ANDROID, clientDownloadPath);
            } else if (Constants.CLIENT_IOS.equals(pf.toLowerCase())) {
                // iOS客户端从oneSpace获取
                String clientDownloadPath = this.oneSpaceApi.getClientDownloadPath(id, domain, OneSpaceApiConstants.ClientFlag.CLIENT_IOS);
                pathMap.put(Constants.CLIENT_IOS, clientDownloadPath);
            } else {
                ClientDownloadPathExample example = new ClientDownloadPathExample();
                example.createCriteria().andDomainEqualTo(domain.toLowerCase()).andPlatformEqualTo(pf.toLowerCase());
                List<ClientDownloadPath> clientDownloadPaths = this.clientDownloadPathMapper.selectByExample(example);
                if (CollectionUtils.isNotEmpty(clientDownloadPaths)) {
                    ClientDownloadPath clientDownloadPath = clientDownloadPaths.get(0);
                    pathMap.put(clientDownloadPath.getPlatform(), clientDownloadPath.getDownloadPath());
                }
            }
        }
        return pathMap;
    }

    @Override
    public String getDefaultClientDownloadPathByPlatform(Integer incId, String platform) {
        return this.oneSpaceApi.getDefaultClientDownloadPath(incId, platform);
    }
}
