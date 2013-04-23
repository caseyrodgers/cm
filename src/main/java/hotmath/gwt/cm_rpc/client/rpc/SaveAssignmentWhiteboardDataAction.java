package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;



/** Save a single Whiteboard command for this user
 * 
 * @author casey
 *
 */
public class SaveAssignmentWhiteboardDataAction implements Action<RpcData> {

    Integer uid;
    String pid;
    String commandData;
    boolean isAdmin;
    
    
    CommandType commandType;
    private int assignKey;
    
    public SaveAssignmentWhiteboardDataAction() {}
     
    
    public SaveAssignmentWhiteboardDataAction(int uid, int assignKey, String pid, CommandType commandType, String commandData, boolean isAdmin) {
        this.uid = uid;
        this.assignKey = assignKey;
        this.pid = pid;
        this.commandType = commandType;
        this.commandData = commandData;
        this.isAdmin = isAdmin;
    }


    public boolean isAdmin() {
        return isAdmin;
    }


    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
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


    public String getCommandData() {
        return commandData;
    }


    public void setCommandData(String commandData) {
        this.commandData = commandData;
    }


    public CommandType getCommandType() {
        return commandType;
    }


    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }


    public int getAssignKey() {
        return assignKey;
    }


    public void setAssignKey(int assignKey) {
        this.assignKey = assignKey;
    }


    @Override
    public String toString() {
        return "SaveAssignmentWhiteboardDataAction [uid=" + uid + ",  pid=" + pid + ", commandData="
                + commandData + ", commandType=" + commandType + ", assignKey=" + assignKey + "]";
    }
}
