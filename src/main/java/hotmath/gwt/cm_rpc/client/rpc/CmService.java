package hotmath.gwt.cm_rpc.client.rpc;


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