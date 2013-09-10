package hotmath.gwt.cm_rpc_assignments.client.model;

import java.util.List;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

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
