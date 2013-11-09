package hotmath.gwt.cm_rpc_assignments.client.rpc;

import hotmath.gwt.cm_rpc_assignments.client.model.AssignmentRealTimeStatsUsers;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;

public class GetAssignmentRealTimeStatsUsersAction implements Action<AssignmentRealTimeStatsUsers>{ 
    
    private int assignKey;
    private String pid;

    public GetAssignmentRealTimeStatsUsersAction() {}

    public GetAssignmentRealTimeStatsUsersAction(int assignKey, String pid) {
        this.assignKey = assignKey;
        this.pid = pid;
    }

    public int getAssignKey() {
        return assignKey;
    }

    public void setAssignKey(int assignKey) {
        this.assignKey = assignKey;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public String toString() {
        return "GetAssignmentRealTimeStatsUsersAction [assignKey=" + assignKey + ", pid=" + pid + "]";
    }
    
    
}
