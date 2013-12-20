package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class SaveStaticWhiteboardDataAction implements Action<RpcData>{
    
    private int adminId;
    private String pid;
    private CommandType commandType;
    private String data;

    public SaveStaticWhiteboardDataAction(){}

    public SaveStaticWhiteboardDataAction(int adminId, String pid, CommandType commandType, String data) {
        this.adminId = adminId;
        this.pid = pid;
        this.commandType = commandType;
        this.data = data;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
