package hotmath.gwt.shared.server.service.command;

import hotmath.cm.lwl.CmTutoringDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.ClearWhiteboardDataAction;

import java.sql.Connection;

import org.apache.log4j.Logger;

/** Save a single whiteboard command into database
 * 
 *  Return RpcData with single var (status == OK)
 *  
 * @author casey
 *
 */
public class SaveWhiteboardDataCommand implements ActionHandler<SaveWhiteboardDataAction, RpcData> {

    static Logger logger = Logger.getLogger(SaveWhiteboardDataCommand.class);
    
    @Override
    public RpcData execute(Connection conn, SaveWhiteboardDataAction action) throws Exception {
        RpcData rData = new RpcData();
        
        if(action.getCommandType() == CommandType.CLEAR) {
            rData = new ClearWhiteboardDataCommand().execute(conn,new ClearWhiteboardDataAction(action.getUid(), action.getRid(),action.getPid()));
        }
        else {
            new CmTutoringDao().saveWhiteboardData(conn,action.getUid(),action.getRid(),action.getPid(),action.getCommandData());
        }
        return rData;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveWhiteboardDataAction.class;
    }
}
