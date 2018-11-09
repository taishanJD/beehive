package com.quarkdata.quark.share.model.vo;

import com.quarkdata.quark.share.model.dataobj.Department;

import java.util.List;

public class DepartmentVO extends Department{

    private boolean isLastNode;//是否叶子节点
    private List<DepartmentVO> children;
    private String _type;

    public boolean isLastNode() {
        return isLastNode;
    }

    public void setLastNode(boolean lastNode) {
        isLastNode = lastNode;
    }

    public List<DepartmentVO> getChildren() {
        return children;
    }

    public void setChildren(List<DepartmentVO> children) {
        this.children = children;
    }

    public String get_type() {
        return _type;
    }

    public void set_type(String _type) {
        this._type = _type;
    }
}
