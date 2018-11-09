package com.quarkdata.yunpan.api.aspect.isDelete;

import com.quarkdata.yunpan.api.aspect.exception.FileIsDeleteException;
import com.quarkdata.yunpan.api.model.common.DocumentConstants;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.dataobj.Tag;
import com.quarkdata.yunpan.api.service.DocumentService;
import com.quarkdata.yunpan.api.service.TagService;
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
 * Created by yanyq1129@thundersoft.com on 2018/6/20.
 */
@Component
@Aspect
public class IsDeleteAspect {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private TagService tagService;

    @Before(value = "@annotation(com.quarkdata.yunpan.api.aspect.isDelete.IsDelete)")
    public void isDelete(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        IsDelete isDelete = method.getAnnotation(IsDelete.class);
        Object ids = AnnotationResolver.newInstance().resolver(joinPoint, isDelete.ids());
        Object className = AnnotationResolver.newInstance().resolver(joinPoint, isDelete.className());
        if (null != ids && null != className) {
            List<Object> idList = new ArrayList<>();
            if (ids instanceof String || ids instanceof Long) {
                String idStr = String.valueOf(ids);
                if (StringUtils.isNotBlank(idStr)) {
                    for (String id : idStr.split(",")) {
                        idList.add(Long.parseLong(id));
                    }
                }
            }
            if (ids instanceof List) {
                idList = (List<Object>) ids;
            }
            if (CollectionUtils.isNotEmpty(idList)) {
                String classNameStr = String.valueOf(className);
                for (Object id : idList) {
                    if(!id.toString().equals(DocumentConstants.DOCUMENT_ROOT_PARENTID.toString())) {
                        if(StringUtils.isNotBlank(classNameStr)) {
                            if(IsDeleteType.Document.equals(classNameStr)) {
                                Document document = this.documentService.getDocumentById(Long.parseLong(id.toString()));
                                if (document == null || DocumentConstants.DOCUMENT_IS_DELETE_YES.equals(document.getIsDelete())) {
                                    throw new FileIsDeleteException();
                                }
                            }
                            if(IsDeleteType.DocumentVersion.equals(classNameStr)) {
                                Document document = this.documentService.getDocumentByVersion(Long.parseLong(id.toString()));
                                if(document != null && DocumentConstants.DOCUMENT_IS_DELETE_YES.equals(document.getIsDelete())) {
                                    throw new FileIsDeleteException();
                                }
                            }
                            if(IsDeleteType.Tag.equals(classNameStr)) {
                                Tag tag = this.tagService.getTagById(Long.parseLong(id.toString()));
                                if(tag != null && DocumentConstants.DOCUMENT_IS_DELETE_YES.equals(tag.getIsDelete())) {
                                    throw new FileIsDeleteException();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
