package hotmath.gwt.cm_rpc_assignments.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.util.List;

public class AssignmentRealTimeStats implements Response {
    
    private List<PidStats> pidStats;

    public AssignmentRealTimeStats() {}

    public AssignmentRealTimeStats(List<PidStats> pidStats) {
        this.pidStats = pidStats;
    }

    public List<PidStats> getPidStats() {
        return pidStats;
    }

    public void setPidStats(List<PidStats> pidStats) {
        this.pidStats = pidStats;
    }

}
