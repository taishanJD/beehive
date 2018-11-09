package com.quarkdata.yunpan.api.log;

import javax.servlet.http.HttpServletRequest;

import com.quarkdata.yunpan.api.model.common.ActionType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.model.common.ResultCode;

/**
 * @Author yanyq1129@thundersoft.com
 * @Description 保存日志Demo
 * 说明: 文档相关操作日志请在 Service 中分别继承 BaseLogService 和 BaseLogServiceImpl 调用其中方法(addDocumentLog())进行日志记录
 */
@Controller
@RequestMapping(RouteKey.PREFIX_API + "/log")
public class LogDemoController {

	/**
	 * 保存日志
	 * 说明: 文档相关操作日志请在 Service 中分别继承 BaseLogService 和 BaseLogServiceImpl 调用其中方法(addDocumentLog())进行日志记录
	 * 
	 * @param request
	 */
	@RequestMapping("/add_log")
	// ----------其它日志(不需要关联记录文档id的)在需要保存操作日志的方法上添加此注解, 最好还是在service中进行操作日志的记录------------
//	@LogAnnotation
	@ResponseBody
	public ResultCode<String> addLog(HttpServletRequest request) {
		ResultCode<String> result = new ResultCode<>();

		// 1.......逻辑代码......

		// 2.方法返回前调用LogUtils保存用户操作日志
		// 2.1如果是单条操作,传入单条动作详情String 例如: 撤销管理员权限日志记录
		LogUtils.addLog(request, ActionType.DELETE_THE_SYSTEM_ADMINISTRATOR, "user1");

		// 2.2如果是批量操作,传入动作详情集合List<String>
		/*List<String> details = new ArrayList<String>();
		details.add("user1");
		details.add("user2");
		details.add("user3");
		details.add("user4");
		LogUtils.addLogs(request, ActionType.DELETE_THE_ORGANIZATION_ADMINISTRATOR, details);*/
		return result;
	}
}
