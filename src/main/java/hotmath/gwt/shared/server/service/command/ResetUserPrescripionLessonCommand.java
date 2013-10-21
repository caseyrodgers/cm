package hotmath.gwt.shared.server.service.command;

import hotmath.cm.program.CmProgramFlow;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_rpc.client.rpc.ResetUserPrescripionLessonAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;


/** Reset the solution context of the named user's current program lesson
 * 
 * @author casey
 *
 */
public class ResetUserPrescripionLessonCommand implements ActionHandler<ResetUserPrescripionLessonAction,RpcData> {

    static private Logger __logger = Logger.getLogger(ResetUserPrescripionLessonCommand.class);
    
    @Override
    public RpcData execute(Connection conn, ResetUserPrescripionLessonAction action) throws Exception {
        PreparedStatement ps=null;
        try {
            CmProgramFlow cmProgram = new CmProgramFlow(conn,action.getUserId());
            int runId = cmProgram.getActiveInfo().getActiveRunId();
            if(runId == 0) {
                throw new CmException("This user is not currently in a prescription");
            }
            int lessonNumber = cmProgram.getActiveInfo().getActiveRunSession();

            __logger.info("Resetting user lesson: " + action.getUserId() + ", " + cmProgram.getActiveInfo());
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("RESET_USER_LESSON");
            ps = conn.prepareStatement(sql);
            ps.setInt(1, runId);
            ps.setInt(2, runId);
            ps.setInt(3, lessonNumber);
            
            int cnt = ps.executeUpdate();
            __logger.debug("Reset user lesson: " + cnt + "\n" + ps.toString());
        }
        finally {
            SqlUtilities.releaseResources(null,  ps,  null);
        }
        
        return new RpcData("status=OK");
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return ResetUserPrescripionLessonAction.class;
    }
}