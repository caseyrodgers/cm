package hotmath.gwt.shared.server.service.command;

import hotmath.cm.program.CmProgramFlow;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

import org.apache.log4j.Logger;

/** Expose CM program flow operations
 *  
 * @author casey
 *
 */
public class GetCmProgramFlowCommand implements ActionHandler<GetCmProgramFlowAction, CmProgramFlowAction> {

    static final Logger LOGGER = Logger.getLogger(GetCmProgramFlowCommand.class);

    @Override
    public CmProgramFlowAction execute(Connection conn, GetCmProgramFlowAction action) throws Exception {

        // new CmStudentDao().verifyActiveProgram(conn, action.getTestId());

    	long start = System.currentTimeMillis();
        CmProgramFlow cmFlow = new CmProgramFlow(conn, action.getUserId());
        LOGGER.info(String.format("+++ CmProgramFlow(): took %d msec", System.currentTimeMillis()-start));
        start = System.currentTimeMillis();
        try {
        switch(action.getFlowType()) {
            case NEXT:
                return cmFlow.moveToNextFlowItem(conn);

            case RETAKE_SEGMENT:
                return cmFlow.retakeActiveProgramSegment(conn);

            case ACTIVE:
            default:
                return cmFlow.getActiveFlowAction(conn);
        }
        }
        finally{
            LOGGER.info(String.format("+++ FlowType: %s, took %d msec", action.getFlowType(), System.currentTimeMillis()-start));        	
        }
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetCmProgramFlowAction.class;
    }
}





