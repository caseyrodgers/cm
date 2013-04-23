package hotmath.gwt.cm_rpc_core.server.service;



import hotmath.gwt.cm_rpc_core.client.ClientInfo;
import hotmath.gwt.cm_rpc_core.client.ClientInfo.UserType;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc_core.client.rpc.CmService;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_rpc_core.server.rpc.ContextListener;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletResponse;

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
    
    
    /** Override to force expires/headers to deal with IOS caching Post requests
     * 
     *  see:
     *  https://groups.google.com/forum/?fromgroups=#!topic/google-web-toolkit/CWkgCXLi8tA
     * 
     */
    @Override
    protected void onAfterResponseSerialized(String serializedResponse) {
        HttpServletResponse response = getThreadLocalResponse();
        response.setDateHeader("Expires", 0L);  // always expired
        response.setHeader("Cache-Control", "no-cache");
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
