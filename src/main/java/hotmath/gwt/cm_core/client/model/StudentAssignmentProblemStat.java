package hotmath.gwt.cm_core.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class StudentAssignmentProblemStat implements Response {

    private int uid;
    private String name;
    private String status;

    public StudentAssignmentProblemStat() {
    }

    public StudentAssignmentProblemStat(int uid, String name, String status) {
        this.uid = uid;
        this.name = name;
        this.status = status != null ? status : "";
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    

    @Override
    public String toString() {
        return "StudentAssignmentProblemStat [uid=" + uid + ", name=" + name + ", status=" + status + "]";
    }    
}
