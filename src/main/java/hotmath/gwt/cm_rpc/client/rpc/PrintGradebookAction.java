package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.shared.client.rpc.CmWebResource;

public class PrintGradebookAction implements Action<CmWebResource> {
    private int aid;
    private int groupId;

    public PrintGradebookAction(){}
    
    public PrintGradebookAction(int aid, int groupId) {
        this.aid = aid;
        this.groupId = groupId;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "PrintGradebookAction [aid=" + aid + ", groupId=" + groupId + "]";
    }
}
