package hotmath.gwt.cm_rpc.server.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.CmService;
import hotmath.gwt.cm_rpc.client.rpc.Response;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CmServiceImpl extends RemoteServiceServlet implements CmService {

    /**
     * 
     */
    private static final long serialVersionUID = -3449687645592040570L;

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
}
