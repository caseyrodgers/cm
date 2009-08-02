package hotmath.gwt.cm_tools.client.service;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.util.CmRpcException;

import com.google.gwt.user.client.rpc.RemoteService;


/** Defines central point of RPC execution, implementing
 *  Command Pattern.
 *  
 * @author casey
 *
 */
public interface CmService extends RemoteService {
    <T extends Response> T execute(Action<T> action) throws CmRpcException;
}