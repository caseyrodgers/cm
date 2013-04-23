package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.ParallelProgramDao;
import hotmath.gwt.cm_rpc.client.model.CmParallelProgram;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.ParallelProgramModel;
import hotmath.gwt.shared.client.rpc.action.GetParallelProgramsAction;

import java.sql.Connection;
import java.util.List;

/**
 * 
 * @author bob
 *
 */

public class GetParallelProgramsCommand implements ActionHandler<GetParallelProgramsAction, CmList<ParallelProgramModel>>{

    @Override
    public CmList<ParallelProgramModel> execute(final Connection conn, GetParallelProgramsAction action) throws Exception {
        
        List<CmParallelProgram> list = ParallelProgramDao.getInstance().getParallelProgramsForAdminId(action.getAdminId());
        
        CmList<ParallelProgramModel> modelList = new CmArrayList<ParallelProgramModel>();

        for (CmParallelProgram pp : list) {
        	ParallelProgramModel model = new ParallelProgramModel(pp.getName(), pp.getId(), pp.getAdminId(), pp.getPassword(), pp.getCmProgId(),
        			pp.getStudentCount(), pp.isActive(), pp.getCmProgName());
        	
        	modelList.add(model);
        }
        return modelList;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetParallelProgramsAction.class;
    }
    
}
