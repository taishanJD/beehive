package com.quarkdata.quark.share.model.vo;

import com.quarkdata.quark.share.model.dataobj.Incorporation;
import com.quarkdata.yunpan.api.model.dataobj.Document;

public class UserVo extends UserInfoVO {

    private Document document;//外部账号对应的外部空间

    private Incorporation incorporation; //企业信息

    public Incorporation getIncorporation() {
        return incorporation;
    }

    public void setIncorporation(Incorporation incorporation) {
        this.incorporation = incorporation;
    }


    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

}
