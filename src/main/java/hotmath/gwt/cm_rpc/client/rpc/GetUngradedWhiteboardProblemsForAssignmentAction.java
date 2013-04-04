package hotmath.gwt.cm_rpc.client.rpc;


/** Get the count of students that have ungraded whiteboard problems
 * 
 *  returned in RpcData 'count'
 * @author casey
 *
 */
public class GetUngradedWhiteboardProblemsForAssignmentAction implements Action<RpcData>{
    
    private int assignKey;

    public GetUngradedWhiteboardProblemsForAssignmentAction() {}
    
    public GetUngradedWhiteboardProblemsForAssignmentAction(int assignKey) {
        this.assignKey = assignKey;
    }

    public int getAssignKey() {
        return assignKey;
    }

    public void setAssignKey(int assignKey) {
        this.assignKey = assignKey;
    }

    @Override
    public String toString() {
        return "GetUngradedWhiteboardProblemsForAssignmentAction [assignKey=" + assignKey + "]";
    }
}
