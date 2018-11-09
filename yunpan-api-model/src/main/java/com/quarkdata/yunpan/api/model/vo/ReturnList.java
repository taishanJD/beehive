package com.quarkdata.yunpan.api.model.vo;

import com.quarkdata.yunpan.api.model.dataobj.Document;

import java.util.List;

public class ReturnList {
    //所有文件的列表
    private List<Document> allDocList;

    //需要归档的id列表
    private List<Long> archivalList;

    public List<Document> getAllDocList() {
        return allDocList;
    }

    public void setAllDocList(List<Document> allDocList) {
        this.allDocList = allDocList;
    }

    public List<Long> getArchivalList() {
        return archivalList;
    }

    public void setArchivalList(List<Long> archivalList) {
        this.archivalList = archivalList;
    }

    @Override
    public String toString() {
        return "ReturnList{" +
                "allDocIdList=" + allDocList +
                ", archivalList=" + archivalList +
                '}';
    }
}
