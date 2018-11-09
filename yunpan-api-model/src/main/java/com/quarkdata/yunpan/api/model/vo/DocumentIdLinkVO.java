package com.quarkdata.yunpan.api.model.vo;

import com.quarkdata.yunpan.api.model.dataobj.Document;

/**linkIdVO
 * @author maorl
 * @date 12/29/17.
 */
public class DocumentIdLinkVO extends Document{
    private  String documentId;

    private String linkType;

    private String linkCode;

    public String getLinkType() {
        return linkType;
    }

    public DocumentIdLinkVO setLinkType(String linkType) {
        this.linkType = linkType;
        return this;
    }

    public String getLinkCode() {
        return linkCode;
    }

    public DocumentIdLinkVO setLinkCode(String linkCode) {
        this.linkCode = linkCode;
        return this;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

}
