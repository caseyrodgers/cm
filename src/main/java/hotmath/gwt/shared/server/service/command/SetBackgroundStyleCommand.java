package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.SetBackgroundStyleAction;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;

/** Set the users background style name
 * 
 * @author casey
 *
 */
public class SetBackgroundStyleCommand implements ActionHandler<SetBackgroundStyleAction, RpcData>{

    @Override
    public RpcData execute(Connection conn, SetBackgroundStyleAction action) throws Exception {
        try {
            conn = HMConnectionPool.getConnection();
            CmStudentDao.getInstance().setBackgroundStyle(conn, action.getUid(),action.getStyleName());
            
            RpcData rData = new RpcData("status=OK");
            return rData;
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
        return SetBackgroundStyleAction.class;
    }

}
