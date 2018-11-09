package com.quarkdata.yunpan.api.model.common;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ResultTwoDataCode<T,V> {
    private String msg;
    private Integer code;
    private T data;
    private V secondData;

    public V getSecondData() {
        return secondData;
    }

    public void setSecondData(V secondData) {
        this.secondData = secondData;
    }

    public ResultTwoDataCode() {
        this.msg = Messages.SUCCESS_MSG;
        this.code = Messages.SUCCESS_CODE;
        this.data = null;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}