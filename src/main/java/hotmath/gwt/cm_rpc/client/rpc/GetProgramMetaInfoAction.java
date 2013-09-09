package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.model.StudentProgramModel;

public class GetProgramMetaInfoAction implements Action<RpcData> {
    
    private StudentProgramModel program;

    public GetProgramMetaInfoAction(){}
    
    public GetProgramMetaInfoAction(StudentProgramModel program) {
        this.program = program;
    }

    public StudentProgramModel getProgram() {
        return program;
    }

    public void setProgram(StudentProgramModel program) {
        this.program = program;
    }
}
