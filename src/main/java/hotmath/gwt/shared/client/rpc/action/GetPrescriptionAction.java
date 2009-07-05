package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.util.RpcData;

public class GetPrescriptionAction implements Action<RpcData>{

    int runId;
    int sessionNumber;
    boolean updateActiveInfo;
    
    public GetPrescriptionAction(int runId, int sessionNumber, boolean updateActiveInfo) {
        this.runId = runId;
        this.sessionNumber = sessionNumber;
        this.updateActiveInfo = updateActiveInfo;
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public int getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(int sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    public boolean isUpdateActiveInfo() {
        return updateActiveInfo;
    }

    public void setUpdateActiveInfo(boolean updateActiveInfo) {
        this.updateActiveInfo = updateActiveInfo;
    }
    
    public boolean getUpdateActionInfo() {
        return updateActiveInfo;
    }
}