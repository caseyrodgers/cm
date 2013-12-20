package hotmath.gwt.cm_core.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

/** represents a single student assigned to an Assignment
 * 
 * @author casey
 *
 */
public class AssignmentStudent implements Response{
    private int assignKey;
    private int uid;
    public AssignmentStudent() {}
    
    public AssignmentStudent(int assignKey, int uid) {
        this.assignKey = assignKey;
        this.uid = uid;
    }

    public int getAssignKey() {
        return assignKey;
    }

    public void setAssignKey(int assignKey) {
        this.assignKey = assignKey;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "AssignmentStudent [assignKey=" + assignKey + ", uid=" + uid + "]";
    }
}
