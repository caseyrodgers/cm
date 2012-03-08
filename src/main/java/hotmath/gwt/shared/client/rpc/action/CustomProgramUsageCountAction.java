package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.shared.client.model.IntValueHolder;

public class CustomProgramUsageCountAction implements Action<IntValueHolder>{
    Integer programId;
    
    public CustomProgramUsageCountAction(){
    }

    public CustomProgramUsageCountAction(Integer programId) {
        this.programId = programId;
    }

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    @Override
    public String toString() {
        return "CustomProgramUsageCountAction [programId=" + programId + "]";
    }
}    
