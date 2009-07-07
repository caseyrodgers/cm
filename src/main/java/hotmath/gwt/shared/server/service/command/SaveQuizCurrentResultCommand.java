package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.SaveQuizCurrentResultAction;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.testset.ha.HaTest;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;



/** Save the a single Quiz question result
 * 
 * @author casey
 *
 */
public class SaveQuizCurrentResultCommand implements ActionHandler<SaveQuizCurrentResultAction, RpcData> {

    @Override
    public RpcData execute(SaveQuizCurrentResultAction action) throws CmRpcException {
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            
            HaTest test = HaTest.loadTest(conn,action.getTestId());
            test.saveTestQuestionChange(action.getPid(), action.getAnswerIndex(),action.isCorrect());
            
            RpcData rpcData = new RpcData();
            rpcData.putData("complete", "true");
            
            return rpcData;
        } catch (Exception e) {
            throw new CmRpcException(e);
        }
        finally {
            SqlUtilities.releaseResources(null,null,conn);
        }

    }
    
    

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return SaveQuizCurrentResultAction.class;
    }

}
