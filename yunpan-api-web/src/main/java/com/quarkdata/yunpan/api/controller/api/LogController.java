package com.quarkdata.yunpan.api.controller.api;

import com.quarkdata.quark.share.model.dataobj.Department;
import com.quarkdata.quark.share.model.dataobj.Group;
import com.quarkdata.yunpan.api.controller.BaseController;
import com.quarkdata.yunpan.api.controller.RouteKey;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.model.common.ListResult;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.dataobj.Log;
import com.quarkdata.yunpan.api.model.vo.LogAddVO;
import com.quarkdata.yunpan.api.model.vo.LogQueryVO;
import com.quarkdata.yunpan.api.service.LogService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author yanyq1129@thundersoft.com
 * @Description 日志Controller
 */
@Controller
@RequestMapping(RouteKey.PREFIX_API + "/log")
public class LogController extends BaseController {

	@Autowired
	private LogService logService;

	/**
	 * 日志信息综合分页查询
	 * 
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public ResultCode<ListResult<Log>> getLogList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, LogQueryVO logQueryVO) {
		ResultCode<ListResult<Log>> result = new ResultCode<ListResult<Log>>();
		try {
			result = this.logService.getLogList(UserInfoUtil.getIncorporation().getId(), pageNum, pageSize, logQueryVO);
		} catch (Exception e) {
			result.setCode(1);
			result.setMsg(Messages.GET_LOG_FAIL_MSG);
			this.logger.error(Messages.GET_LOG_FAIL_MSG, e);
		}
		return result;

	}

	/**
	 * 导出日志
	 */
	@RequestMapping(value = "/export", method = RequestMethod.GET)
	public void exportLog(HttpServletRequest request, LogQueryVO logQueryVO, HttpServletResponse response) {
		ResultCode<ListResult<Log>> result = new ResultCode<ListResult<Log>>();
		try {
			this.logger.info("导出日志");
			// 获取数据源
			result = this.logService.exportLog(request, UserInfoUtil.getIncorporation().getId(),logQueryVO);
			List<Log> logs = null;
			if (result != null) {
				ListResult<Log> data = result.getData();
				if (data != null) {
					logs = data.getListData();
				}
			}

			// 封装数据
			// 1.创建一个webbook工作簿
			XSSFWorkbook workbook = new XSSFWorkbook();
			// 2.在Excel文件中添加一个sheet页
			XSSFSheet sheet = workbook.createSheet("日志报表");
			// 3.在sheet页中添加表头，第一行
			XSSFRow row = sheet.createRow(0);
			row.setHeight((short) 550);
			// 4.设置单元格的样式
			XSSFCellStyle headStyle = workbook.createCellStyle();
			XSSFCellStyle cellStyle = workbook.createCellStyle();
			// 生成字体
			Font headFont = workbook.createFont();
			headFont.setFontName("宋体");
			headFont.setFontHeightInPoints((short) 14);
			headFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);

			Font cellFont = workbook.createFont();
			cellFont.setFontName("宋体");
			cellFont.setFontHeightInPoints((short) 12);
			cellFont.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
			// 把字体应用到当前的样式
			headStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
			headStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			headStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); // 下边框
			headStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);// 左边框
			headStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);// 上边框
			headStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);// 右边框
			headStyle.setFont(headFont);

			cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
			cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER_SELECTION);
			cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); // 下边框
			cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);// 左边框
			cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);// 上边框
			cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);// 右边框
			cellStyle.setFont(cellFont);
			// 5.创建一个单元格，并设置居中样式
			XSSFCell cell = row.createCell(0);
			cell.setCellValue("修改者");
			cell.setCellStyle(headStyle);
			cell = row.createCell(1);
			cell.setCellValue("动作");
			cell.setCellStyle(headStyle);
			cell = row.createCell(2);
			cell.setCellValue("动作接收者");
			cell.setCellStyle(headStyle);
			cell = row.createCell(3);
			cell.setCellValue("时间");
			cell.setCellStyle(headStyle);
			cell = row.createCell(4);
			cell.setCellValue("IP地址");
			cell.setCellStyle(headStyle);
			// 6.向单元格中填充数据
			if (logs != null && logs.size() > 0) {
				for (int i = 0; i < logs.size(); i++) {
					row = sheet.createRow(i + 1);
					row.setHeight((short) 400);
					Log log = logs.get(i);
					XSSFCell cell0 = row.createCell(0);
					cell0.setCellValue(log.getCreateUsername());
					cell0.setCellStyle(cellStyle);
					XSSFCell cell1 = row.createCell(1);
					cell1.setCellValue(log.getActionType());
					cell1.setCellStyle(cellStyle);
					XSSFCell cell2 = row.createCell(2);
					cell2.setCellValue(log.getActionDetail());
					cell2.setCellStyle(cellStyle);
					XSSFCell cell3 = row.createCell(3);
					cell3.setCellValue(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(log.getCreateTime()));
					cell3.setCellStyle(cellStyle);
					XSSFCell cell4 = row.createCell(4);
					cell4.setCellValue(log.getUserIp());
					cell4.setCellStyle(cellStyle);

				}
			}

			sheet.setColumnWidth(0, 2000 * 3);
			sheet.setColumnWidth(1, 1500 * 3);
			sheet.setColumnWidth(2, 3500 * 3);
			sheet.setColumnWidth(3, 2000 * 3);
			sheet.setColumnWidth(4, 2000 * 3);

			// 冻结表头
			sheet.createFreezePane(5,1);

			// 导出
			// web浏览通过MIME类型判断文件是excel类型
			response.setContentType("application/vnd.ms-excel;charset=utf-8");
			response.setCharacterEncoding("utf-8");

			String agent = request.getHeader("USER-AGENT").toLowerCase();
			response.setContentType("applicationnd.ms-excel");

			// 对文件名进行处理,防止文件名乱码
			String fileName;
			fileName = new String((new Date() + "日志报表.xlsx").getBytes("UTF-8"), "iso-8859-1");
			// Content-disposition属性设置成以附件方式进行下载
			if (agent.contains("firefox")) {
				// 火狐浏览器特殊处理
				response.setCharacterEncoding("utf-8");
				response.setHeader("content-disposition",
						"attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1") + ".xlsx");
			} else {
				response.setHeader("content-disposition", "attachment;filename=" + fileName + ".xlsx");
			}

			OutputStream os = response.getOutputStream();
			workbook.write(os);
			os.flush();
			os.close();
			result.setData(null);
		} catch (Exception e) {
			result.setData(null);
			result.setCode(1);
			result.setMsg(Messages.EXPORT_LOG_FAIL_MSG);
			this.logger.error(Messages.EXPORT_LOG_FAIL_MSG, e);
		}
	}

	/**
	 * 添加浏览日志
	 * @param logAddVOs
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public ResultCode<String> addPreviewLogs(HttpServletRequest request, @RequestBody List<LogAddVO> logAddVOs) {
		ResultCode<String> result = new ResultCode<>();
		if(logAddVOs == null) {
			result.setCode(Messages.MISSING_INPUT_CODE);
			result.setData(Messages.MISSING_INPUT_MSG);
			return result;
		}
		Integer incId = UserInfoUtil.getIncorporation().getId();
		Integer userId = UserInfoUtil.getUserInfoVO().getUser().getUserId();
		List<Group> groupsList = UserInfoUtil.getUserInfoVO().getGroupsList();
		List<Long> groupIds = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(groupsList)) {
			for(Group group: groupsList) {
				groupIds.add(group.getId().longValue());
			}
		}
		Department department = UserInfoUtil.getUserInfoVO().getDepartment();
		try {
			this.logService.addPreviewLogs(request, incId, userId, groupIds, department == null ? null : department.getId().longValue(), logAddVOs);
		} catch (Exception e) {
			logger.error("<<<<<< add log error <<<<<<", e);
			result.setCode(Messages.API_ERROR_CODE);
			result.setMsg(Messages.API_ERROR_MSG);
		}
		return result;
	}

}
