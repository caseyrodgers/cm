package hotmath.gwt.cm_tools.client.service;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.Response;

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