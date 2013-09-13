package hotmath.gwt.cm_rpc_assignments.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

/** Information about a single pids
 *  answer analysis
 *  
 * @author casey
 *
 */
public class PidStats implements Response {
    
    private String pid;
    private int correctPercent;

    public PidStats() {}
    
    public PidStats(String pid, int percent) {
        this.pid = pid;
        this.correctPercent = percent;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getCorrectPercent() {
        return correctPercent;
    }
    
}
