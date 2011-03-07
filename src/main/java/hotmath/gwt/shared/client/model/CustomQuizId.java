package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class CustomQuizId implements Response {
    String pid;
    int loadOrder;
    
    public CustomQuizId(){}
    
    public CustomQuizId(String pid, int loadOrder) {
        this.pid = pid;
        this.loadOrder = loadOrder;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getLoadOrder() {
        return loadOrder;
    }

    public void setLoadOrder(int loadOrder) {
        this.loadOrder = loadOrder;
    }
}
