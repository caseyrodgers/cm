package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;


/** Save a single Whiteboard command for this user
 * 
 * @author casey
 *
 */
public class SaveWhiteboardDataAction implements Action<RpcData> {

    Integer uid;
    Integer rid;
    String pid;
    String commandData;
    
    static public enum CommandType{DRAW,CLEAR, UNDO};
    CommandType commandType;
    
    public SaveWhiteboardDataAction() {}
     
    
    public SaveWhiteboardDataAction(int uid, int runId, String pid, CommandType commandType, String commandData) {
        this.uid = uid;
        this.rid = runId;
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

    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }


    public CommandType getCommandType() {
        return commandType;
    }

    
    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }


    public String getCommandData() {
        return commandData;
    }

    public void setCommandData(String commandData) {
        this.commandData = commandData;
    }
    

    @Override
    public String toString() {
        return "SaveWhiteboardDataAction [commandDataLen=" + (commandData!=null?commandData.length():-1) + ", commandType=" + commandType + ", pid=" + pid
                + ", rid=" + rid + ", uid=" + uid + "]";
    }
}
