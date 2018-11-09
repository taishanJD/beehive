package com.quarkdata.yunpan.api.model.vo;

import java.util.List;

/**
 * Created by liuda
 * Date : 2018/3/27
 * 全文检索返回包装类
 */
public class FullTextVo {
    /**
     * 搜索记录数
     */
    private String resultSize;

    /**
     * 返回的结果集
     */
    private List<ResultEsVo> resultEsVoList;

    public FullTextVo() {
    }

    public FullTextVo(String resultSize, List<ResultEsVo> resultEsVoList) {
        this.resultSize = resultSize;
        this.resultEsVoList = resultEsVoList;
    }

    @Override
    public String toString() {
        return "FullTextVo{" +
                "resultSize='" + resultSize + '\'' +
                ", resultEsVoList=" + resultEsVoList +
                '}';
    }

    public String getResultSize() {
        return resultSize;
    }

    public void setResultSize(String resultSize) {
        this.resultSize = resultSize;
    }

    public List<ResultEsVo> getResultEsVoList() {
        return resultEsVoList;
    }

    public void setResultEsVoList(List<ResultEsVo> resultEsVoList) {
        this.resultEsVoList = resultEsVoList;
    }
}
