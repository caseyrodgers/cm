package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.ParallelProgramDao;
import hotmath.gwt.cm_rpc.client.model.CmParallelProgram;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
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
        	ParallelProgramModel model = new ParallelProgramModel();
        	
        	model.setAdminId(pp.getAdminId());
        	model.setCmProgId(pp.getCmProgId());
        	model.setId(pp.getId());
        	model.setName(pp.getName());
        	model.setPassword(pp.getPassword());
        	model.setStudentCount(pp.getStudentCount());
        	model.setIsActive(pp.isActive());

        	modelList.add(model);
        }
        return modelList;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetParallelProgramsAction.class;
    }
    
}
