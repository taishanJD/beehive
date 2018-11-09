package com.quarkdata.yunpan.api.model.vo;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Author yanyq1129@thundersoft.com
 * @Date 2017年12月14日
 * @Description 日志信息综合查询包装对象
 */
public class LogQueryVO implements Serializable {

	private static final long serialVersionUID = 7168951095283372088L;
	// 查询日志的天数
	private Integer days;
	// 日志查询开始时间
	private Date startTime;
	// 日志查询结束时间
	private Date endTime;
	// 操作者名称
	private String operator;

	public LogQueryVO() {
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

}
