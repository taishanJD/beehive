package com.quarkdata.yunpan.api.aspect.checkDocPermission;

import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.service.DocumentPermissionService;
import com.quarkdata.yunpan.api.util.AnnotationResolver;
import com.quarkdata.yunpan.api.util.common.Exception.YCException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanyq1129@thundersoft.com on 2018/9/10.
 */
@Component
@Aspect
public class CheckDocPermissionAspect {

    @Autowired
    private DocumentPermissionService documentPermissionService;

    @Before(value = "@annotation(com.quarkdata.yunpan.api.aspect.checkDocPermission.CheckDocPermission)")
    public void hasPermission(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        CheckDocPermission checkDocPermisson = method.getAnnotation(CheckDocPermission.class);
        Object documentIds = AnnotationResolver.newInstance().resolver(joinPoint, checkDocPermisson.ids());
        Object permissionIndex = AnnotationResolver.newInstance().resolver(joinPoint, checkDocPermisson.permission());
        if(null != documentIds && !documentIds.equals("")) {
            List<Object> docIdList = new ArrayList<>();
            if (documentIds instanceof String || documentIds instanceof Long) {
                String idStr = String.valueOf(documentIds);
                if (StringUtils.isNotBlank(idStr)) {
                    for (String id : idStr.split(",")) {
                        docIdList.add(Long.parseLong(id));
                    }
                }
            }
            if (documentIds instanceof List) {
                docIdList = (List<Object>) documentIds;
            }
            if(CollectionUtils.isNotEmpty(docIdList)) {
                for(Object docId: docIdList) {
                    Boolean hasPermission = this.documentPermissionService.hasPermission(UserInfoUtil.getIncId(), UserInfoUtil.getUserId(), UserInfoUtil.getGroupIds(), UserInfoUtil.getDeptId(), Long.parseLong(docId.toString()), String.valueOf(permissionIndex));
                    if(!hasPermission) {
                        throw new YCException(Messages.API_AUTHEXCEPTION_MSG, Messages.API_AUTHEXCEPTION_CODE);
                    }
                }
            }
        }
    }
}
