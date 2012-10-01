package hotmath.gwt.cm_rpc.client.rpc;


/** Returns the whiteboard data for a given user's
 *  assignment problem.
 * 
 * @author casey
 *
 */
public class GetAssignmentWhiteboardDataAction implements Action<CmList<WhiteboardCommand>> {

    private Integer uid;
    private String pid;
    private Integer assignId;

    public GetAssignmentWhiteboardDataAction(){}
    
    public GetAssignmentWhiteboardDataAction(Integer uid, String pid, Integer assignId) {
        this.uid = uid;
        this.pid = pid;
        this.assignId = assignId;
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

    public Integer getAssignId() {
        return assignId;
    }

    public void setAssignId(Integer assignId) {
        this.assignId = assignId;
    }
    

    @Override
    public String toString() {
        return "GetAssignmentWhiteboardDataAction [uid=" + uid + ", pid=" + pid + ", assignId=" + assignId + "]";
    }

}
