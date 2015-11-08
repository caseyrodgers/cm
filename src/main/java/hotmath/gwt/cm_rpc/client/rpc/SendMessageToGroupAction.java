package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class SendMessageToGroupAction implements Action<RpcData>{
    
    private int groupId;
    private String message;

    public SendMessageToGroupAction() {}

    public SendMessageToGroupAction(int groupId, String message) {
        this.groupId = groupId;
        this.message = message;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SendMessageToGroupAction [groupId=" + groupId + ", message=" + message + "]";
    }

}
