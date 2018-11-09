package com.quarkdata.yunpan.api.model.vo;

import com.quarkdata.yunpan.api.model.dataobj.Document;

import java.util.List;

/**linkIdVO
 * @author maorl
 * @date 12/29/17.
 */
public class DocumentIdExtendVO extends Document{
    private  String documentId;

    private boolean col;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public boolean isCol() {
        return col;
    }

    public void setCol(boolean col) {
        this.col = col;
    }
}
