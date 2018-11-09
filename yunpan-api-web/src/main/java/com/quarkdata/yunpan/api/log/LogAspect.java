package com.quarkdata.yunpan.api.log;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.quarkdata.yunpan.api.model.common.ActionDetail;
import com.quarkdata.yunpan.api.model.dataobj.LogDocumentRel;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.quarkdata.quark.share.model.dataobj.Incorporation;
import com.quarkdata.quark.share.model.vo.UserInfoVO;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.dataobj.Log;
import com.quarkdata.yunpan.api.service.LogService;
import com.quarkdata.yunpan.api.util.GetIPUtils;

/**
 * @Author yanyq1129@thundersoft.com
 * @Description 日志切面类
 */
@Component
@Aspect
public class LogAspect {
	@Autowired
	private LogService logService;

	/**
	 * 日志记录
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);

	/**
	 * 后置通知中记录用户操作日志
	 * 
	 * @param joinPoint
	 */
	@After(value = "@annotation(com.quarkdata.yunpan.api.log.LogAnnotation) ")
	public void after(JoinPoint joinPoint) {

		try {
			// 1.从登录态中获取用户信息
			// 获取当前用户信息（包括：用户、用户组、部门、角色）
			UserInfoVO userInfoVO = UserInfoUtil.getUserInfoVO();
            Long userId = -1L;
            String userName = "";
			if (userInfoVO != null){
                userId = Long.parseLong(userInfoVO.getUser().getUserId().toString());
                userName = userInfoVO.getUser().getUserName();
            }
            // 获取当前用户所在企业信息
			Incorporation incorporation = UserInfoUtil.getIncorporation();
            Long incId = -1L;
			if (incorporation != null){
                incId = Long.parseLong(incorporation.getId().toString());
            }

			// 2.从request域中获取用户操作记录(动作类型和动作详情)
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
					.getRequest();
			String actionType = (String) request.getAttribute(LogParameters.actionType);
			ActionDetail actionDetail = (ActionDetail) request.getAttribute(LogParameters.actionDetail);
			String documentId = (String) request.getAttribute(LogParameters.documentId);

			// 3.将用户操作日志保存到数据库
			if (actionDetail != null && StringUtils.isNotBlank(actionDetail.getDetail())) {
				// 单条操作
				Log log = new Log();
				log.setCreateUserId(userId);
				log.setCreateUsername(userName);
				log.setIncId(incId);
				log.setActionType(actionType);
				log.setActionDetail(actionDetail.getDetail());
				log.setCreateTime(new Date());
				log.setUserIp(GetIPUtils.getRequestIp(request));
				logService.add(log);

				// 判断是否是文档相关操作日志
				if(StringUtils.isNotBlank(documentId)){
                    LogDocumentRel logDocumentRel = new LogDocumentRel();
                    logDocumentRel.setIncId(incId);
                    logDocumentRel.setCreateUserId(userId);
                    logDocumentRel.setLogId(log.getId());
                    logDocumentRel.setDocumentId(Long.parseLong(documentId));
                    logDocumentRel.setCreateTime(new Date());
                    this.logService.addLogDocumentRel(logDocumentRel);
                }
			}
			if (actionDetail != null && actionDetail.getDetails() != null && actionDetail.getDetails().size() > 0) {
				// 批量操作
				for (String detail : actionDetail.getDetails()) {
					if (StringUtils.isNotBlank(detail)) {
						Log log = new Log();
						log.setCreateUserId(userId);
						log.setCreateUsername(userName);
						log.setIncId(incId);
						log.setActionType(actionType);
						log.setActionDetail(detail);
						log.setCreateTime(new Date());
						log.setUserIp(GetIPUtils.getRequestIp(request));
						logService.add(log);
					}
				}
			}

		} catch (Exception e) {
			// 记录本地异常日志
			LOGGER.error("后置通知异常");
			LOGGER.error("异常信息:" + e.getMessage());
		}

	}

}
