package hotmath.gwt.solution_editor.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;

/** Flush the server
 * 
 * TODO: add ability to flush specific areas
 * 
 * @author casey
 *
 */
public class ServerFlusherAction implements Action<RpcData>{
    String serverToFlush;
    
    public ServerFlusherAction() {}
    
    public ServerFlusherAction(String serverToFlush) {
        this.serverToFlush = serverToFlush;
    }

    public String getServerToFlush() {
        return serverToFlush;
    }

    public void setServerToFlush(String serverToFlush) {
        this.serverToFlush = serverToFlush;
    }

    @Override
    public String toString() {
        return "ServerFlusherAction [serverToFlush=" + serverToFlush + "]";
    }
}
