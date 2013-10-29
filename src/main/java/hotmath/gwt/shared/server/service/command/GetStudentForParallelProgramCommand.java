package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.ParallelProgramDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.shared.client.rpc.action.GetStudentForParallelProgramAction;

import java.sql.Connection;

/**
 * 
 * @author bob
 *
 */

public class GetStudentForParallelProgramCommand implements ActionHandler<GetStudentForParallelProgramAction, StudentModelExt>{

    @Override
    public StudentModelExt execute(final Connection conn, GetStudentForParallelProgramAction action) throws Exception {

    	int ppId = action.getParallelProgId();
    	
    	StudentModelExt sm = ParallelProgramDao.getInstance().getStudentModelForParallelProgram(ppId);

    	return sm;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetStudentForParallelProgramAction.class;
    }
    
}
