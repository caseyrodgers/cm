package hotmath.cm.server.rest;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher;

import com.cedarsoftware.util.io.JsonWriter;

public class ActionDispacherWrapper  {
    public ActionDispacherWrapper() {}
    
    
    public String execute(Action<?> action) throws Exception {
        String json = JsonWriter.objectToJson(ActionDispatcher.getInstance().execute(action));
        return json;
    }
}
