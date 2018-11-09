package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.yunpan.api.dal.dao.DocumentPermissionMapper;
import com.quarkdata.yunpan.api.dal.dao.ExternalUserMapper;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.UsersApi;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.DocumentPermissionExample;
import com.quarkdata.yunpan.api.model.dataobj.ExternalUser;
import com.quarkdata.yunpan.api.model.dataobj.ExternalUserExample;
import com.quarkdata.yunpan.api.service.ExternalUserService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yanyq1129@thundersoft.com on 2018/6/4.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ExternalUserServiceImpl implements ExternalUserService {

    private static Logger logger = LoggerFactory.getLogger(ShareServiceImpl.class);

    @Autowired
    private ExternalUserMapper externalUserMapper;

    @Autowired
    private DocumentPermissionMapper documentPermissionMapper;

    @Autowired
    private UsersApi usersApi;

    @Override
    public Long timingDelete() {
        Long count = 0L;
        List<String> userIds = new ArrayList<>();

        ExternalUserExample externalUserExample = new ExternalUserExample();
        externalUserExample.createCriteria().andExpiryDateLessThan(new Date());
        List<ExternalUser> externalUsers = this.externalUserMapper.selectByExample(externalUserExample);

        if(CollectionUtils.isNotEmpty(externalUsers)) {
            for(ExternalUser externalUser: externalUsers) {
                // 删除过期系统创建账号关联权限数据
                DocumentPermissionExample documentPermissionExample = new DocumentPermissionExample();
                documentPermissionExample.createCriteria().andIncIdEqualTo(externalUser.getIncId())
                        .andReceiverIdEqualTo(externalUser.getUserId()).andReceiverTypeEqualTo("3");
                this.documentPermissionMapper.deleteByExample(documentPermissionExample);
                userIds.add(externalUser.getUserId().toString());
            }

            // 删除过期系统创建账号数据
            count = this.externalUserMapper.deleteByExample(externalUserExample);
            this.logger.info("<<<<<< delete expire external user, quantity is : " + count);
        }

        // 删除users表中过期系统创建账号
        if(CollectionUtils.isNotEmpty(userIds)) {
            String[] userIdArr = new String[userIds.size()];
            ResultCode<Object> resultCode = this.usersApi.batchDeleteUsers(userIds.toArray(userIdArr));
            if(0 == resultCode.getCode()) {
                this.logger.info("<<<<<<delete expire user success<<<<<<");
            } else {
                this.logger.error("<<<<<<delete expire user error, msg is : ", resultCode.getMsg());
            }
        }

        return count;
    }
}
