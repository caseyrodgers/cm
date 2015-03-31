package hotmath.gwt.shared.server.service.command.cm2;

import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunAction;
import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunResponse;
import hotmath.gwt.cm_rpc.client.rpc.cm2.CheckCm2QuizAction;
import hotmath.gwt.cm_rpc.client.rpc.cm2.QuizCm2CheckedResult;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.server.service.command.CreateTestRunCommand;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


/** 
 * 
 * creates cm2 quiz question abstractions.
 *
 *
 */
public class CheckCm2QuizCommand implements ActionHandler<CheckCm2QuizAction, QuizCm2CheckedResult> {

    @Override
    public QuizCm2CheckedResult execute(Connection conn, CheckCm2QuizAction action) throws Exception {
        try {
            
            int uid=0;
            PreparedStatement ps=null;
            try {
                ps = conn.prepareStatement("select user_id from HA_TEST where test_id = ?");
                ps.setInt(1,  action.getTestId());
                ResultSet rs = ps.executeQuery();
                if(rs.next()) {
                    uid = rs.getInt("user_id");
                }
                else {
                    throw new CmRpcException("test not found: " + action.getTestId());
                }
            }
            finally {
                SqlUtilities.releaseResources(null, ps,null);
            }
            
            CreateTestRunAction testRunAction = new CreateTestRunAction(action.getTestId(),uid);
            CreateTestRunResponse testRunResults = new CreateTestRunCommand().execute(conn, testRunAction);
            return new QuizCm2CheckedResult(testRunResults);
            
        } catch (Exception e) {
            /** for catching error during debugging */
            throw e;
        }
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return CheckCm2QuizAction.class;
    }
}
