package hotmath.gwt.cm_rpc.server.service;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.CmService;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;

import hotmath.gwt.cm_rpc.client.ClientInfo;
import hotmath.cm.util.ClientInfoHolder;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import org.apache.log4j.Logger;

public class CmServiceImpl extends RemoteServiceServlet implements CmService {

    private static final long serialVersionUID = -3449687645592040570L;
    
    private static final Logger LOGGER = Logger.getLogger(CmServiceImpl.class);

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

        ClientInfo ci = ClientInfoHolder.get();
        if (ci != null) {
            LOGGER.error(String.format("*** userId: %d, userType: %s", ci.getUserId(), ci.getUserType()), t);
        }
        else {
            LOGGER.error("*** ClientInfo is NULL ***", t);
        }
    }
}
