package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.ParallelProgramDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.ParallelProgramUsageModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.shared.client.rpc.action.GetParallelProgramUsageAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentForParallelProgramAction;

import java.sql.Connection;
import java.util.List;

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
