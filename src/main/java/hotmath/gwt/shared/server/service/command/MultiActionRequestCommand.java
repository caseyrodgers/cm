package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;

/** Run all actions named in getActions and collect response objects 
 *  for a single round trip execution.
 *  
 * @author casey
 *
 */
public class MultiActionRequestCommand implements ActionHandler<MultiActionRequestAction, CmList<Response>> {

    @Override
    public CmList<Response> execute(Connection conn, MultiActionRequestAction action) throws Exception {
        CmList<Response> responses = new CmArrayList<Response>();
        for(Action<? extends Response> runAct: action.getActions()) {
            /** create a call a new instance of the real command object
             * 
             */
            Class clazz = ActionDispatcher.loadCommandClass(runAct); 
            ActionHandler actionHandler = (ActionHandler) clazz.newInstance();
            responses.add( actionHandler.execute(conn, runAct));
        }
        return responses;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return MultiActionRequestAction.class;
    }

}
