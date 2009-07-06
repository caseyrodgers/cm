package hotmath.gwt.shared.server.service;


import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.server.service.command.GetPrescriptionCommand;
import hotmath.gwt.shared.server.service.command.GetSolutionCommand;
import hotmath.gwt.shared.server.service.command.GetViewedInmhItemsCommand;

import java.util.HashMap;
import java.util.Map;

/** Implements a Command Pattern for controlling
 *  commands used by Catchup Math RPC calls.
 *  
 *  @TODO: configuration should be injected by DI
 *  
 *  
 * @author casey
 *
 */
public class ActionDispatcher {

    static private ActionDispatcher __instance;
    static public ActionDispatcher getInstance() {
        if(__instance == null)
            __instance = new ActionDispatcher();
        return __instance;
    }
    
    Map<Class<? extends Action<? extends Response>>, ActionHandler> commands = new HashMap<Class<? extends Action<? extends Response>>, ActionHandler>();
    
    
    private ActionDispatcher() {
        
        /** All commands should be injected, hard coded for now
         * 
         * Register each command availiable to RPC
         */
        GetPrescriptionCommand gc = new GetPrescriptionCommand();
        GetViewedInmhItemsCommand ac = new GetViewedInmhItemsCommand();
        GetSolutionCommand sc = new GetSolutionCommand();

        commands.put(gc.getActionType(), gc);
        commands.put(ac.getActionType(), ac);
        commands.put(sc.getActionType(), sc);
    }
    
    
    /** Execute the given command and return the proper Result object
     * 
     * @param <T>
     * @param action
     * @return
     * @throws Exception
     */
    public <T extends Response> T execute(Action<T> action) throws Exception {
        return (T) commands.get(action.getClass()).execute(action);
    }
}
