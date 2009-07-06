package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.util.RpcData;

public class SetInmhItemAsViewedAction implements Action<RpcData>{


    int runId;
    String type;
    String file;
    public SetInmhItemAsViewedAction(int runId, String type, String file) {
        this.runId = runId;
        this.type = type;
        this.file = file;
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

    @Override
    public String toString() {
        return "SetInmhItemAsViewedAction [file=" + file + ", runId=" + runId + ", type=" + type + "]";
    }    
}
