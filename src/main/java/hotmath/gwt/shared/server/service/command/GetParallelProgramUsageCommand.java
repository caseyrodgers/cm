package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.ParallelProgramDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.ParallelProgramUsageModel;
import hotmath.gwt.shared.client.rpc.action.GetParallelProgramUsageAction;

import java.sql.Connection;
import java.util.List;

/**
 * 
 * @author bob
 *
 */

public class GetParallelProgramUsageCommand implements ActionHandler<GetParallelProgramUsageAction, CmList<ParallelProgramUsageModel>>{

    @Override
    public CmList<ParallelProgramUsageModel> execute(final Connection conn, GetParallelProgramUsageAction action) throws Exception {

    	int ppId = action.getParallelProgId();
        List<ParallelProgramUsageModel> list = ParallelProgramDao.getInstance().getUsageForParallelProgram(ppId);
        
        CmList<ParallelProgramUsageModel> modelList = new CmArrayList<ParallelProgramUsageModel>();

        for (ParallelProgramUsageModel pp : list) {
        	ParallelProgramUsageModel model = new ParallelProgramUsageModel();
        	
        	model.setStudentName(pp.getStudentName());
        	model.setUserId(pp.getUserId());
        	model.setActivity(pp.getActivity());
        	model.setResult(pp.getResult());
        	model.setUseDate(pp.getUseDate());
        	model.setIsQuiz(pp.getIsQuiz());

        	modelList.add(model);
        }
        return modelList;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetParallelProgramUsageAction.class;
    }
    
}
