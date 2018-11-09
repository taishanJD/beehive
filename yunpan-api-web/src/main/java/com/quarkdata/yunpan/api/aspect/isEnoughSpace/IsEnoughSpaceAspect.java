package com.quarkdata.yunpan.api.aspect.isEnoughSpace;

import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.dal.dao.IncConfigMapper;
import com.quarkdata.yunpan.api.aspect.exception.IncSpaceNotEnopughException;
import com.quarkdata.yunpan.api.aspect.exception.PerUserSpaceNotEnoughException;
import com.quarkdata.yunpan.api.model.common.DocumentConstants;
import com.quarkdata.yunpan.api.model.dataobj.IncConfig;
import com.quarkdata.yunpan.api.model.dataobj.IncConfigExample;
import com.quarkdata.yunpan.api.service.DashboardService;
import com.quarkdata.yunpan.api.util.AnnotationResolver;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by yanyq1129@thundersoft.com on 2018/8/2.
 */
@Component
@Aspect
public class IsEnoughSpaceAspect {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private IncConfigMapper incConfigMapper;

    @Before(value = "@annotation(com.quarkdata.yunpan.api.aspect.isEnoughSpace.IsEnoughSpace)")
    public void isEnoughSpace(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        IsEnoughSpace isEnoughSpace = method.getAnnotation(IsEnoughSpace.class);
        Object typeObj = AnnotationResolver.newInstance().resolver(joinPoint, isEnoughSpace.type());
        Object docSizeObj = AnnotationResolver.newInstance().resolver(joinPoint, isEnoughSpace.docSize());
        if(null != typeObj && docSizeObj != null) {
            String type = String.valueOf(typeObj);
            String docSizeStr = String.valueOf(docSizeObj);
            Double docSize = Double.parseDouble(docSizeStr);
            Integer incId = UserInfoUtil.getUserInfoVO().getUser().getIncid();
            Integer userId = UserInfoUtil.getUserInfoVO().getUser().getUserId();
            if(type.equals(DocumentConstants.DOCUMENT_TYPE_PERSONAL)) {
                // 检查个人空间是否充足
                IncConfigExample example = new IncConfigExample();
                example.createCriteria().andIncIdEqualTo(incId.longValue());
                IncConfig incConfig = this.incConfigMapper.selectByExample(example).get(0);
                Double perUserQuota = incConfig.getPerUserQuota().doubleValue();
                Object myUsageObj = this.dashboardService.getUsageByCurrentUserAndIncId(incId, userId).getData();
                Double myUsage = Double.parseDouble(myUsageObj.toString());
                if((perUserQuota * 1024 * 1024 - myUsage * 1024 * 1024) < docSize) {
                    throw new PerUserSpaceNotEnoughException();
                }

            }
            if(type.equals(DocumentConstants.DOCUMENT_TYPE_ORGANIZED)) {
                // 检查组织空间是否充足
                Map<String, Object> incUsage = this.dashboardService.getIncUsage(incId).getData();
                Double inc_quota = Double.parseDouble(incUsage.get("inc_quota").toString()) ;
                Double inc_quota_used = Double.parseDouble(incUsage.get("inc_quota_used").toString()) ;
                if((inc_quota * 1024 * 1024 - inc_quota_used * 1024 * 1024) < docSize) {
                    throw new IncSpaceNotEnopughException();
                }
            }
        }
    }
}
