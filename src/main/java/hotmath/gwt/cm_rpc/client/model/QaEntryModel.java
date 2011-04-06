package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class QaEntryModel implements Response {

    String item;
    String description;
    boolean verified;
    
    public QaEntryModel() {}
    
    public QaEntryModel(String item, String description, boolean verified) {
        this.item = item;
        this.description = description;
        this.verified = verified;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @Override
    public String toString() {
        return "QaEntryModel [item=" + item + ", description=" + description + ", verified=" + verified + "]";
    }
}
