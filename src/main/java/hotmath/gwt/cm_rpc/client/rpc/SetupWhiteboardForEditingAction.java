package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class SetupWhiteboardForEditingAction implements Action<RpcData> {
    
    private String pid;
    private String pidEdit;
    private SetupType setupType;
    

    public enum SetupType{
        /** copy WB into tmp area for editing
         * return pid used to allow unique editing
         */
        CREATE,
        
        /** save tmp WB back
         * 
         */
        SAVE
        };

    public SetupWhiteboardForEditingAction(){}
    
    public SetupWhiteboardForEditingAction(SetupType type, String pid) {
        this.setupType = type;
        this.pid = pid;
    }
    
    public SetupWhiteboardForEditingAction(SetupType type, String pid, String pidEdit) {
        this(type, pid);
        this.pidEdit = pidEdit;
    }

    public String getPidEdit() {
        return pidEdit;
    }

    public void setPidEdit(String pidEdit) {
        this.pidEdit = pidEdit;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public SetupType getSetupType() {
        return setupType;
    }

    public void setSetupType(SetupType setupType) {
        this.setupType = setupType;
    }
}
