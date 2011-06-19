package hotmath.gwt.cm_rpc.server.service;

import hotmath.gwt.cm_rpc.client.ClientInfo;
import hotmath.gwt.cm_rpc.client.ClientInfo.UserType;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.CmService;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_rpc.server.rpc.ContextListener;

import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CmServiceImpl extends RemoteServiceServlet implements CmService {

    private static final long serialVersionUID = -3449687645592040570L;
    
    private static final Logger LOGGER = Logger.getLogger(CmServiceImpl.class);
    
    private static String unknownActionId;
    
    static {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        unknownActionId = sdf.format(ContextListener.getStartDate()) + ".UNKNOWN";
    }

    /**
     * Central processing of RPC calls. All RPC calls are one-to-one with a
     * 'command' following the Command Pattern.
     * 
     * @see hotmath.gwt.cm_rpc.client.rpc.Action
     */
    @Override
    public <T extends Response> T execute(Action<T> action) throws CmRpcException {
        return ActionDispatcher.getInstance().execute(action);
    }
    
    /**
     * Override doUnexpectedFailure() so we can log userId and userType
     * 
     */
    @Override
    public void doUnexpectedFailure(Throwable t) {
        super.doUnexpectedFailure(t);

    	ClientInfo clientInfo = ClientInfoHolder.get();
    	if (clientInfo == null) {
    		clientInfo = new ClientInfo();
    		clientInfo.setUserType(UserType.UNKNOWN);
        	clientInfo.setActionId(unknownActionId);
    		LOGGER.warn("+++ doUnexpectedFailure(): ClientInfo from ThreadLocal is NULL");
    	}

        LOGGER.info(String.format("RPC Action (userId:%d,userType:%s) (ID:%s) %s toString: %s - %s FAILED; elapsed time: %d msec",
        		clientInfo.getUserId(), clientInfo.getUserType(), unknownActionId, "GWT-RPC", t.getClass().getName(), t.getMessage(), -1));
    }
}
