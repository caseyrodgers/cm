package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.ParallelProgramDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.DeleteParallelProgramAction;

import java.sql.Connection;

/** Delete Parallel Program
 * 
 * @author bob
 *
 */
public class DeleteParallelProgramCommand implements ActionHandler<DeleteParallelProgramAction, RpcData> {

    @Override
    public RpcData execute(Connection conn, DeleteParallelProgramAction action) throws Exception {
                   		
        ParallelProgramDao ppDao = ParallelProgramDao.getInstance();
        ppDao.deleteParallelProgram(action.getParallelProgId());
        
        RpcData rdata = new RpcData("status=OK");
        return rdata;
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return DeleteParallelProgramAction.class;
    }
}
