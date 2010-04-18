package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.shared.client.util.RpcData;

public class SaveCmLoggerTextAction implements Action<RpcData>{
    String logMsgs;
    int uid;
    
    public SaveCmLoggerTextAction() {}
    
    public SaveCmLoggerTextAction(int uid,String logMsgs) {
        this.uid = uid;
        this.logMsgs = logMsgs;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getLogMsgs() {
        return logMsgs;
    }

    public void setLogMsgs(String logMsgs) {
        this.logMsgs = logMsgs;
    }

}
