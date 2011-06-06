package hotmath.gwt.shared.server.service.command;

import hotmath.cm.program.CmProgramFlow;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.GetActiveInfoForStudentUidAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfoModel;

import java.sql.Connection;

import org.apache.log4j.Logger;

/** Get Student Active Info Model via CmProgramFlow
 *  
 * @author bob
 *
 */
public class GetActiveInfoForStudentUidCommand implements ActionHandler<GetActiveInfoForStudentUidAction, StudentActiveInfoModel> {

    static final Logger logger = Logger.getLogger(GetActiveInfoForStudentUidCommand.class);

    @Override
    public StudentActiveInfoModel execute(Connection conn, GetActiveInfoForStudentUidAction action) throws Exception {

        CmProgramFlow cmFlow = new CmProgramFlow(conn, action.getUserId());
        
        StudentActiveInfoModel activeInfo = new StudentActiveInfoModel(cmFlow.getActiveInfo());
        activeInfo.setTotalSegments(cmFlow.getTotalProgramSegments());
       
        return activeInfo;
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetCmProgramFlowAction.class;
    }
}





