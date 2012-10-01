package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;



/** Save a single Whiteboard command for this user
 * 
 * @author casey
 *
 */
public class SaveAssignmentWhiteboardDataAction implements Action<RpcData> {

    Integer uid;
    String pid;
    String commandData;
    
    
    CommandType commandType;
    private int assignKey;
    
    public SaveAssignmentWhiteboardDataAction() {}
     
    
    public SaveAssignmentWhiteboardDataAction(int uid, int assignKey, String pid, CommandType commandType, String commandData) {
        this.uid = uid;
        this.assignKey = assignKey;
        this.pid = pid;
        this.commandType = commandType;
        this.commandData = commandData;
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
