package com.quarkdata.yunpan.api.model.vo;

public class TagVO extends CollectionVO {

	//是否收藏
	private boolean isCollected;

	private String source; // 0-共享空间 2-归档文件

	private String namePath;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getNamePath() {
		return namePath;
	}

	public void setNamePath(String namePath) {
		this.namePath = namePath;
	}

	public boolean isCollected() {
		return isCollected;
	}

	public void setCollected(boolean isCollected) {
		this.isCollected = isCollected;
	}
}
