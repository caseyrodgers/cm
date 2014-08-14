package hotmath.gwt.cm_core.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class GroupCopyModel implements Response {
    int assignmentId;
    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    String group;
    String assignment;
    
    
    public GroupCopyModel() {}
    
    public GroupCopyModel(String groupName, String assignment, int assignmentKey) {
        this.group = groupName;
        this.assignment = assignment;
        this.assignmentId = assignmentKey;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }


    public String getAssignment() {
        return assignment;
    }

    public void setAssignment(String assignment) {
        this.assignment = assignment;
    }
}
