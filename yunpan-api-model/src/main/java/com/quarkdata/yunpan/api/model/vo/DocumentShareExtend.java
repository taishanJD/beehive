package com.quarkdata.yunpan.api.model.vo;

import com.quarkdata.yunpan.api.model.dataobj.Document;

/**
 * @author maorl
 * @date 12/16/17.
 */
public class DocumentShareExtend extends Document {
    private Long documentId;

    private Long shareId;

    private String createUsername;

    private String directShare;

//    private List<Tag> tags;

    private Long createUser;

    private boolean isCol;

    private String permission;


    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }


    @Override
    public Long getCreateUser() {
        return createUser;
    }

    @Override
    public void setCreateUser(Long createUser) {
        this.createUser = createUser;
    }

    @Override
    public String getCreateUsername() {
        return createUsername;
    }

    @Override
    public void setCreateUsername(String createUsername) {
        this.createUsername = createUsername;
    }

//    public List<Tag> getTags() {
//        return tags;
//    }

//    public void setTags(List<Tag> tags) {
//        this.tags = tags;
//    }

    public boolean isCol() {
        return isCol;
    }

    public void setCol(boolean col) {
        isCol = col;
    }


    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getDirectShare() {
        return directShare;
    }

    public void setDirectShare( String directShare) {
        this.directShare = directShare;
    }

    public Long getShareId() {
        return shareId;
    }

    public void setShareId(Long shareId) {
        this.shareId = shareId;
    }

    @Override
    public boolean equals(Object that) {
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        DocumentShareExtend other = (DocumentShareExtend) that;
        if (createUsername.equals(other.createUsername) && documentId.equals(other.documentId)) {
            if ("1".equals(this.permission)) {
                other.setPermission("1");
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getDocumentName() == null) ? 0 : getDocumentName().hashCode());
        result = prime * result + ((getDocumentType() == null) ? 0 : getDocumentType().hashCode());
        result = prime * result + ((getIdPath() == null) ? 0 : getIdPath().hashCode());
        result = prime * result + ((getCreateUser() == null) ? 0 : getCreateUser().hashCode());
        result = prime * result + ((getCreateUsername() == null) ? 0 : getCreateUsername().hashCode());
        result = prime * result + ((getDocumentId() == null) ? 0 : getDocumentId().hashCode());
        result = prime * result + ((getDirectShare() == null) ? 0 : getDirectShare().hashCode());
        return result;
    }
}
