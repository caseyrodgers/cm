package hotmath.gwt.solution_editor.server.rpc;

import java.sql.Connection;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.solution_editor.client.rpc.SaveSolutionAdminAction;
import hotmath.gwt.solution_editor.server.CmSolutionManagerDao;

/** Not used? *
 * 
 * @author casey
 *
 */
public class SaveSolutionAdminCommand implements ActionHandler<SaveSolutionAdminAction, RpcData>{

    @Override
    public RpcData execute(Connection conn, SaveSolutionAdminAction action) throws Exception {
        new CmSolutionManagerDao().saveSolutionXml(conn, action.getPid(), action.getXml(),null,true );
        RpcData rdata = new RpcData("status=OK");
        return rdata;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return SaveSolutionAdminAction.class;
    }

}
