package hotmath.gwt.shared.server.service;


import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.server.service.command.GetPrescriptionCommand;
import hotmath.gwt.shared.server.service.command.GetSolutionCommand;
import hotmath.gwt.shared.server.service.command.GetViewedInmhItemsCommand;

import java.util.HashMap;
import java.util.Map;

public class ActionDispatcher {

    static private ActionDispatcher __instance;
    static public ActionDispatcher getInstance() {
        if(__instance == null)
            __instance = new ActionDispatcher();
        return __instance;
    }
    
    Map<Class<? extends Action<? extends Response>>, ActionHandler> commands = new HashMap<Class<? extends Action<? extends Response>>, ActionHandler>();
    
    
    private ActionDispatcher() {
        GetPrescriptionCommand gc = new GetPrescriptionCommand();
        GetViewedInmhItemsCommand ac = new GetViewedInmhItemsCommand();
        GetSolutionCommand sc = new GetSolutionCommand();

        commands.put(gc.getActionType(), gc);
        commands.put(ac.getActionType(), ac);
        commands.put(sc.getActionType(), sc);
    }
    
    /* Single point of entry */
    public <T extends Response> T execute(Action<T> action) throws Exception {
        return (T) commands.get(action.getClass()).execute(action);
    }
}
