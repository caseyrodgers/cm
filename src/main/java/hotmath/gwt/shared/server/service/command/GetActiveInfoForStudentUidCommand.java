package hotmath.gwt.shared.server.service.command;

import hotmath.cm.program.CmProgramFlow;
import hotmath.gwt.cm_rpc.client.model.StudentActiveInfo;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.GetActiveInfoForStudentUidAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;

import org.apache.log4j.Logger;

/** Get Student Active Info via CmProgramFlow
 *  
 * @author bob
 *
 */
public class GetActiveInfoForStudentUidCommand implements ActionHandler<GetActiveInfoForStudentUidAction, StudentActiveInfo> {

    static final Logger logger = Logger.getLogger(GetActiveInfoForStudentUidCommand.class);

    @Override
    public StudentActiveInfo execute(Connection conn, GetActiveInfoForStudentUidAction action) throws Exception {
        return new CmProgramFlow(conn, action.getUserId()).getActiveInfo();
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetCmProgramFlowAction.class;
    }
}





