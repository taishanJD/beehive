package com.quarkdata.yunpan.api.model.request;

import java.io.Serializable;

/**
 * 
 * Created by xxm on 2016-12-29.
 */
public class TestRequest implements Serializable{

   
    /**
	 * 
	 */
	private static final long serialVersionUID = 4852578842735743193L;
	private String billNum;
	public String getBillNum() {
		return billNum;
	}
	public void setBillNum(String billNum) {
		this.billNum = billNum;
	}
}
