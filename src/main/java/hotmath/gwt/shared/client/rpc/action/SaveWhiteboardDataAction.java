package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.util.RpcData;

/** Save a single Whiteboard command for this user
 * 
 * @author casey
 *
 */
public class SaveWhiteboardDataAction implements Action<RpcData> {

    Integer uid;
    Integer rid;
    String pid;
    String command;
    String commandData;
    
    
    public SaveWhiteboardDataAction() {}
    
    public SaveWhiteboardDataAction(int uid, int runId, String pid, String command, String commandData) {
        this.uid = uid;
        this.rid = runId;
        this.pid = pid;
        this.command = command;
        this.commandData = commandData;
    }

    @Override
    public String toString() {
        return "SaveWhiteboardDataAction [command=" + command + ", commandData=" + commandData + ", pid=" + pid
                + ", rid=" + rid + ", uid=" + uid + "]";
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

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommandData() {
        return commandData;
    }

    public void setCommandData(String commandData) {
        this.commandData = commandData;
    }
}
