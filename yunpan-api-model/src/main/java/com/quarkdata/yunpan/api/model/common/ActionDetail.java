package com.quarkdata.yunpan.api.model.common;

import java.util.List;

/**
 * @Author yanyq1129@thundersoft.com
 * @Description 动作详情
 *
 */
public class ActionDetail {

	/**
	 * 单个动作
	 */
	private String detail;
	/**
	 * 批量动作
	 */
	private List<String> details;

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public List<String> getDetails() {
		return details;
	}

	public void setDetails(List<String> details) {
		this.details = details;
	}

}
