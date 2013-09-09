package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.model.CmProgramType;
import hotmath.gwt.cm_rpc.client.rpc.GetProgramMetaInfoAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.CmProgram;

import java.sql.Connection;

public class GetProgramMetaInfoCommand implements ActionHandler<GetProgramMetaInfoAction, RpcData>{

    @Override
    public RpcData execute(Connection conn, GetProgramMetaInfoAction action) throws Exception {
        
        try {
            CmProgramType progType = action.getProgram().getProgramType();
            String subjId = action.getProgram().getSubjectId();
            
            int count = lookupSectionCount(progType, subjId);
            return new RpcData("section_count=" + count);
        }
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    private int lookupSectionCount(CmProgramType progType, String subjId) throws Exception {
        switch(progType) {
            case PROF:
                if(subjId.equals("ElemAlg")) {
                    return CmProgram.ELEMALG.getSectionCount();
                }
                else {
                    // default 
                    return CmProgram.PREALG_PROF.getSectionCount();
                }
                
                
                
            case GRADPREP:
                return CmProgram.CAHSEEHM.getSectionCount();
                
            case GRADPREPNATIONAL:
                return CmProgram.NATIONAL.getSectionCount();
                
            case GRADPREPTX:
                return CmProgram.TAKS.getSectionCount();
                
                default:
                    break;
        }
        
        return 0;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetProgramMetaInfoAction.class;
    }
}
