package com.quarkdata.yunpan.api.model.vo;

import com.quarkdata.yunpan.api.model.dataobj.DocumentEsIndex;

public class DocumentEsIndexExtend {
    private Long indexId;
    private String documentName;
    private String type;
    private String documentType;
    private Long documentId;
    private String documentVersionId;

    public Long getIndexId(){ return this.indexId; }

    public void setIndexId(Long indexId){ this.indexId = indexId; }

    public Long getDocumentId() {
        return documentId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getType() {
        return type;
    }

    public String getDocumentVersionId() {
        return documentVersionId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDocumentVersionId(String documentVersionId) {
        this.documentVersionId = documentVersionId;
    }
}
