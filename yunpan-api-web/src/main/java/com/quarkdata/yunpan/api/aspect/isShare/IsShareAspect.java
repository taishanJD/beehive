package com.quarkdata.yunpan.api.aspect.isShare;

import com.quarkdata.quark.share.model.dataobj.Department;
import com.quarkdata.yunpan.api.aspect.exception.FileIsDeleteException;
import com.quarkdata.yunpan.api.aspect.exception.FileIsNotShareException;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.common.DocumentConstants;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.dataobj.DocumentShare;
import com.quarkdata.yunpan.api.service.DocumentService;
import com.quarkdata.yunpan.api.service.ShareService;
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
 * Created by yanyq1129@thundersoft.com on 2018/7/2.
 */
@Aspect
@Component
public class IsShareAspect {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ShareService shareService;

    @Before(value = "@annotation(com.quarkdata.yunpan.api.aspect.isShare.IsShare)")
    public void isShareToMe(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        IsShare isShare = method.getAnnotation(IsShare.class);
        Object documentIds = AnnotationResolver.newInstance().resolver(joinPoint, isShare.documentIds());
        Object shareIds = AnnotationResolver.newInstance().resolver(joinPoint, isShare.shareIds());
        Integer incId = UserInfoUtil.getIncorporation().getId();
        Integer userId = UserInfoUtil.getUserInfoVO().getUser().getUserId();
        Department department = UserInfoUtil.getUserInfoVO().getDepartment();
        List<Long> groupIds = UserInfoUtil.getGroupIds();
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
                    if (document == null || DocumentConstants.DOCUMENT_IS_DELETE_YES.equals(document.getIsDelete())) {
                        throw new FileIsDeleteException();
                    }
                    if(document != null && DocumentConstants.DOCUMENT_TYPE_PERSONAL.equals(document.getType())) {
                        if(!document.getCreateUser().toString().equals(userId.toString())) {
                            Boolean iShare = this.shareService.getIsShareToMeByDocId(incId, userId, groupIds, department == null || department.getId().equals(0) ? null : department.getId(), document.getId());
                            if(!iShare) {
                                throw new FileIsNotShareException();
                            }
                        }
                    }
                }
            }
        }
        if(null != shareIds && !shareIds.equals("")) {
            List<Object> shareIdList = new ArrayList<>();
            if (shareIds instanceof String || shareIds instanceof Long) {
                String idStr = String.valueOf(shareIds);
                if (StringUtils.isNotBlank(idStr)) {
                    for (String id : idStr.split(",")) {
                        shareIdList.add(Long.parseLong(id));
                    }
                }
            }
            if (shareIds instanceof List) {
                shareIdList = (List<Object>) shareIds;
            }
            if(CollectionUtils.isNotEmpty(shareIdList)) {
                for(Object shareId: shareIdList) {
                    DocumentShare documentShare = this.shareService.getShareById(Long.parseLong(shareId.toString()));
                    if(documentShare != null) {
                        Long documentId = documentShare.getDocumentId();
                        Boolean iShare = this.shareService.getIsShareToMeByDocId(incId, userId, groupIds, department == null || department.getId().equals(0) ? null : department.getId(), documentId);
                        if(!iShare) {
                            throw new FileIsNotShareException();
                        }
                    } else {
                        throw new FileIsNotShareException();
                    }
                }
            }
        }
    }

}
