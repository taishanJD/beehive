package com.quarkdata.quark.share.model.vo;

import com.quarkdata.quark.share.model.dataobj.Group;

/**
 * Created by yanyq1129@thundersoft.com on 2018/7/18.
 */
public class GroupSearchVO extends Group {
    private String _type;

    public String get_type() {
        return _type;
    }

    public void set_type(String _type) {
        this._type = _type;
    }
}
