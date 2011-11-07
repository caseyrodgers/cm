package hotmath.gwt.solution_editor.server.rpc;

import hotmath.SolutionManager;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.solution_editor.client.rpc.ServerFlusherAction;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;

import org.apache.log4j.Logger;

public class ServerFlusherCommand implements ActionHandler<ServerFlusherAction, RpcData>{

    static Logger LOGGER = Logger.getLogger(ServerFlusherCommand.class); 
    
    @Override
    public RpcData execute(Connection conn, ServerFlusherAction action) throws Exception {
        
        LOGGER.info("Flushing server: " + action);
        
        if(action.getServerToFlush() == null) {
            SolutionManager.flushCache();  // will call Flusher (TODO: move to Flusher only)
        }
        else {
            String url = "http://" + action.getServerToFlush() + "/system/hm_admin_flush_cache.jsp";
            URLConnection urlConnection = new URL(url).openConnection();
            BufferedReader in = new BufferedReader( new InputStreamReader( urlConnection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) { 
               System.out.println(inputLine);
            }
            in.close();
        }
        return new RpcData("status=OK");
    }


    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return ServerFlusherAction.class;
    }
}
