package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveQuizCurrentResultAction;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;



/** Save the a single Quiz question result
 * 
 * @author casey
 *
 */
public class SaveQuizCurrentResultCommand implements ActionHandler<SaveQuizCurrentResultAction, RpcData> {

    @Override
    public RpcData execute(final Connection conn, SaveQuizCurrentResultAction action) throws CmRpcException {
        try {
            HaTest test = HaTestDao.loadTest(conn,action.getTestId());
            HaTestDao.saveTestQuestionChange(conn,test.getTestId(), action.getPid(), action.getAnswerIndex(),action.isCorrect());
            
            RpcData rpcData = new RpcData();
            rpcData.putData("complete", "true");
            
            return rpcData;
        } catch (Exception e) {
            throw new CmRpcException(e);
        }
        finally {
            SqlUtilities.releaseResources(null,null,null);
        }

    }
    
    

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return SaveQuizCurrentResultAction.class;
    }

}
