package com.quarkdata.yunpan.api.model.request;
import java.util.List;

/**
 * @author maorl
 * @date 12/7/17.
 */
public class AddShareObj {

    private List<String> documentIds;
    private List<Receiver> receiver;

    private List<Receiver> exclusions;

    public void setDocumentIds(List<String> documentIds) {
        this.documentIds = documentIds;
    }
    public List<String> getDocumentIds() {
        return documentIds;
    }

    public void setReceiver(List<Receiver> receiver) {
        this.receiver = receiver;
    }
    public List<Receiver> getReceiver() {
        return receiver;
    }

    public List<Receiver> getExclusions() {
        return exclusions;
    }

    public void setExclusions(List<Receiver> exclusions) {
        this.exclusions = exclusions;
    }
}

