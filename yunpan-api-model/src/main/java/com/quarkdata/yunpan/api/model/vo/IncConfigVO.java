package com.quarkdata.yunpan.api.model.vo;

import java.io.Serializable;

import com.quarkdata.quark.share.model.dataobj.Incorporation;
import com.quarkdata.yunpan.api.model.dataobj.IncConfig;

/**
 * @Author yanyq1129@thundersoft.com
 * @Date 2017年12月17日
 * @Description 企业信息包装对象
 *
 */
public class IncConfigVO implements Serializable {

	private static final long serialVersionUID = -407924050407444808L;

	/**
	 * 云盘配置信息
	 */
	private IncConfig incConfig;

	/**
	 * 企业信息
	 */
	private Incorporation incorporation;

	public IncConfig getIncConfig() {
		return incConfig;
	}

	public void setIncConfig(IncConfig incConfig) {
		this.incConfig = incConfig;
	}

	public Incorporation getIncorporation() {
		return incorporation;
	}

	public void setIncorporation(Incorporation incorporation) {
		this.incorporation = incorporation;
	}
}