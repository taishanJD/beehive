package com.quarkdata.yunpan.api.aspect.isLock;

import com.quarkdata.yunpan.api.aspect.exception.FileIsLockedException;
import com.quarkdata.yunpan.api.model.common.DocumentConstants;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.service.DocumentService;
import com.quarkdata.yunpan.api.util.AnnotationResolver;
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
 * Created by yanyq1129@thundersoft.com on 2018/9/6.
 */
@Component
@Aspect
public class IsLockAspect {

    @Autowired
    private DocumentService documentService;

    @Before(value = "@annotation(com.quarkdata.yunpan.api.aspect.isLock.IsLock)")
    public void isLocked(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        IsLock isLock = method.getAnnotation(IsLock.class);
        Object documentIds = AnnotationResolver.newInstance().resolver(joinPoint, isLock.ids());
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
                    Document document = this.documentService.getDocumentById(Long.parseLong(docId.toString()));
                    if (document != null && DocumentConstants.DOCUMENT_TYPE_ORGANIZED.equals(document.getType()) && DocumentConstants.IS_LOCK_YES.equals(document.getIsLock())) {
                        throw new FileIsLockedException();
                    }
                }
            }
        }
    }
}
