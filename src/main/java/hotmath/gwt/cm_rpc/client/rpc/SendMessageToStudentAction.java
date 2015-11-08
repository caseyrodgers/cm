package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class SendMessageToStudentAction implements Action<RpcData>{
    
    private int uid;
    private String message;

    public SendMessageToStudentAction() {}

    public SendMessageToStudentAction(int uid, String message) {
        this.uid = uid;
        this.message = message;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SendMessageToStudentAction [uid=" + uid + ", message=" + message + "]";
    }

}
