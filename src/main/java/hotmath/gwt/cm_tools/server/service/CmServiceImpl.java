package hotmath.gwt.cm_tools.server.service;

import hotmath.gwt.cm_tools.client.service.CmService;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.server.service.ActionDispatcher;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CmServiceImpl extends RemoteServiceServlet implements CmService {

    /**
     * 
     */
    private static final long serialVersionUID = -3449687645592040570L;

    /** Central processing of RPC calls.  All RPC calls are one-to-one
     * with a 'command' following the Command Pattern.
     * 
     * @see hotmath.gwt.shared.client.rpc.Action
     */
    @Override
    public <T extends Response> T execute(Action<T> action) throws CmRpcException {
        return ActionDispatcher.getInstance().execute(action);
    }
}
