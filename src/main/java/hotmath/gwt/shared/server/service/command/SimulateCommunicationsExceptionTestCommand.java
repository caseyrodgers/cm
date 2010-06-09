package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.SimulateCommunicationsExceptionTestAction;

import org.apache.log4j.Logger;

import java.sql.Connection;

public class SimulateCommunicationsExceptionTestCommand implements ActionHandler<SimulateCommunicationsExceptionTestAction, RpcData>{

	private static Logger logger = Logger.getLogger(SimulateCommunicationsExceptionTestCommand.class);

    @Override
    public RpcData execute(Connection conn, SimulateCommunicationsExceptionTestAction action) throws Exception {

    	throw new SimulateCommunicationsException("Simulating a CommunicationsException");
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SimulateCommunicationsExceptionTestAction.class;
    }

    public class SimulateCommunicationsException extends Exception {
    	
    	public SimulateCommunicationsException(String msg) {
    		super(msg);
    	}
    	
    }
}


