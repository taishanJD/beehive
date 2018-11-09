package com.quarkdata.yunpan.api.model.vo;

import com.quarkdata.yunpan.api.model.dataobj.Document;

/**
 * Created by yanyq1129@thundersoft.com on 2018/5/30.
 */
public class OrganizedDocumentDeleteVO extends DocumentExtend {
    public boolean getCanBeDelete() {
        return canBeDelete;
    }

    public void setCanBeDelete(boolean canBeDelete) {
        this.canBeDelete = canBeDelete;
    }

    private boolean canBeDelete;
}
