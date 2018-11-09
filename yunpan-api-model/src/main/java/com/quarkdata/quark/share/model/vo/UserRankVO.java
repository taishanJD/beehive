package com.quarkdata.quark.share.model.vo;

import java.io.Serializable;

/**
 * @Author yanyq1129@thundersoft.com
 * @Date 2017年12月19日
 * @Description 用户个人空间使用量排行包装对象
 *
 */
public class UserRankVO implements Serializable {
	private static final long serialVersionUID = 7430767278473709089L;
	private Long id;
	// 用户
	private String createUserName;
	// 用户使用总空间
	private String SumSize;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getSumSize() {
		return SumSize;
	}

	public void setSumSize(String sumSize) {
		SumSize = sumSize;
	}

}
