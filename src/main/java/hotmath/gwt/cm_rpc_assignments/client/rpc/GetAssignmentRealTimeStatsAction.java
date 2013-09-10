package hotmath.gwt.cm_rpc_assignments.client.rpc;

import hotmath.gwt.cm_rpc_assignments.client.model.AssignmentRealTimeStats;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;

public class GetAssignmentRealTimeStatsAction implements Action<AssignmentRealTimeStats>{ 
    
    private int assignKey;

    public GetAssignmentRealTimeStatsAction() {}

    public GetAssignmentRealTimeStatsAction(int assignKey) {
        this.assignKey = assignKey;
    }

    public int getAssignKey() {
        return assignKey;
    }

    public void setAssignKey(int assignKey) {
        this.assignKey = assignKey;
    }
}
