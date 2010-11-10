package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;

public class SetInmhItemAsViewedAction implements Action<RpcData> {


    int runId;
    String type;
    String file;
    int sessionNumber;
    public SetInmhItemAsViewedAction(){}
    
    public SetInmhItemAsViewedAction(int runId, String type, String file,int sessionNumber) {
        this.runId = runId;
        this.type = type;
        this.file = file;
        this.sessionNumber = sessionNumber;
    }
    public int getRunId() {
        return runId;
    }
    public void setRunId(int runId) {
        this.runId = runId;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getFile() {
        return file;
    }
    public void setFile(String file) {
        this.file = file;
    }

    
    public int getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(int sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    @Override
    public String toString() {
        return "SetInmhItemAsViewedAction [runId=" + runId + ", type=" + type + ", file=" + file + ", sessionNumber="
                + sessionNumber + "]";
    }    
}
