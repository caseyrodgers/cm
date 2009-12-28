package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.SaveQuizCurrentResultAction;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionHandler;
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
