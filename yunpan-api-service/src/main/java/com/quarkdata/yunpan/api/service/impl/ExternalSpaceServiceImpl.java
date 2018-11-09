package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.yunpan.api.dal.dao.*;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.UsersApi;
import com.quarkdata.yunpan.api.model.common.*;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.dataobj.DocumentExample;
import com.quarkdata.yunpan.api.model.dataobj.DocumentPermission;
import com.quarkdata.yunpan.api.model.vo.DocumentExtend;
import com.quarkdata.yunpan.api.model.vo.DocumentPrivilegeVO;
import com.quarkdata.yunpan.api.service.DocumentPermissionService;
import com.quarkdata.yunpan.api.service.ExternalSpaceService;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

@Service
public class ExternalSpaceServiceImpl extends BaseLogServiceImpl implements ExternalSpaceService {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CommonFileServiceImpl commonFileService;

    @Autowired
    private DocumentPermissionMapper documentPermissionMapper;

    @Autowired
    private DocumentPermissionMapper2 documentPermissionMapper2;

    @Autowired
    private DocumentMapper2 documentMapper2;

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private DocumentPermissionService documentPermissionService;

    @Autowired
    private UsersApi usersApi;

    @Override
    public ResultCode<Users> getExternalUser(String documentId) {
        DocumentPermission documentPermission=documentPermissionMapper2.selectByDidAndReceiveType(Long.parseLong(documentId),DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_GENERATE_ACCOUNT);

        ResultCode<Users> resultCode=new ResultCode<>();
        if (documentPermission!=null && documentPermission.getReceiverId()!=null){
            Long userId=documentPermission.getReceiverId();
            resultCode=usersApi.getUserById(userId.toString());
        }else {
            resultCode.setCode(Messages.EXTERNAL_ACCOUNT_NOT_EXIST_CODE);
            resultCode.setMsg(Messages.EXTERNAL_ACCOUNT_NOT_EXIST_MSG);
        }
        return resultCode;
    }


    @Override
    public ResultCode<String> deleteExternalUser(String documentId) {
        DocumentPermission documentPermission=documentPermissionMapper2.selectByDidAndReceiveType(Long.parseLong(documentId),DocumentConstants.DOCUMENT_PERMISSION_RECEIVER_TYPE_GENERATE_ACCOUNT);
        ResultCode<String> resultCode=new ResultCode<>();
        if (documentPermission!=null && documentPermission.getReceiverId()!=null){
            Long userId=documentPermission.getReceiverId();
            resultCode=usersApi.delete(userId.intValue());
            //删除对应documentpermission中的对应关系
            documentPermissionMapper2.deleteByReceiveId(userId);
        }else {
            resultCode.setCode(Messages.EXTERNAL_ACCOUNT_NOT_EXIST_CODE);
            resultCode.setMsg(Messages.EXTERNAL_ACCOUNT_NOT_EXIST_MSG);
        }
        return resultCode;
    }

}
