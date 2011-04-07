package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class QaEntryModel implements Response {

    String item;
    String description;
    boolean verified;
    boolean problem;
    
    public QaEntryModel() {}
    
    public QaEntryModel(String item, String description, boolean verified, boolean problem) {
        this.item = item;
        this.description = description;
        this.verified = verified;
        this.problem = problem;
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

    public boolean isProblem() {
        return problem;
    }

    public void setProblem(boolean problem) {
        this.problem = problem;
    }

    @Override
    public String toString() {
        return "QaEntryModel [item=" + item + ", description=" + description + ", verified=" + verified + ", problem="
                + problem + "]";
    }
}
