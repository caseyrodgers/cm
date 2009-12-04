package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.result.WhiteboardCommand;

/** Returns the whiteboard data for a given user/pid
 * 
 * @author casey
 *
 */
public class GetWhiteboardDataAction implements Action<CmList<WhiteboardCommand>> {

    Integer uid;
    String pid;

    public GetWhiteboardDataAction(){}
    
    public GetWhiteboardDataAction(Integer uid, String pid) {
        this.uid = uid;
        this.pid = pid;
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
    
    @Override
    public String toString() {
        return "GetWhiteboardDataAction [pid=" + pid + ", uid=" + uid + "]";
    }
}
