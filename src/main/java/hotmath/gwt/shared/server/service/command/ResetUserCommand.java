package hotmath.gwt.shared.server.service.command;

import hotmath.cm.program.CmProgramFlow;
import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.ResetUserAction;
import hotmath.testset.ha.StudentUserProgramModel;

import java.sql.Connection;

public class ResetUserCommand implements ActionHandler<ResetUserAction, RpcData> {

    @Override
    public RpcData execute(final Connection conn, ResetUserAction action) throws Exception {
        
        switch(action.getType()) {
            case FULL:
                CmProgramFlow cmProgram = new CmProgramFlow(conn, action.getId());
                cmProgram.reset(conn);
                break;

                
            case RESENT_QUIZ:
                StudentUserProgramModel program = CmUserProgramDao.getInstance().loadProgramInfo(conn,  action.getId());
                int userId = program.getUserId();
                CmProgramFlow programFlow = new CmProgramFlow(conn, userId);
                
                programFlow.getActiveInfo().setActiveRunId(0); // 
                programFlow.getActiveInfo().setActiveTestId(0); // 
                
                programFlow.saveActiveInfo(conn);
                break;
        }
        
        return new RpcData("status=OK");
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return ResetUserAction.class;
    }

}
