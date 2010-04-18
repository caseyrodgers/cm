package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.result.WhiteboardCommand;

/** Returns the whiteboard data for a given user/pid/run
 * 
 * @author casey
 *
 */
public class GetWhiteboardDataAction implements Action<CmList<WhiteboardCommand>> {

    Integer uid;
    String pid;
    Integer runId;


    public GetWhiteboardDataAction(){}
    
    public GetWhiteboardDataAction(Integer uid, String pid, Integer runId) {
        this.uid = uid;
        this.pid = pid;
        this.runId = runId;
    }
    
    public Integer getRunId() {
        return runId;
    }

    public void setRunId(Integer runId) {
        this.runId = runId;
    }
    
    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public String toString() {
        return "GetWhiteboardDataAction [pid=" + pid + ", runId=" + runId + ", uid=" + uid + "]";
    }
}
