package com.quarkdata.yunpan.api.model.request;

/**
 * Created by liuda
 * Date : 2018/3/2
 */
public class ShowDocumentType {
    private String label;
    private String value;
    public Boolean disabled;

    public ShowDocumentType(String label, String value, Boolean disabled) {
        this.label = label;
        this.value = value;
        this.disabled = disabled;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String toString() {
        return "ShowDocumentType{" +
                "label='" + label + '\'' +
                ", value='" + value + '\'' +
                ", disabled=" + disabled +
                '}';
    }
}
